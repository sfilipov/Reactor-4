package simulator;

import java.util.ArrayList;
import java.util.List;

import components.PlantComponent;
import components.Pump;
import components.Valve;


import model.Plant;



/**
 * UIData holds all data necessary for a UI (both text and graphical based). It provides methods 
 * for updating all fields with the latest information and getting that information.
 * 
 * @author Lamprey 
 */
public class UIData {
	private Plant plant;
	
	private String  operatorName;
	private int     score;
	private boolean gameOver;
	private boolean multiplayer;
	
	private int reactorHealth;
	private int reactorTemperature;
	private int reactorMaxTemperature;
	private int reactorPressure;
	private int reactorMaxPressure;
	private int reactorWaterVolume;
	private int reactorMinSafeWaterVolume;
	
	private int condenserHealth;
	private int condenserTemperature;
	private int condenserMaxTemperature;
	private int condenserPressure;
	private int condenserMaxPressure;
	private int condenserWaterVolume;
	
	private int turbineRpm;
	private boolean turbineFunctional;
	private boolean operatingSoftwareFunctional;
	private int powerOutput;
	
	private int controlRods;
	
	private List<Valve> valves;
	private List<Pump>  pumps;
	
	private List<PlantComponent> brokenOnStep;
	
	UIData(Plant plant) {
		this.plant = plant;
		
		this.operatorName = plant.getOperatorName();
		this.score        = plant.getScore();
		this.gameOver     = plant.isGameOver();
		this.multiplayer  = plant.isMultiplayer();
		
		this.turbineRpm			= plant.getTurbine().getRpm();
		this.turbineFunctional	= plant.getTurbine().isOperational();
		this.operatingSoftwareFunctional = plant.getOperatingSoftware().isOperational();
		this.powerOutput  		= plant.getGenerator().getPowerOutput();
		
		this.reactorHealth             = plant.getReactor().getHealth();
		this.reactorTemperature        = plant.getReactor().getTemperature();
		this.reactorMaxTemperature     = plant.getReactor().getMaxTemperature();
		this.reactorPressure           = plant.getReactor().getPressure();
		this.reactorMaxPressure        = plant.getReactor().getMaxPressure();
		this.reactorWaterVolume        = plant.getReactor().getWaterVolume();
		this.reactorMinSafeWaterVolume = plant.getReactor().getMinSafeWaterVolume();
		
		this.condenserHealth         = plant.getCondenser().getHealth();
	    this.condenserTemperature    = plant.getCondenser().getTemperature();
	    this.condenserMaxTemperature = plant.getCondenser().getMaxTemperature();
	    this.condenserPressure       = plant.getCondenser().getPressure();
	    this.condenserMaxPressure    = plant.getCondenser().getMaxPressure();
	    this.condenserWaterVolume    = plant.getCondenser().getWaterVolume();
	    
	    this.controlRods = plant.getReactor().getPercentageLowered();
	    
	    this.valves = plant.getValves();
	    this.pumps  = plant.getPumps();
	    
	    this.brokenOnStep = new ArrayList<PlantComponent>();
	}
	
	/**
	 * Updates all necessary information for its internal fields from Plant.
	 */
	public void updateUIData() {
		this.operatorName = plant.getOperatorName();
		this.score        = plant.getScore();
		this.gameOver     = plant.isGameOver();
		this.multiplayer  = plant.isMultiplayer();
		
		this.turbineRpm			= plant.getTurbine().getRpm();
		this.turbineFunctional	= plant.getTurbine().isOperational();
		this.operatingSoftwareFunctional = plant.getOperatingSoftware().isOperational();
		this.powerOutput  		= plant.getGenerator().getPowerOutput();
		
		this.reactorHealth             = plant.getReactor().getHealth();
		this.reactorTemperature        = plant.getReactor().getTemperature();
		this.reactorMaxTemperature     = plant.getReactor().getMaxTemperature();
		this.reactorPressure           = plant.getReactor().getPressure();
		this.reactorMaxPressure        = plant.getReactor().getMaxPressure();
		this.reactorWaterVolume        = plant.getReactor().getWaterVolume();
		this.reactorMinSafeWaterVolume = plant.getReactor().getMinSafeWaterVolume();
		
		this.condenserHealth         = plant.getCondenser().getHealth();
	    this.condenserTemperature    = plant.getCondenser().getTemperature();
	    this.condenserMaxTemperature = plant.getCondenser().getMaxTemperature();
	    this.condenserPressure       = plant.getCondenser().getPressure();
	    this.condenserMaxPressure    = plant.getCondenser().getMaxPressure();
	    this.condenserWaterVolume    = plant.getCondenser().getWaterVolume();
	    
	    this.controlRods = plant.getReactor().getPercentageLowered();
	    
	    this.valves = plant.getValves();
	    this.pumps  = plant.getPumps();
	}
	
	/**
	 * 
	 * @return 
	 */
	public String getOperatorName() {
		return operatorName;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isGameOver() {
		return gameOver;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getReactorHealth() {
		return reactorHealth;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getReactorTemperature() {
		return reactorTemperature;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getReactorMaxTemperature() {
		return reactorMaxTemperature;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getReactorPressure() {
		return reactorPressure;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getReactorMaxPressure() {
		return reactorMaxPressure;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getReactorWaterVolume() {
		return reactorWaterVolume;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getReactorMinSafeWaterVolume() {
		return reactorMinSafeWaterVolume;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getCondenserHealth() {
		return condenserHealth;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getCondenserTemperature() {
		return condenserTemperature;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getCondenserMaxTemperature() {
		return condenserMaxTemperature;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getCondenserPressure() {
		return condenserPressure;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getCondenserMaxPressure() {
		return condenserMaxPressure;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getCondenserWaterVolume() {
		return condenserWaterVolume;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getControlRodsPercentage() {
		return controlRods;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Valve> getValves() {
		return valves;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Pump> getPumps() {
		return pumps;
	}

	/**
	 * 
	 * @return
	 */
	public int getTurbineRpm() {
		return turbineRpm;
	}

	/**
	 * 
	 * @return
	 */
	public int getPowerOutput() {
		return powerOutput;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isTurbineFunctional() {
		return turbineFunctional;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isOperatingSoftwareFunctional()
	{
		return operatingSoftwareFunctional;
	}
	
	/**
	 * Adds a broken component to list brokenOnStep.
	 * 
	 * @param broken a broken component.
	 */
	public void addBrokenOnStep(PlantComponent broken) {
		brokenOnStep.add(broken);
	}
	
	/**
	 * Resets the list of brokenOnStep components.
	 */
	public void resetBrokenOnStep() {
		brokenOnStep = new ArrayList<PlantComponent>();
	}
	
	/**
	 * Returns a list of all broken components since last call of step command.
	 * 
	 * Used to print information about components that broke when step was called.
	 * 
	 * @return a list of all broken components since last call of step command.
	 */
	public List<PlantComponent> getBrokenOnStep() {
		return brokenOnStep;
	}

	public boolean isMultiplayer() {
		return multiplayer;
	}
}
