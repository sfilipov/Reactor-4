package components;

/**
 * Pump is a plant component that pumps some amount of water based on pump's "on"
 * state and its RPM. There is a chance that a pump fails randomly. 
 * 
 * @author Lamprey
 */
public class Pump extends RandomlyFailableComponent implements ForcedFailableComponent, UpdatableComponent {

	private static final long serialVersionUID = -446684199807618671L;
	
	//DEFAULT_RPM is used when a rpm argument is not passed to the constructor
	private final static int DEFAULT_RPM = 0;
	private final static boolean DEFAULT_ON_STATE = true;
	private final static int MAX_RPM = 1000; 
	private final static int DEFAULT_FAILURE_RATE = 10; //1%
	private final static int DEFAULT_REPAIR_TIME = 5;
	private final static int DEFAULT_STEPS_UNTIL_FORCE_FAILABLE = 10;
	private final static int MAX_FAILURE_RATE = 50; //5%
	// the maximum flow rate for a pump when it is on and not broken
	private final static int MAX_WATER_FLOW_RATE_PER_PUMP = 400;

	private int ID;
	private int rpm;
	private int stepsUntilForceFailable;
	
	/**
	 * Constructs a pump with the selected ID.
	 * 
	 * @param ID the selected ID for the pump.
	 */
	public Pump(int ID) {
		super(DEFAULT_FAILURE_RATE, DEFAULT_REPAIR_TIME, MAX_FAILURE_RATE);
		this.ID = ID;
		setRpm(DEFAULT_RPM);
		stepsUntilForceFailable = 0;
	}
	
	/**
	 * 
	 * @return the ID of this pump.
	 */
	public int getID()
	{
		return ID;
	}

	/**
	 * Note: If the pump is off, the RPM returned will be zero.
	 * 		 If the pump is broken, the RPM returned will also be zero.
	 * 
	 * @return the current RPM value of the pump
	 */
	public int getRpm() {
		return (!this.isOperational()) ? 0 : rpm;
	}
	
	/**
	 * Sets a new value for the RPM of the pump
	 * 
	 * @param rpm the new value
	 */
	public void setRpm(int rpm) throws IllegalArgumentException {
		if (rpm <= MAX_RPM && rpm >= 0) {
			this.rpm = rpm;
		} else {
			throw new IllegalArgumentException("Pump rpm must be in the range [0 - " + MAX_RPM + "]");
		}
	}
	
	/**
	 * 
	 * @return the max RPM of the pump.
	 */
	public int getMaxRpm()
	{
		return MAX_RPM;
	}
	
	/**
	 * Update the state of the pump.
	 * 
	 * Increases the failure rate of the pump if appropriate.
	 */
	@Override
	public void increaseFailureRate() {
		super.increaseFailureRate();
	}
	
	/**
	 * Checks if the pump fails randomly.
	 * 
	 * @return true if the pump has failed
	 */
	public boolean hasFailed() {
		return super.hasFailed();
	}
	
	/**
	 * 
	 * @return maximum rate at which water can flow through a pump
	 */
	public static int getMaxWaterFlowRatePerPump()
	{
		return MAX_WATER_FLOW_RATE_PER_PUMP;
	}
	
	@Override
	public void setOperational(boolean operational) {
		super.setOperational(operational);
		if (operational == false) {
			stepsUntilForceFailable = DEFAULT_STEPS_UNTIL_FORCE_FAILABLE;
		}
	}

	@Override
	public int numStepsUntilFailable() {
		return stepsUntilForceFailable;
	}

	@Override
	public boolean isForceFailable() {
		return (stepsUntilForceFailable == 0) ? true : false;
	}

	@Override
	public void updateState() {
		if (stepsUntilForceFailable > 0) {
			stepsUntilForceFailable--;
		}
	}
}
