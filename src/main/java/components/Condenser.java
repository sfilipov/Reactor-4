package components;

import model.Flow;


/**
 * Condenser class is a plant component that takes steam as an input and converts some
 * of the steam inside it to water. It's health goes down when the internal 
 * temperature and/or pressure's value is higher than the maximum one. If the health
 * is 0 or lower, the game is over.
 * 
 * @author Lamprey
 */
public class Condenser extends CriticalComponent implements UpdatableComponent {
	private static final long serialVersionUID = 4348915919668272156L;

	private final static int DEFAULT_WATER_VOLUME = 2000;
	
	private final static int MAX_TEMPERATURE = 2000;
	private final static int MAX_PRESSURE = 2000;
	private final static int COOLANT_TEMP = 20; // temperature of the coolant coming in
	private final static int MAX_COOLDOWN_PER_STEP = 500; // Maximum amount to cool the condenser per step. 
	private final static int WATER_STEAM_RATIO = 2; // water to steam ratio.
	private final static int HEALTH_CHANGE_WHEN_DAMAGING = 5;
	private final static double COND_MULTIPLIER = 2; // temperature to steam condensed multiplier.
	private final static double VOL_TO_PRESSURE_MULTIPLIER = 0.15;
	
	private int steamIn;
	private Pump coolantPump;
	
	
	/**
	 * Constructor for Condenser.
	 * 
	 * The created Condenser never fails randomly and is pressured.
	 * 
	 * @param coolantPump   the pump which is used to cool the condenser
	 */
	public Condenser(Pump coolantPump) {
		super(DEFAULT_WATER_VOLUME);
		this.coolantPump = coolantPump;
	}

	// ----------- Getters & Setters ---------------
	
	/**
	 * 
	 * @return the max temperature of the condenser.
	 */
	public int getMaxTemperature() {
		return MAX_TEMPERATURE;
	}
	
	/**
	 * 
	 * @return the max pressure of the condenser.
	 */
	public int getMaxPressure() {
		return MAX_PRESSURE;
	}
	
	/**
	 * Updates the amount of water in the condenser.
	 * 
	 * @param pumpedOutVolume amount of water to add to the total in the condenser
	 */
	public void pumpOutWater(int pumpedOutVolume) throws IllegalArgumentException {
		if (pumpedOutVolume < 0) {
			throw new IllegalArgumentException("The volume of the water pumped out cannot be negative.");
		}
		else {
			setWaterVolume(getWaterVolume() - pumpedOutVolume);
		}
	}

	/**
	 * Updates the amount of steam in the condenser.
	 * Also stores the amount of steam into the condenser for this step and 
	 * as such should not be called more than once per step.
	 * 
	 * amount can be negative and will be when steam is condensed into water.
	 *  
	 * @param amount the amount of steam to add to the volume
	 */
	public void updateSteamVolume(int amount)
	{
		this.steamIn = amount;
		setSteamVolume(getSteamVolume() + amount);
	}

	public Pump getCoolantPump() {
		return coolantPump;
	}
	
	/**
	 * Updates the state of the condenser.
	 * 
	 * Updates the temperature, condenses some steam to water,
	 * updates the pressure.
	 */
	@Override
	public void updateState() {
		updateTemperature();
		condenseSteam();
		updatePressure();
	}

	/**
	 * Updates the temperature.
	 * 
	 * Calculates a new value for the temperature based on
	 * heating because of steam flowing in the condenser and
	 * cooling.
	 */
	private void updateTemperature() {
		int changeInTemp;
		Flow flowIn = this.getInput().getFlowOut();
		int steamTemperature = flowIn.getTemperature();
		
		changeInTemp = heating(steamTemperature, this.steamIn) - cooldown();
		setTemperature(getTemperature() + changeInTemp);
	}
	
	/**
	 * Updates the pressure.
	 * 
	 * Calculates a new value for the pressure.
	 */
	private void updatePressure() {
		int currentPressure;
		currentPressure = (int) Math.round(new Double(getSteamVolume()) * VOL_TO_PRESSURE_MULTIPLIER);
		setPressure(currentPressure);
	}
	
