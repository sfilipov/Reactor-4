package model;

import java.io.Serializable;

import components.FailableComponent;
import components.PlantComponent;



/**
 * Repair class keeps track of the components that are being repaired.
 * When a component's timeStepsRemaining reach 0, the component needs to be
 * removed from the beingRepaired list inside Plant.
 * 
 * @author Lamprey
 * @author Vel
 */
public class Repair implements Serializable {
	private static final long serialVersionUID = 1819944421888642516L;
	
	private FailableComponent failableComponent;
	private int timeStepsRemaining;
	
	/**
	 * 
	 * @param componentToRepair component that needs repairing 
	 */
	public Repair (FailableComponent componentToRepair) {
		this.failableComponent  = componentToRepair;
		this.timeStepsRemaining = componentToRepair.getRepairTime();
	}
	
	/**
	 * Decrements the time remaining until the component is repaired 
	 */
	public void decTimeStepsRemaining() {
		if(timeStepsRemaining>0)
		    timeStepsRemaining--;
		else
		    timeStepsRemaining = 0;
	}
	
	/**
	 * 
	 * @return number of time steps remaining
	 */
	public int getTimeStepsRemaining() {
		return timeStepsRemaining;
	}
	
	/**
	 * 
	 * @return plant component 
	 */
	public FailableComponent getPlantComponent() {
		return failableComponent;
	}
	
}