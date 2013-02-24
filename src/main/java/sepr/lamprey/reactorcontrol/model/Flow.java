package sepr.lamprey.reactorcontrol.model;

import java.io.Serializable;


/**
 * The Flow class stores information about the flow at a certain point in 
 * the system. Every PlantComponent has one Flow variable that represents
 * the flow OUT of that variable. i.e. towards the PlantComponent connected
 * to it's output.
 * 
 * We store the following information:
 * 		- Rate 			- int The amount of water/steam flowing through this
 * 							point in 1 time-step.
 * 		- Temperature 	- int The temperature of the water/steam flowing through
 *							this point.
 * 		- Type 			- enum FlowType = {Water, Steam}
 * 
 * @author Lamprey
 * @author Vel
 */
public class Flow implements Serializable {
	
	private static final long serialVersionUID = 8001114698646828534L;
	
	private final static int DEFAULT_RATE = 0;
	private final static FlowType DEFAULT_TYPE = FlowType.Water;
	private final static int DEFAULT_TEMPERATURE = 0;
	
	private int rate;
	private FlowType type;
	private int temperature;

	/**
	 * The default constructor uses the predefined values
	 */
	public Flow() {
		this.rate = DEFAULT_RATE;
		this.type = DEFAULT_TYPE;
		this.temperature = DEFAULT_TEMPERATURE;
	}
	
	 /**
     * This constructor uses the predefined values for
     * temperature and rate but takes the type as an input
     * 
     * @param type   type of flow - either steam or water
     */
	public Flow(FlowType type) {
		this.rate = DEFAULT_RATE;
		this.temperature = DEFAULT_TEMPERATURE;
		this.type = type;
	}
	
    /**
     * This constructor uses the predefined value for
     * temperature but takes the type and the rate as inputs
     * 
     * @param type    type of flow - either steam or water
     * @param rate    rate of flow 
     */
	public Flow(FlowType type, int rate) {
		this.rate = rate;
		this.temperature = DEFAULT_TEMPERATURE;
		this.type = type;
	}
	
    /**
     * This constructor uses none of the predefined values
     * 
     * @param type         type of flow - either steam or water
     * @param rate         rate of flow
     * @param temperature  temperature of the water/steam
     */
	public Flow(FlowType type, int rate, int temperature) {
		this.rate = rate;
		this.temperature = temperature;
		this.type = type;
	}
	
	/**
	 * 
	 * @return rate of flow
	 */
	public int getRate() {
		return rate;
	}

	/**
	 * 
	 * @param rate  rate of flow
	 */
	public void setRate(int rate) {
		if(rate>=0)
	     this.rate = rate;
	}

	/**
	 * 
	 * @return type of flow - water/steam 
	 */
	public FlowType getType() {
		return type;
	}
	
	/**
	 * 
	 * @param type   type of flow - either water or steam
	 */
	public void setType(FlowType type) {
		this.type = type;
	}
	
	/**
	 * 
	 * @return temperature of the water/steam
	 */
	public int getTemperature()
	{
		return temperature;
	}

	/**
	 * 
	 * @param temperature  temperature of the water/steam
	 */
	public void setTemperature(int temperature)
	{
	    if(temperature>=0)
		this.temperature = temperature;
	}

}
