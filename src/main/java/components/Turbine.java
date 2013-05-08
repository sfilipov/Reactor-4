package components;

/**
 * The turbine is a class that makes the generator create power output.
 * The RPM is created based on the steam flow in. The turbine has a chance
 * to fail randomly.
 * 
 * @author Lamprey
 */
public class Turbine extends RandomlyFailableComponent implements UpdatableComponent, ForcedFailableComponent {
	private static final long serialVersionUID = 1106025692966179166L;

	public final static int DEFAULT_FAILURE_RATE = 10; //1%
	public final static int DEFAULT_REPAIR_TIME = 5;
	private final static int DEFAULT_STEPS_UNTIL_FORCE_FAILABLE = 40;
	private final static int MAX_FAILURE_RATE = 25;
	private static final int MAX_TURBINE_RPM = 3500;
	
	private int rpm;
	private int maxSteamThroughput;
	private int stepsUntilForceFailable;
	
	/**
	 * 
	 * @param maxSteamThroughput  the maximum amount of steam that may enter the turbine
	 */
	public Turbine(int maxSteamThroughput) {
		super(DEFAULT_FAILURE_RATE, DEFAULT_REPAIR_TIME, MAX_FAILURE_RATE);
		this.maxSteamThroughput = maxSteamThroughput;
		this.rpm = 0;
	}
	
	/**
	 * Update the state of the Turbine.
	 * 
	 * Calculates the new value of RPM based on the steam flow in. It also 
	 * increases the failure rate if appropriate.
	 */
	@Override
	public void updateState() {
		int steamFlowIn = this.getInput().getFlowOut().getRate();
		// Need to create a couple of new doubles mid-calc here to make sure we get a decimal
		double linearMultiplier = 1 - (new Double((this.maxSteamThroughput - steamFlowIn))/new Double(this.maxSteamThroughput)); 
		int newRpm = (int) Math.round(new Double(MAX_TURBINE_RPM) * linearMultiplier);
		this.rpm = (this.isOperational()) ? newRpm : 0;
		
		increaseFailureRate();
		
		if (stepsUntilForceFailable > 0) {
			stepsUntilForceFailable--;
		}
	}
	
	/**
	 * Used only in Generator.
	 * 
	 * @return the value of rpm
	 */
	public int getRpm() {
		return (this.isOperational()) ? rpm : 0;
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
}