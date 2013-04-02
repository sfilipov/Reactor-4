package components;

import java.util.Random;

public abstract class RandomlyFailableComponent extends PlantComponent {
	private static final long serialVersionUID = 3981519622605741840L;
	
	//The values of the constants below are for illustration.
	public final static int DEFAULT_FAILURE_RATE = 10; //1%
	public final static int DEFAULT_REPAIR_TIME = 5; 
	public final static int MAX_FAILURE_RATE = 50; //5%
	public final static boolean DEFAULT_OPERATIONAL = true;
	
	private int failureRate;
	private int maxFailureRate;
	private int repairTime;
	private boolean operational; //Possibly unnecessary
	private Random random;
	
	protected RandomlyFailableComponent() {
		super();
		this.failureRate    = DEFAULT_FAILURE_RATE;
		this.maxFailureRate = MAX_FAILURE_RATE;
		this.repairTime     = DEFAULT_REPAIR_TIME;
		this.operational    = DEFAULT_OPERATIONAL;
		this.random = new Random();
	}
	
	protected RandomlyFailableComponent(int failureRate, int repairTime, int maxFailureRate) {
		super();
		this.failureRate    = failureRate;
		this.repairTime     = repairTime;
		this.maxFailureRate = maxFailureRate;
		this.operational = DEFAULT_OPERATIONAL;
		random = new Random();
	}
	
	protected RandomlyFailableComponent(int failureRate, int repairTime, boolean operational, boolean pressurised) {
		super(pressurised);
		this.failureRate = failureRate;
		this.repairTime = repairTime;
		this.operational = operational;
		random = new Random();
	}
	
	/**
	 * 
	 * @return the current chance of the component failing randomly
	 */
	public int getFailureRate() {
		return failureRate;
	}
	
	
	/**
	 * Sets a new value for failureRate.
	 * 
	 * @param failureRate the new value for failureRate
	 */
	protected void setFailureRate(int failureRate) {
		this.failureRate = failureRate;
	}
	
	/**
	 * 
	 * @return the number of turns that it takes for a component to repair
	 */	
	public int getRepairTime() {
		return repairTime;
	}
	
	/**
	 * Changes the number of turns it take for a component to get repaired.
	 * 
	 * @param repairTime the number of turns required for this component 
	 * 					 to be repaired
	 */
	protected void setRepairTime(int repairTime) {
		this.repairTime = repairTime;
	}

	/**
	 * 
	 * @return true if the component is operational (working correctly)
	 */
	public boolean isOperational() {
		return operational;
	}
	
	/**
	 * Sets the condition of the component - true if it is
	 * working correctly, false if it has failed.
	 * 
	 * @param operational the new state of the component
	 */
	public void setOperational(boolean operational) {
		this.operational = operational;
	}
	
	/**
	 * Runs all checks for the component and changes it's operational state if needed. 
	 */
	public boolean hasFailed() {
		int checkFailure = random.nextInt(1000);
		if(failureRate > checkFailure) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Increases the component's chance to fail by 0.1% per call.
	 * 
	 * Doesn't increase the chance to fail once MAX_FAILURE_RATE is reached.
	 */
	public void increaseFailureRate() {
		int currentFailureRate = this.getFailureRate();
		if (currentFailureRate < maxFailureRate) {
			this.setFailureRate(++currentFailureRate);
		}
	}
}
