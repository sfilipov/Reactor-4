package model;

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
