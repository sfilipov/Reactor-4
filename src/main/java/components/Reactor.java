package components;

import java.io.Serializable;

import model.Flow;
import model.FlowType;




/**
 * A reactor is an object that controls the nuclear reaction
 * inside the plant. Based on the percentage of lowering of
 * the control rods, the water inside the reactor is heated
 * up. When a particular temperature is reached, some amount
 * of water is converted to steam which goes out of the reactor.
 * 
 * Reactor's health gets lower when the max temperature or pressure
 * is exceeded. When a reactor's health is 0 or lower, the game is over.
 * 
 * @author Lamprey
 */
public class Reactor extends CriticalComponent implements UpdatableComponent {
	private static final long serialVersionUID = 2901479494890681361L;
	
	private final static int DEFAULT_WATER_VOLUME = 8000;
	
	private final static int MAX_TEMPERATURE = 2865; // 2865C is the melting point of uranium oxide.
	private final static int MAX_PRESSURE = 2000;
	private final static int MAX_HEALTH = 100;
	private final static int MAX_HEATING_PER_STEP = 100; // in degrees C. maximum amount to increase temp by in a step. 
	private final static int MIN_SAFE_WATER_VOLUME = 2000;
	private final static int UNSAFE_HEATING_MULTIPLIER = 2; // amount to increase 
	private final static int WATER_STEAM_RATIO = 2; // 1:2 water to steam
	private final static int HEALTH_CHANGE_WHEN_DAMAGING = 10;
	private final static double EVAP_MULTIPLIER = 0.2; // conversion from temperature to amount evaporated. 
	private final static double VOL_TO_PRESSURE_MULTIPLIER = 0.15;
	private final static int BOILING_POINT = 285; // boiling point of water at 1000psi - no variable boiling point.
	
	private ControlRod controlRod;
	private int waterPumpedIn;
	
	public Reactor() {
		super(DEFAULT_WATER_VOLUME);
		this.controlRod = new ControlRod();
		this.getFlowOut().setType(FlowType.Steam);
	}
	
	// ----------- Getters & Setters ---------------
	
