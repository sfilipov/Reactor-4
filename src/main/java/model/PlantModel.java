package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import components.Condenser;
import components.ConnectorPipe;
import components.RandomlyFailableComponent;
import components.Generator;
import components.OperatingSoftware;
import components.PlantComponent;
import components.Pump;
import components.Reactor;
import components.Turbine;
import components.UpdatableComponent;
import components.Valve;




/**
 * Plant class is the holder of all plant components, the score, time steps passed, the name of the
 * player, etc. It represents the "model" of the MVC design of the game.
 * 
 * @author Lamprey
 */
public class PlantModel implements Serializable {

	private static final long serialVersionUID = 4799981348038802742L;
	
	private ComponentFactory factory;
	
	private String operatorName;
	private boolean gameOver;
	private boolean multiplayer;
	private int score;
	private List<Repair> beingRepaired;
	private boolean isPaused;
	private List<HighScore> highScores;
	private List<PlantComponent> plantComponents;
	private List<RandomlyFailableComponent> failedComponents;
	private Reactor reactor;
	private List<Valve> valves;
	private List<ConnectorPipe> connectorPipes;
	private Condenser condenser;
	private List<Pump> pumps;
	private Turbine turbine;
	private Generator generator;
	private OperatingSoftware operatingSoftware;
	
	/**
	 * This is the default constructor that is used 
	 * when there is no saved game (i.e. new game)
	 */
	public PlantModel() {
		this.factory = new ModelComponentFactory();
		
		this.operatorName = null;
		this.gameOver = false;
		this.score = 0;
		this.beingRepaired = new ArrayList<Repair>();
		this.isPaused = false;
		this.highScores = new ArrayList<HighScore>();
		this.plantComponents = factory.createPlantComponents();
		assignComponentsToFields(this.plantComponents);
		this.failedComponents = new ArrayList<RandomlyFailableComponent>();
	}
	
	public void newGame(String operatorName) {
		this.operatorName = operatorName;
		this.gameOver = false;
		this.score = 0;
		this.beingRepaired = new ArrayList<Repair>();
		this.isPaused = false;
		this.highScores = new ArrayList<HighScore>();
		this.plantComponents = factory.createPlantComponents();
		assignComponentsToFields(this.plantComponents);
		this.failedComponents = new ArrayList<RandomlyFailableComponent>();
	}
	
	/**
	 * 
	 * @return name of the operator (player)
	 */
	public String getOperatorName() {
		return operatorName;
	}
	
	/**
	 * Sets the name of the operator (player).
	 * 
	 * @param operatorName name of the operator (player)
	 */
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	
	/**
	 * 
	 * @return current score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Updates the score.
	 * 
	 * Calculates the score based on the power output of the generator.
	 */
	public void calcScore() {
		int powerOutput = getGenerator().getPowerOutput();
		this.score += powerOutput * 10;
	}

	/**
	 * Checks if the game is paused.
	 * Currently not used as the game is turn based. Makes creating a real time game easier.
	 * 
	 * @return true if the game is paused
 	 */
	public boolean isPaused() {
		return isPaused;
	}

