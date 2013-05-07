package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import components.Condenser;
import components.ConnectorPipe;
import components.GameOverException;
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
public class Plant implements Serializable {

	private static final long serialVersionUID = 4799981348038802742L;
	
	private ComponentFactory factory;
	private FlowUpdater flowUpdater;
	
	private String playerName;
	private boolean gameOver;
	private boolean randomFailures;
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
	public Plant() {
		this.factory = new PlantComponentFactory();
		this.flowUpdater = new FlowUpdater(this);
		
		this.playerName = null;
		this.gameOver = false;
		this.score = 0;
		this.beingRepaired = new ArrayList<Repair>();
		this.isPaused = false;
		this.highScores = new ArrayList<HighScore>();
		this.plantComponents = factory.createPlantComponents();
		assignComponentsToFields(this.plantComponents);
		this.failedComponents = new ArrayList<RandomlyFailableComponent>();
	}
	
	public void newGame(String playerName) {
		this.playerName = playerName;
		this.gameOver = false;
		this.score = 0;
		this.beingRepaired = new ArrayList<Repair>();
		this.isPaused = false;
		this.highScores = new ArrayList<HighScore>();
		this.plantComponents = factory.createPlantComponents();
		assignComponentsToFields(this.plantComponents);
		this.failedComponents = new ArrayList<RandomlyFailableComponent>();
		
		flowUpdater.updateFlow();
		updatePlant();
	}
	
	/**
	 * 
	 * @return name of the operator (player)
	 */
	public String getOperatorName() {
		return playerName;
	}
	
