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
	
	private String playerName;
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
	public Plant() {
		this.factory = new ModelComponentFactory();
		
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
		
		updateFlow();
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
	public void step(int numSteps) {
		for (int i = 0; i < numSteps; i++) {
			if (!isGameOver()) {
				updateBeingRepaired();
				updateFlow();
				updatePlant();
				updateCriticalComponentsHealth();
				// If not in multiplayer mode then invoke random failures.
				if (!isMultiplayer()) updateRandomFailures();
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
	 * Highest level method for updating flow. This method calls all other methods
	 * necessary for propagating flow, as well as blockages, throughout the system.
	 * In this order, we:
	 * 		- Set all outputs of all ConnectorPipes to not blocked.
	 * 		- Propagate blockages from all closed valves in the system back to their
	 * 			first preceding ConnectorPipe.
	 * 		- Propagate all blockages throughout the entire system.
	 * 		- Set the flow rate and temperature of all components to zero in 
	 * 			preparation for flow calculation & propagation.
	 * 		- Calculate and propagate the flow from the reactor forward.
	 * 		- Calculate the flow due to the pumps in the system and totals them up at
	 * 			the condenser output.
	 * 		- Propagate the flow out of the condenser forwards.
	 * 		- Propagate flow through all paths in the system.
	 * 		- Transfer steam from the reactor into the condenser.
	 * 		- Transfer water from the condenser into the reactor. 
	 */
	private void updateFlow() {
		setAllConnectorPipesUnblocked();
		blockFromValves();
		blockFromConnectorPipes();
		resetFlowAllComponents();
		
		propagateFlowFromReactor(); // Start propagation of steam flow.
		propagateFlowFromPumpsToCondenser(); // Total up all pump flows at condenser
		propagateFlowFromCondenser();	// Start propagation of water flow.
		propagateFlowFromConnectorPipes();
		moveSteam();
		moveWater(); 
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
	
	private void updateCriticalComponentsHealth() {
		try {
			getReactor().updateHealth();
			getCondenser().updateHealth();
		} catch (GameOverException e) {
			gameOver();
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
	
	// --------------- updateFlow() helper methods ---------------
	/**
	 * Resets all ConnectorPipe paths to unblocked.
	 * We do this to all ConnectorPipes at the beginning of each updatePlant()
	 * before propagating the blockages since valves can change state between 
	 * steps.
	 */
	private void setAllConnectorPipesUnblocked() {
		for (ConnectorPipe cp : getConnectorPipes()) {
			cp.resetState();
		}
	}
	
	/**
	 * Iterates through all valves in the system and if they are closed we
	 * propagate the blockage through to the next preceding ConnectorPipe.
	 */
	private void blockFromValves() {
		List<Valve> valves = getValves();
		for (Valve v : valves) {
			if (!v.isOpen()) blockToPrecedingConnectorPipe(v);
		}
	}
	
	/**
	 * Traces back to the first occurring connector pipe and blocks the path out leading 
	 * to blockedComponent.
	 * We assume checks have been made to ensure blockedComponent is actually blocked.
	 * 
	 * @param blockedComponent component to start from
	 */
	private void blockToPrecedingConnectorPipe(PlantComponent blockedComponent) {
		PlantComponent currentComponent = blockedComponent.getInput();
		PlantComponent prevComponent = blockedComponent;
		boolean doneBlocking = false;
		while (!doneBlocking) {
			if (currentComponent instanceof ConnectorPipe) {
				((ConnectorPipe) currentComponent).setComponentBlocked(prevComponent);
				doneBlocking = true;
			} else if (currentComponent instanceof Reactor) {
				// No need to do anything here, just stop iterating.
				doneBlocking = true;
			} else {
				prevComponent = currentComponent;
				currentComponent = currentComponent.getInput();
			}
		}
	}
	
	/**
	 * Iterates through all ConnectorPipes in the system and propagates the blockage,
	 * if all outputs of that ConnectorPipe is blocked.
	 * 
	 * This is done until all blocked ConnectorPipes have had their blockage propagated.
	 */
	private void blockFromConnectorPipes() {
		boolean changed = true;
		List<ConnectorPipe> connectorPipes = getConnectorPipes();
		Map<ConnectorPipe, Boolean> hasBeenPropagated = new HashMap<ConnectorPipe, Boolean>();
		while (changed) {
			changed = false;
			// iterate through all connector pipes and check if they're blocked up.
			for (ConnectorPipe c : connectorPipes) {
				// If we're not already keeping track of c, add it to the hashmap
				if (!hasBeenPropagated.containsKey(c)) hasBeenPropagated.put(c, false);
				// If connectorPipe has all of it's outputs blocked
				// And the blockage hasn't been propagated
				if (isConnectorBlocking(c) && !hasBeenPropagated.get(c)) {
					// Block the path leading into it.
					blockPrecedingFromConnectorPipe(c);
					hasBeenPropagated.put(c, true);
					changed = true;
				}
			}
		}
	}
	
	/**
	 * 
	 * @param cp the connector pipe being checked
	 * @return true if all outputs of a ConnectorPipe are blocked.
	 */
	private boolean isConnectorBlocking(ConnectorPipe cp) {
		for(Boolean blocked : cp.getOutputsMap().values()) {	
			if (!blocked) return false;
		}
		return true;
	}
	
	/**
	 * Calls blockPrecedingConnectorPipe() for all input paths into blockedConnector. 
	 * We assume checks have been made to ensure blockedConnector is actually blocked.
	 * 
	 * If an input is a ConnectorPipe, set the output that blockedConnector is connected
	 * to blocked.
	 * 
	 * @param blockedConnector the blocked ConnectorPipe to start from
	 */
	private void blockPrecedingFromConnectorPipe(ConnectorPipe blockedConnector) {
		List<PlantComponent> multipleInputs = ((ConnectorPipe) blockedConnector).getInputs();
		for (PlantComponent pc : multipleInputs) {
			if (pc instanceof ConnectorPipe) {
				((ConnectorPipe) pc).setComponentBlocked(blockedConnector);
			} else {
				if (pc != null) blockToPrecedingConnectorPipe(pc);
			}
		}
	}
	
	/**
	 * Resets the flow of all components back ready for the flow around the system to be
	 * recalculated for the current state of the plant.
	 */
	private void resetFlowAllComponents() {
		for (PlantComponent pc : getPlantComponents()) {
			pc.getFlowOut().setRate(0);
			pc.getFlowOut().setTemperature(0);
		}
	}
	
	/**
	 * Start off propagation of the flow from the reactor to the next 
	 * ConnectorPipe encountered.
	 */
	private void propagateFlowFromReactor()
	{
		int flowRate = calcReactorFlowOut();
		Reactor reactor = getReactor();
		Condenser condenser = getCondenser();
		// If there's a clear path from the reactor to the condenser then calculate
		// and start off the flow being propagated.
		if (isPathToForwards(reactor, condenser)) {
			reactor.getFlowOut().setRate(flowRate);
			reactor.getFlowOut().setTemperature(reactor.getTemperature());
			limitReactorFlowDueToValveMaxFlow(reactor);
			propagateFlowToNextConnectorPipe(reactor);
		} else {
			// Otherwise, all paths are blocked & don't bother.
		}
	}
	
	/**
	 * Calculate and return the flow of steam out of the reactor due to the difference in
	 * steam volume between the reactor and condenser.
	 * 
	 * This method ignores any blockages, these are dealt with when the flow is propagated
	 * around the system.
	 *  
	 * @return rate of flow of steam out of the reactor
	 */
	private int calcReactorFlowOut() {
		int steamDifference = Math.abs(getReactor().getSteamVolume() - getCondenser().getSteamVolume());
		return Math.min(steamDifference, Math.min(getReactor().getSteamVolume(), Reactor.getMaxSteamFlowRate()));
	}
	
	/**
	 * Returns true if there exists a path forwards from start to goal that is not blocked and does not 
	 * pass through a pressurised component (Reactor/Condenser) in the direction that is specified.
	 * 
	 * @param start Component to start from.
	 * @param goal Component to attempt to reach.
	 * @return true if there exists a path from start to goal that is not blocked and does not 
	 * pass through a pressurised component in the direction that is specified.
	 */
	private boolean isPathToForwards(PlantComponent start, PlantComponent goal) {
		if(start.equals(goal)) {
			return true;
		} else {
			// If we're at any other component than a ConnectorPipe, then advance to the next
			// component in the system in the direction we want.
			if (!(start.getOutput() instanceof ConnectorPipe)) {				
				return isPathToForwards(start.getOutput(), goal);
			} else {
				ConnectorPipe cp = (ConnectorPipe) start.getOutput();
				// I say, I say, we've got ourselves a ConnectorPipe!
				List<PlantComponent> possiblePaths = cp.getOutputs();
				for (PlantComponent possibleNext : possiblePaths) {
					/* Check if we're moving forwards, check that the ConnectorPipe output
					 * we're leaving from isn't blocked. If it is we don't move that way.
					 */
					if (!cp.getOutputsMap().get(possibleNext)) {
						// return isPathTo(possibleNext1, ...) || ... || isPathTo(possibleNextN,...)
						if (isPathToForwards(possibleNext, goal)) return true;
					}
				}
				// All paths out of this connector pipe are blocked, no paths available :(
				return false;
			}
		}
	}
	
	/**
	 * Returns true if there exists a path backwards from start to goal that is not blocked and does not 
	 * pass through a pressurised component (Reactor/Condenser) in the direction that is specified.
	 * 
	 * @param start Component to start from.
	 * @param goal Component to attempt to reach.
	 * @return true if there exists a path from start to goal that is not blocked and does not 
	 * pass through a pressurised component in the direction that is specified.
	 */
	private boolean isPathToBackwards(PlantComponent start, PlantComponent goal) {
		if (start.equals(goal)) {
			return true;
		} else {
			// If we're at any other component than a ConnectorPipe, then advance to the next
			// component in the system in the direction we want.
			if (!(start.getInput() instanceof ConnectorPipe)) {
				return isPathToBackwards(start.getInput(), goal);
			} else {
				ConnectorPipe cp = (ConnectorPipe) start.getInput();
				//Check if this path back is blocked
				if (cp.getOutputsMap().get(start)) {
					return false;
				}
				// I say, I say, we've got ourselves a ConnectorPipe!
				List<PlantComponent> possiblePaths = cp.getInputs();
				for (PlantComponent possibleNext : possiblePaths) {
					/* Check if we're moving forwards, check that the ConnectorPipe output
					 * we're leaving from isn't blocked. If it is we don't move that way.
					 */
					
					// return isPathTo(possibleNext1, ...) || ... || isPathTo(possibleNextN,...)
					if (isPathToBackwards(possibleNext, goal)) return true;
				}
				// All paths out of this connector pipe are blocked, no paths available :(
				return false;
			}
		}
	}
	
	/**
	 * Sums up the maximum flow possible through all valves that have a clear backward
	 * path to the reactor and if this maximum flow is greater than the amount of steam 
	 * wanting to come out of the reactor due to pressue, the rate is limited. 
	 * 
	 * @param reactor the reactor to limit
	 */
	private void limitReactorFlowDueToValveMaxFlow(Reactor reactor)
	{
		int maxFlow = 0;
		for (Valve v : getValves()) {
			// If there is a path backwards from this valve to the reactor.
			// Also implying that it is actually in front of the reactor.
			if (isPathToBackwards(v, reactor)) {
				// increase the maximum flow allowed out of the reactor.
				maxFlow += v.getMaxSteamFlow();
			}
		}
		if (reactor.getFlowOut().getRate() > maxFlow) reactor.getFlowOut().setRate(maxFlow);
	}
	
	/**
	 * Propagates the flow rate and temperature to every component from startComponent
	 * until a ConnectorPipe is encountered.
	 * 
	 * @param startComponent Component to start the propagation from.
	 */
	private void propagateFlowToNextConnectorPipe(PlantComponent startComponent) {
		PlantComponent prevComponent;
		// If startComponent.isPressurised() (=> it is a reactor or condenser) start from here, not its input. 
		prevComponent = (startComponent.isPressurised()) ? startComponent : startComponent.getInput();
		PlantComponent currComponent = startComponent;
		boolean donePropagating = false;
		while (!donePropagating) {
			if (currComponent instanceof ConnectorPipe) {
				donePropagating = true;
			} else if (currComponent instanceof Condenser) {
				donePropagating = true;
			} else {
				currComponent.getFlowOut().setRate(prevComponent.getFlowOut().getRate());
				currComponent.getFlowOut().setTemperature(prevComponent.getFlowOut().getTemperature());
				prevComponent = currComponent;
				currComponent = currComponent.getOutput();
			}
		}
	}
	
	/**
	 * Tracks back from a pump and if there is a clear path to the condenser
	 * adds the flow increase at this pump to the flow out of the condenser.
	 * 
	 * This method does not support multiple condensers.
	 */
	private void propagateFlowFromPumpsToCondenser()
	{
		Condenser condenser = getCondenser();
		// Iterate through all pumps and start tracking back through the system
		for (Pump p : getPumps()) {
			// If the pump is broken, move onto the next one.
			if (!getFailedComponents().contains(p) && p.getInput() != null) {
				increaseCondenserFlowOutFromPump(p);
			}
		}
		// Finally.. Make sure the flow out of the condenser will not take us into negative volume.
		int condenserWaterVolume = condenser.getWaterVolume();
		int condenserFlowOut = condenser.getFlowOut().getRate();
		if (condenserFlowOut > condenserWaterVolume) condenser.getFlowOut().setRate(condenserWaterVolume);
	}
	
	/**
	 * Gets the flowRate due to this pump from it's current rpm.
	 * Then checks if there is a path from Pump p to the connector (backwards)
	 * and if there is, we add the flow rate due to this pump to the flow rate out of
	 * the condenser.
	 * 
	 * @param p Pump to increase the flow out of the condenser
	 */
	private void increaseCondenserFlowOutFromPump(Pump p) {
		int flowRate = calcFlowFromPumpRpm(p);
		Condenser condenser = getCondenser();
		condenser.getFlowOut().setRate(condenser.getFlowOut().getRate() + flowRate);
	}
	
	/**
	 * Calculates the flow through a pump based upon it's rpm.
	 * The flow is linearly correlated to the rpm.
	 * 
	 * @param pump The pump to calculate the flow of
	 * @return The flow rate through pump
	 */
	private int calcFlowFromPumpRpm(Pump pump)
	{
		int maxRpm = pump.getMaxRpm();
		return (int) Math.round(Pump.getMaxWaterFlowRatePerPump() * (1 - (new Double((maxRpm - pump.getRpm())/new Double(maxRpm)))));
	}
	
	/**
	 * Set's off the propagation from the condenser to the next ConnectorPipe from 
	 * it's output.
	 */
	private void propagateFlowFromCondenser()
	{
		Condenser condenser = getCondenser();
		condenser.getFlowOut().setTemperature(condenser.getTemperature());
		propagateFlowToNextConnectorPipe(condenser);
	}
	
	/**
	 * Iterates through connector pipes, calculates their flow out & if it has changed,
	 * propagate this new flow forward to the next connector pipe.
	 * Do this until nothing in the system changes 
	 * (Inspired by bubble sort's changed flag... "Good Ol' Bubble Sort!")
	 */
	private void propagateFlowFromConnectorPipes()
	{
		boolean changed = true;
		int oldRate;
		List<ConnectorPipe> connectorPipes = getConnectorPipes();
		while (changed) {
			changed = false;
			// iterate through all connector pipes and update their rate.
			for (ConnectorPipe c : connectorPipes) {
				oldRate = c.getFlowOut().getRate();
				calcConnectorFlowOut(c);
				if (oldRate != c.getFlowOut().getRate()) {
					propagateFlowFromConnectorPipe(c);
					changed = true;
				}
			}
		}
	}
	
	/**
	 * Update the Flow out of a connector to reflect it's inputs and outputs.
	 * 
	 * @param connector the connector to update.
	 */
	private void calcConnectorFlowOut(ConnectorPipe connector) {
		ArrayList<PlantComponent> inputs = connector.getInputs();
		int totalFlow = 0;
		int avgTemp = 0;
		int numOutputs = connector.numOutputs();
		int numInputs = 0;
		for (PlantComponent input : inputs) {
			if (input != null) {
				totalFlow += input.getFlowOut().getRate();
				avgTemp += input.getFlowOut().getTemperature();
				numInputs++;
			}
		}
		totalFlow = (numOutputs != 0) ? totalFlow / numOutputs : 0; // average the flow across all active outputs.
		avgTemp = (numInputs != 0) ? avgTemp / numInputs : 0;
		connector.getFlowOut().setRate(totalFlow);
		connector.getFlowOut().setTemperature(avgTemp);
	}
	
	/**
	 * Propagates calls the appropriate methods for all unblocked outputs of 
	 * startConnectorPipe in order to propagate flow through the system.  
	 * 
	 * @param startConnectorPipe The ConnectorPipe to propagate flow onward from.
	 */
	private void propagateFlowFromConnectorPipe(ConnectorPipe startConnectorPipe) {
		Map<PlantComponent, Boolean> outputs = startConnectorPipe.getOutputsMap();
		for (PlantComponent pc : outputs.keySet()) {
			// If the output is not blocked.
			if (!outputs.get(pc)) {
				if (pc instanceof ConnectorPipe) {
					propagateFlowFromConnectorPipe((ConnectorPipe) pc);
				} else {
					propagateFlowToNextConnectorPipe(pc);
				}
			}
		}
	}
	
	/**
	 * Forcefully removes steam from the reactor and places it into the condenser.
	 * Based upon the flow! :) 
	 */
	private void moveSteam()
	{
		Reactor reactor = getReactor();
		Condenser condenser = getCondenser();
		reactor.removeSteam(reactor.getFlowOut().getRate());
		condenser.addSteam(condenser.getInput().getFlowOut().getRate(), condenser.getInput().getFlowOut().getTemperature());
	}
	
	/**
	 * Moves water out of the condenser and into the reactor due to the flow in and
	 * out of the components.
	 */
	private void moveWater()
	{
		Condenser condenser = getCondenser();
		Reactor reactor = getReactor();
		int waterInCondenser = condenser.getWaterVolume();
		int amountOut = 0;
		int condenserFlowOut = condenser.getFlowOut().getRate();
		// Check if there's enough water in the condenser to fulfil the flow rate.
		amountOut = (waterInCondenser > condenser.getFlowOut().getRate()) ?
						condenserFlowOut: 
						waterInCondenser; // otherwise empty out the condenser!)
		condenser.pumpOutWater(amountOut);
		// This should really use reactor's input's flow out but ah well.
		reactor.pumpInWater(amountOut);
	}
}