	public int getMaxTemperature() {
		return MAX_TEMPERATURE;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMaxPressure() {
		return MAX_PRESSURE;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMinSafeWaterVolume() {
		return MIN_SAFE_WATER_VOLUME;
	}
	
	/**
	 * Updates the amount of water in the reactor.
	 * Also stores the amount of water pumped in for future calculations.
	 * This method should only be called once per timeStep.
	 * 
	 * @param pumpedInVolume amount of water to add to the total in the reactor
	 */
	public void pumpInWater(int pumpedInVolume) throws IllegalArgumentException {
		if (pumpedInVolume < 0) {
			throw new IllegalArgumentException("The volume of the water pumped in cannot be negative.");
		}
		else {
			this.waterPumpedIn = pumpedInVolume; // allows for only 1 call per step.
			setWaterVolume(getWaterVolume() + pumpedInVolume);
		}
	}

	/**
	 * Updates the amount of steam in the reactor.
	 * 
	 * amount can be negative and will be when steam is leaving 
	 * the reactor.
	 *  
	 * @param amount the amount of steam to add to the volume
	 */
	public void addSteamVolume(int amount) {
		setSteamVolume(getSteamVolume() + amount);
	}

	
	/**
	 * 
	 * @return rod level
	 */
	public int getPercentageLowered() {
		return controlRod.getPercentageLowered();
	}
	
	/**
	 * 
	 * @param percentageLowered the new rod level
	 */
	public void setPercentageLowered(int percentageLowered) {
		controlRod.setPercentageLowered(percentageLowered);
	}
	
	// ---------------- System update methods ---------------
	/**
	 * Updates the state of the reactor.
	 * 
	 * Updates the temperature, pressure, evaporates some water if
	 * appropriate and check if the reactor is damaged based on
	 * it's internal temperature and pressure.
	 */
	@Override
	public void updateState() {
		updateTemperature();
		updatePressure();
		evaporateWater();
		checkIfDamaging();
	}
	
	/**
	 * Updates the temperature inside the reactor.
	 * 
	 * Change the temperature based on the heat produced by nuclear
	 * reaction and the cooling based on the water pump in from
	 * the condenser.
	 */
	private void updateTemperature() {
		int changeInTemp;
		Flow flowIn = this.getInput().getFlowOut();
		int waterTemperature = flowIn.getTemperature();
		
		changeInTemp = heating(controlRod.getPercentageLowered()) - cooldown(waterTemperature, this.waterPumpedIn);
		setTemperature(getTemperature() + changeInTemp);
	}
	
	/**
	 * Updates the pressure inside the reactor.
	 * 
	 * It depends on the amount of steam that is currently inside the reactor.
	 */
	private void updatePressure() {
		int currentPressure;
		currentPressure = (int) Math.round(new Double(getSteamVolume()) * VOL_TO_PRESSURE_MULTIPLIER);
		setPressure(currentPressure);
	}
	
	/**
	 * Calculates the amount of cooldown in the reactor for this
	 * time step. Dependent upon the temperature and volume of water being
	 * pumped into the reactor.
	 * 
	 * @param waterTemperature temperature of the water being pumped into the reactor
	 * @param pumpedIn amount of water pumped in since the last timeStep
	 * @return how much to reduce the temperature by 
	 */
	private int cooldown(int waterTemperature, int pumpedIn) {
		int waterInTempDiff = getTemperature() - waterTemperature; 
		if (getWaterVolume() < 1) return 0; // stops a potential divide by 0 on the next line.
		return (int) Math.round(waterInTempDiff * (1 - (new Double(getWaterVolume() - pumpedIn)/ getWaterVolume() )));
		
	}
	
	/**
	 * Calculates the amount of heating in the reactor for this time step.
	 * Depends upon how far the control rods are lowered.
	 * 
	 * If there is less than the minimum safe amount of water in the reactor,
	 * the control rods will heat up much more quickly.
	 * (The maximum heating amount is multiplied by UNSAFE_HEATING_MULTIPLIER) 
	 * 
	 * @param loweredPercentage percentage the control rods are lowered
	 * @return how much to the increase the temperature by
	 */
	private int heating(int loweredPercentage) {
		if (getWaterVolume() <= MIN_SAFE_WATER_VOLUME) {
			return (int) Math.round((MAX_HEATING_PER_STEP * UNSAFE_HEATING_MULTIPLIER) 
									* (1 - percentageToDecimal(loweredPercentage)));
		} else {
			return (int) Math.round(MAX_HEATING_PER_STEP * (1 - percentageToDecimal(loweredPercentage)));
		}
	}
	
	/**
	 * Does what it says on the tin.
	 * Assumes input is a valid percentage (i.e. not negative)
	 * 
	 * @param percentage percentage to convert to a decimal
	 * @return percentage as a decimal.
	 */
	private double percentageToDecimal(int percentage) {
		return new Double(percentage) / 100;
	}
	
	/**
	 * Calculates how much water to boil off and updated the volumes of water
	 * and steam as necessary.
	 */
	private void evaporateWater() {
		int waterEvaporated;
		int steamCreated;
		// Don't evaporate anything if the reactor is not above boiling point.
		if (getTemperature() > BOILING_POINT) {
			// I don't like this hacky cast but ah well.
			waterEvaporated = (int) Math.round(getTemperature() * EVAP_MULTIPLIER);
			if (waterEvaporated > getWaterVolume()) waterEvaporated = getWaterVolume();
			steamCreated = waterEvaporated * WATER_STEAM_RATIO;
		
			setWaterVolume(getWaterVolume() - waterEvaporated);
			setSteamVolume(getSteamVolume() + steamCreated);
		}
	}
	
	/**
	 * Damages the reactor if temperature
	 * and/or pressure is higher than the max
	 * temperature and max pressure. Or if water
	 * level is below the safe volume. 
	 */
	private void checkIfDamaging() {
		if(getTemperature() > MAX_TEMPERATURE) {
			damageReactor();					
		}
		if (getPressure() > MAX_PRESSURE) {
			damageReactor();					
		}
		if(getWaterVolume() < MIN_SAFE_WATER_VOLUME){
			damageReactor();
		}
		
	}
	
	/**
	 * Damages the reactor by amount of HEALT_CHANGE_WHEN_DAMAGING.
	 */
	private void damageReactor() {
		setHealth(getHealth() - HEALTH_CHANGE_WHEN_DAMAGING);
	}
	
	/**
	 * 
	 * @return true if health is 0 or lower.
	 */
//	@Override
	public boolean hasFailed() {
		if (getHealth() <= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Control rods class is internal to Reactor. It has a field
	 * that keeps track of how lowered are the control rods.
	 * 
	 * @author Lamprey
	 */
	private final class ControlRod implements Serializable {
		private static final long serialVersionUID = 9216989049507879933L;
		private final static int DEFAULT_PERCENTAGE = 100;
		private int percentageLowered;
		
		ControlRod() {
			setPercentageLowered(DEFAULT_PERCENTAGE);
		}
		
		/**
		 * 
		 * @return rod level
		 */
		int getPercentageLowered() {
			return percentageLowered;
		}
		
		/**
		 * 
		 * @param percentageLowered new rod level
		 */
		void setPercentageLowered(int percentageLowered) {
			if (percentageLowered < 0 || percentageLowered > 100) {
				throw new IllegalArgumentException("Reactor: ControlRod: " +
								"percentageLowered not in range [0..100].");
			}
			this.percentageLowered = percentageLowered;
		}
	}
}