	/**
	 * Sets the name of the operator (player).
	 * 
	 * @param operatorName name of the operator (player)
	 */
	public void setOperatorName(String operatorName) {
		this.playerName = operatorName;
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
	 * Advance the game by a number of time steps.
	 * 
	 * If the game reaches a game over state before all steps are executed,
	 * the game stops stepping.
	 * 
	 * @param numSteps number of timesteps to advance the game by.
	 */
	public void step(int numSteps) throws GameOverException {
		for (int i = 0; i < numSteps; i++) {
			if (!isGameOver()) {
				updateBeingRepaired();
				flowUpdater.updateFlow();
				updatePlant();
				updateCriticalComponentsHealth();
				if (isRandomFailures()) updateRandomFailures();
			}
			else {
				break;
			}
		}
	}
	
	/**
	 * Updates the state of the components that are being repaired.
	 * 
	 * Decreases the number of steps left until a component's repairing is completed.
	 * Then checks all components and if they are finished repairing and take
	 * appropriate actions if that is the case (remove from lists of failed components
	 * and set to operational).
	 */
	private void updateBeingRepaired() {
		List<Repair> beingRepaired = getBeingRepaired();
		List<Repair> finishedRepairing = new ArrayList<Repair>();
		List<RandomlyFailableComponent> failedComponents = getFailedComponents();
		for (Repair repair : beingRepaired) {
			repair.decTimeStepsRemaining();
			int timeStepsRemaining = repair.getTimeStepsRemaining();
			if(timeStepsRemaining <= 0) {
				finishedRepairing.add(repair);
			}
		}
		for (Repair finished : finishedRepairing) {
			failedComponents.remove(finished.getPlantComponent());
			finished.getPlantComponent().setOperational(true);
			beingRepaired.remove(finished);
		}
	}
	
	/**
	 * Go through all components and call updateState() then calculates the current score.
	 */
	private void updatePlant() {
		List<PlantComponent> plantComponents = getPlantComponents();
		for (PlantComponent plantComponent : plantComponents) {
			if (plantComponent instanceof UpdatableComponent)
				((UpdatableComponent) plantComponent).updateState();
		}
		calcScore();
	}
	
	/**
	 * Goes through all components and check for failures.
	 * 
	 * If more than one component fails, only one is actually getting broken.
	 * If a reactor or condenser is broken, then the game is over.
	 */
	private void updateRandomFailures() {
		List<RandomlyFailableComponent> randomlyFailableComponents  = getRandomlyFailableComponents();
		List<RandomlyFailableComponent> failedComponents    = getFailedComponents();
		List<RandomlyFailableComponent> failingComponents = new ArrayList<RandomlyFailableComponent>();
		int faults = 0;
		
		//Checks all components if they randomly fail
		for (RandomlyFailableComponent component : randomlyFailableComponents) 
		{
			if (component.hasFailed() && !failedComponents.contains(component)) 
			{
				failingComponents.add(component);
				faults++;
			}
		}
		
		//Picks only one of all randomly failing components.
		if(faults > 0) {
			Random random = new Random();
			int selection = random.nextInt(faults);
			RandomlyFailableComponent failedComponent = failingComponents.get(selection);
			
			if (failedComponent instanceof Turbine) {
				setValve(1,false);
				setValve(2, true);
				setControlRods(100);
			}
			
			addFailedComponent(failedComponent);
			failedComponent.setOperational(false);
//			uidata.addBrokenOnStep(failedComponent);
		}
	}
	
	private void updateCriticalComponentsHealth() throws GameOverException {
		try {
			getReactor().updateHealth();
			getCondenser().updateHealth();
		} catch (GameOverException goe) {
			gameOver = true;
			throw goe;
		}
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
	
	public int getControlRodsLevel() {
		return getReactor().getPercentageLowered();
	}
	
	public void setControlRods(int percentageLowered) {
		getReactor().setControlRods(percentageLowered);
	}

	/**
	 *
	 * @return the reactor object of the plant
	 */
	public Reactor getReactor() {
		return reactor;
	}
	
	public int getReactorTemperature() {
		return getReactor().getTemperature();
	}
	
	public int getReactorPressure() {
		return getReactor().getPressure();
	}
	
	public int getReactorWaterVolume() {
		return getReactor().getWaterVolume();
	}
	
	public int getReactorHealth() {
		return getReactor().getHealth();
	}
	
	/**
	 * 
	 * @return a list of valves in the plant
	 */
	public List<Valve> getValves() {
		return valves;
	}
	
	public boolean isValveOpen(int valveID) {
		for (Valve valve : getValves()) {
			if (valveID == valve.getID()) {
				return valve.isOpen();
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param valveID the valve to be set
	 * @param open true to open the valve, false to close it
	 * @return true if command was successful, false if a valve with that ID was not found
	 */
	public boolean setValve(int valveID, boolean open) {
		for (Valve valve : getValves()) {
			if (valveID == valve.getID()) {
				valve.setOpen(open);
				return true;
			}
		}
		return false;
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
	
	public int getCondenserTemperature() {
		return getCondenser().getTemperature();
	}
	
	public int getCondenserPressure() {
		return getCondenser().getPressure();
	}
	
	public int getCondenserWaterVolume() {
		return getCondenser().getWaterVolume();
	}
	
	public int getCondenserHealth() {
		return getCondenser().getHealth();
	}
	
	/**
	 * 
	 * @return a list of all pumps in the plant
	 */
	public List<Pump> getPumps() {
		return pumps;
	}
	
	public boolean isPumpOperational(int pumpID) {
		for (Pump pump : getPumps()) {
			if (pumpID == pump.getID()) {
				return pump.isOperational();
			}
		}
		return false;
	}
	
	public int getPumpRpm(int pumpID) {
		for (Pump pump : getPumps()) {
			if (pumpID == pump.getID()) {
				return pump.getRpm();
			}
		}
		return 0;
	}
	
	/**
	 * Sets the RPM of a particular pump.
	 * 
	 * @param  pumpID the internal ID of the pump
	 * @param  rpm the new value of the RPM, needs to be in range (0 to MAX_RPM)
	 * @return true if setting the RPM was successful, false otherwise
	 * @throws IllegalArgumentException if RPM is out of the allowed range (rpm < 0 || rpm > MAX_RPM).
	 */
	public boolean setPumpRpm(int pumpID, int rpm) throws IllegalArgumentException {
		for (Pump pump : getPumps()) {
			if (pumpID == pump.getID()) {
				pump.setRpm(rpm);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Forces the specified pump (by ID) to fail.
	 * 
	 * @param pumpID ID of the pump to fail.
	 */
	public void failPump(int pumpID) {
		List<Pump> pumps = getPumps();
		Pump foundPump = null;
		List<RandomlyFailableComponent> failedComponents = getFailedComponents();
		for (Pump pump : pumps) { // Find the pump with the selected ID
			if (pump.getID() == pumpID) {
				foundPump = pump;
			}
		}
		if (!failedComponents.contains(foundPump)) {
			// No need to check if the pump is currently being repaired...
			// If it is being repaired then it must be broken.
			failedComponents.add(foundPump);
			foundPump.setOperational(false);
//			uidata.addBrokenOnStep(foundPump);
		}
	}
	
	/**
	 * Forces the failure of the turbine.
	 */
	public void failTurbine() {
		List<RandomlyFailableComponent> failedComponents = getFailedComponents();
		Turbine turbine = getTurbine();
		if (!failedComponents.contains(turbine)) {
			// No need to check if the pump is currently being repaired...
			// If it is being repaired then it must be broken.
			failedComponents.add(turbine);
			turbine.setOperational(false);
			// Safety feature.
			setValve(1,false);
			setValve(2, true);
			setControlRods(100);

//			uidata.addBrokenOnStep(turbine);
		}
	}
	
	/**
	 * Forces the failure of the operating software.
	 */
	public void failOS() {
		List<RandomlyFailableComponent> failedComponents = getFailedComponents();
		OperatingSoftware os = getOperatingSoftware();
		if (!failedComponents.contains(os)) {
			// No need to check if the pump is currently being repaired...
			// If it is being repaired then it must be broken.
			failedComponents.add(os);
			os.setOperational(false);
//			uidata.addBrokenOnStep(os);
		}
	}
	
	/**
	 * 
	 * @return the turbine of the plant.
	 */
	public Turbine getTurbine() {
		return turbine;
	}
	
	public boolean isTurbineOperational() {
		return turbine.isOperational();
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
	
	public boolean isSoftwareOperational() {
		return operatingSoftware.isOperational();
	}
	
	/**
	 * Start the repair of a particular pump
	 * 
	 * @param  pumpID the internal ID of the pump to be repaired
	 * @return true only if the pump is found, has failed and is not already being repaired
	 */
	public boolean repairPump(int pumpID) {
		List<Pump> pumps = getPumps();
		Pump foundPump = null;
		boolean found = false;
		List<RandomlyFailableComponent> failedComponents = getFailedComponents();
		List<Repair> beingRepaired = getBeingRepaired();
		for (Pump pump : pumps) { //Find the pump with the selected ID
			if (pump.getID() == pumpID) {
				foundPump = pump;
				found = true;
			}
		}
		if (found && failedComponents.contains(foundPump)) {
			for (Repair br : beingRepaired) {
				if (br.getPlantComponent() == foundPump) 
					return false; //Pump already being repaired
			}
			beingRepaired.add(new Repair(foundPump));
			return true; //Pump has failed and is not being repaired (success)
		}
		return false; //Pump not found or has not failed
	}
	
	/**
	 * Start the repair of the turbine if it has failed.
	 * 
	 * @return true only if the turbine has failed and is not already being repaired
	 */
	public boolean repairTurbine() {
		Turbine turbine = getTurbine();
		List<RandomlyFailableComponent> failedComponents = getFailedComponents();
		List<Repair> beingRepaired = getBeingRepaired();
		if (failedComponents.contains(turbine)) {
			for (Repair br : beingRepaired) {
				if (br.getPlantComponent() == turbine)
					return false; //Turbine already being repaired
			}
			beingRepaired.add(new Repair(turbine));
			return true; //Turbine has failed and is not being repaired (success)
		}
		return false; //Turbine has not failed
	}
	
	/**
	 * Start the repair of the operating software if it has failed
	 * 
	 * @return true only if the operating software has failed and is not already being repaired
	 */
	public boolean repairOperatingSoftware() {
		OperatingSoftware operatingSoftware = getOperatingSoftware();
		List<RandomlyFailableComponent> failedComponents = getFailedComponents();
		List<Repair> beingRepaired = getBeingRepaired();
		if (failedComponents.contains(operatingSoftware)) {
			for (Repair br : beingRepaired) {
				if (br.getPlantComponent() == operatingSoftware)
					return false; //operating software already being repaired
			}
			beingRepaired.add(new Repair(operatingSoftware));
			return true; //OperatingSoftware has failed and is not being repaired (success)
		}
		return false; //OperatingSoftware has not failed
	}
	
	/**
	 * Attempts to quench the reactor.
	 * @return true if the reactor was quenched, false if quench is not available.
	 */
	public boolean quenchReactor() {
		if (getReactor().isQuenchAvailable()) {
			getReactor().quench();
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isQuenchAvailable() {
		return reactor.isQuenchAvailable();
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

	public void setRandomFailures(boolean randomFailures) {
		this.randomFailures = randomFailures;
	}

	public boolean isRandomFailures() {
		return randomFailures;
	}
}