	/**
	 * Sets the paused state of the game.
	 * Currently not used as the game is turn based. Makes creating a real time game easier.
	 * 
	 * @param isPaused whether to pause or resume the game
	 */
	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}
	


	/**
	 *
	 * @return the reactor object of the plant
	 */
	public Reactor getReactor() {
		return reactor;
	}
	
	/**
	 * 
	 * @return a list of valves in the plant
	 */
	public List<Valve> getValves() {
		return valves;
	}

	/**
	 * 
	 * @return a list of all connector pipes in the plant
	 */
	public List<ConnectorPipe> getConnectorPipes() {
		return connectorPipes;
	}

	/**
	 * 
	 * @return the condenser of the plant
	 */
	public Condenser getCondenser() {
		return condenser;
	}
	
	/**
	 * 
	 * @return a list of all pumps in the plant
	 */
	public List<Pump> getPumps() {
		return pumps;
	}
	
	/**
	 * 
	 * @return the turbine of the plant.
	 */
	public Turbine getTurbine() {
		return turbine;
	}
	
	/**
	 * 
	 * @return the generator of the plant
	 */
	public Generator getGenerator() {
		return generator;
	}
	
	/**
	 * 
	 * @return the operating software of the plant
	 */
	public OperatingSoftware getOperatingSoftware() {
		return operatingSoftware;
	}

	/**
	 * 
	 * @return a list of all highscores (maximum 20)
	 */
	public List<HighScore> getHighScores() {
		if(this.highScores.size() > 20) {
			this.highScores = this.highScores.subList(0, 20); //Trims the high scores list to only the first 20 elements
		}
		return this.highScores;
	}
	
	/**
	 * Sets the list of high scores.
	 * 
	 * Used when loading the high scores from a file.
	 * 
	 * @param highScores  list of high scores
	 */
	public void setHighScores(List<HighScore> highScores) {
		this.highScores = highScores;
	}

	/**
	 * 
	 * @return  list of all components that are being repaired
	 */
	public List<Repair> getBeingRepaired() {
		return beingRepaired;
	}

	/**
	 * 
	 * @return list of all plant components
	 */
	public List<PlantComponent> getPlantComponents() {
		return plantComponents;
	}
	
	public List<RandomlyFailableComponent> getRandomlyFailableComponents() {
		ArrayList<RandomlyFailableComponent> failableComponents = new ArrayList<RandomlyFailableComponent>();
		for (PlantComponent plantComponent : plantComponents) {
			if(plantComponent instanceof RandomlyFailableComponent) {
				failableComponents.add((RandomlyFailableComponent)plantComponent);
			}
		}
		return failableComponents;
	}
	
	/**
	 * 
	 * @return all failed (non-operational) components (including those that are being repaired)
	 */
	public List<RandomlyFailableComponent> getFailedComponents() {
		return failedComponents;
	}
	
	/**
	 * Adds failedComponent to failedComponents List, as long as it is
	 * not already in the list.
	 * 
	 * @param failedComponent
	 */
	public void addFailedComponent(RandomlyFailableComponent failedComponent) {
		if (!this.failedComponents.contains(failedComponent)) {
			this.failedComponents.add(failedComponent);
		}
	}	
	
	/**
	 * Sets the game over state to true.
	 */
	public void gameOver() {
		this.gameOver = true;
	}
	
	/**
	 * Checks  if the game is over
	 * 
	 * @return true if the game is over, false otherwise
	 */
	public boolean isGameOver() {
		return this.gameOver;
	}

	/**
	 * Returns true if the game is currently in multiplayer mode.
	 * 
	 * @return true if the game is currently in multiplayer mode.
	 */
	public boolean isMultiplayer() {
		return this.multiplayer;
	}

	/**
	 * Turns on/off multiplayer mode.
	 * 
	 * @param multiplayer true to set the game to multiplayer mode.
	 */
	public void setMultiplayer(boolean multiplayer) {
		this.multiplayer = multiplayer;
	}
	
	private void assignComponentsToFields(List<PlantComponent> components) {
		initialiseListFields();
		for (PlantComponent component : components) {
			if (component instanceof Reactor) {
				this.reactor = (Reactor) component;
			} else if (component instanceof Condenser) {
				this.condenser = (Condenser) component;
			} else if (component instanceof Turbine) {
				this.turbine = (Turbine) component;
			} else if (component instanceof Generator) {
				this.generator = (Generator) component;
			} else if (component instanceof OperatingSoftware) {
				this.operatingSoftware = (OperatingSoftware) component;
			} else if (component instanceof Pump) {
				this.pumps.add((Pump) component);
			} else if (component instanceof Valve) {
				this.valves.add((Valve) component);
			} else if (component instanceof ConnectorPipe) {
				this.connectorPipes.add((ConnectorPipe) component);
			}
		}
	}
	
	private void initialiseListFields() {
		this.pumps = new ArrayList<Pump>();
		this.valves = new ArrayList<Valve>();
		this.connectorPipes = new ArrayList<ConnectorPipe>();
	}
}