	/**
	 * Calculates the increase in temperature based upon the temperature and volume
	 * of steam coming into the condenser.
	 * 
	 * @param steamTemperature temperature of steam coming into the condenser
	 * @param steamVolumeIn amount of steam that has come into the condenser in the last step
	 * @return temperature increase for this step
	 */
	private int heating(int steamTemperature, int steamVolumeIn) {
		int tempDiff = steamTemperature - getTemperature();
		if (getSteamVolume() < 1) return 0; // stops a potential divide by 0.
		if (steamVolumeIn == 0) return 0; // No steam flowing in => no heating.
		return tempDiff * (1 - ((getSteamVolume() - steamVolumeIn) / getSteamVolume()));
	}
	
	/**
	 * Returns COOLDOWN_PER_STEP constant as the pump pumping coolant
	 * into the condenser is always on full.
	 * Will obviously not try to cool the condenser past the temperature of 
	 * the coolant.
	 * 
	 * @return amount of temperature decrease for this step
	 */
	private int cooldown() {
		int cooldownAmount = cooldownPerStep();
		int potentialNewTemp = getTemperature() - cooldownAmount;
		if (potentialNewTemp > COOLANT_TEMP) {
			return cooldownAmount;
		} else {
			return getTemperature() - COOLANT_TEMP;
		}
	}
	
	/**
	 * 
	 * @return 
	 */
	private int cooldownPerStep() {
		int maxRpm = coolantPump.getMaxRpm();
		int currRpm = coolantPump.getRpm();
		return (int) Math.round(MAX_COOLDOWN_PER_STEP * new Double(currRpm)/maxRpm);
	}

	/**
	 * Not very physics accurate, but it provides a reasonable model of 
	 * the behaviour of steam condensing.
	 */
	private void condenseSteam() {
		int steamCondensed;
		int waterCreated;
		if (getTemperature() < MAX_TEMPERATURE) {
			steamCondensed = (int) Math.ceil((MAX_TEMPERATURE - getTemperature()) * COND_MULTIPLIER);
		} else {
			steamCondensed = 0;
		}
		
		if (steamCondensed > getSteamVolume()) steamCondensed = getSteamVolume();
		waterCreated = (int) Math.ceil(steamCondensed * (1 / new Double(WATER_STEAM_RATIO)));
		/*
		 * Since we do a dodgy division above, to make sure we aren't losing / creating
		 * water we remultiply out the waterCreated.
		 */
		steamCondensed = waterCreated * WATER_STEAM_RATIO;	
		setSteamVolume(getSteamVolume() - steamCondensed); // made negative as the water is removed.
		setWaterVolume(getWaterVolume() + waterCreated);
	}
	
	/**
	 * Updates the health of the condenser.
	 * 
	 * Lowers the health of the condenser if appropriate and
	 * throws GameOverException once the health is less 
	 * than or equal to 0.
	 */
	@Override
	public void updateHealth() throws GameOverException {
		checkIfDamaging();
		checkGameOver();
	}

	/**
	 * Damages the condenser if appropriate.
	 * 
	 * Damages the condenser if the current temperature is above
	 * the max temperature and if the current pressure is
	 * above the max pressure.
	 */
	private void checkIfDamaging() {
		if(getTemperature() >= MAX_TEMPERATURE) {
			damageCondenser(HEALTH_CHANGE_WHEN_DAMAGING);
		}
		if(getPressure() >= MAX_PRESSURE) {
			damageCondenser(HEALTH_CHANGE_WHEN_DAMAGING);
		}
	}
	
	/**
	 * Damages the condenser.
	 * 
	 * Lowers the health of the condenser by damageAmount.
	 * @param damageAmount the amount to be subtracted from condenser's health.
	 */
	private void damageCondenser(int damageAmount) {
		setHealth(getHealth() - damageAmount);
	}
	
	private void checkGameOver() throws GameOverException {
		if (getHealth() <= 0) {
			throw new GameOverException("The health of the condenser is below 0!");
		}
	}
}
