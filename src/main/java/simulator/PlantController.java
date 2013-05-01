package simulator;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import components.Condenser;
import components.ConnectorPipe;
import components.GameOverException;
import components.RandomlyFailableComponent;
import components.OperatingSoftware;
import components.PlantComponent;
import components.Pump;
import components.Reactor;
import components.Turbine;
import components.UpdatableComponent;
import components.Valve;


import model.HighScore;
import model.Plant;
import model.Repair;



/**
 * PlantController is the "controller" of the MVC design of the game. It has
 * methods necessary to control the data held in Plant, to execute methods
 * that relate to UI functions, to give information about the Plant to an UI.
 * 
 * @author Lamprey
 */
public class PlantController {

	private Plant plant;
	private UIData uidata;
	
	/**
	 * 
	 * @param utils the utilities for the game
	 */
	public PlantController(Plant plant)
	{
		this.plant = plant;
		readHighScores();
		uidata = new UIData(plant);
	}
	
	/* ----------------		Methods	for UI to call	----------------
	 * There is a method for each command that can be given by the
	 * user. 
	 */
	
	/**
	 * Executes the command that is stored in the operatingSoftware object
	 */
	public void executeStoredCommand()
	{
	    //obtains reference to the object
	    OperatingSoftware operatingSoftware = plant.getOperatingSoftware();
	    
	    switch(operatingSoftware.getRequestedOperation())
	    {//checks what is the command that has to be executed and calls the
	     //appropriate method with the required information obtained from the
	     //operating software
	        case SetControlRods: 
                setControlRods(operatingSoftware.getPercentageLowered());
                break;
                
            case SetPumpRpm:
                setPumpRpm(operatingSoftware.getPumpID(),operatingSoftware.getRpm());
                break;
                
            case SetValve: 
                setValve(operatingSoftware.getValveID(),operatingSoftware.isOpen());
                break;
                
            case RepairTurbine:
                repairTurbine();
                break;
                
            case RepairPump:
                repairPump(operatingSoftware.getPumpID());
                break;
                
            default:
                //execute no command
                break; 
	    }
	    
	}
	
	/**
	 * Creates a new Game - used in UI to start a new game.
	 * 
	 * @param operatorName the name of the player
	 */
	public void newGame(String operatorName) {
		this.plant.newGame(operatorName);
		uidata = new UIData(plant);
		readHighScores();
		// update things as per the default values.
		// mainly to calculate the pressure etc in these things.
	}
	
	/**
	 * Saves the state of the current game (plant) into a file called "save.ser" inside the current folder.
	 * 
	 * @return true if saving a game was successful, false otherwise
	 */
	public boolean saveGame(){
		FileOutputStream fileOut = null;
		ObjectOutputStream out   = null;
		try {
			fileOut = new FileOutputStream("save.ser");
			out =     new ObjectOutputStream(fileOut);
			out.writeObject(plant);
			out.close();
			fileOut.close();
			return true;
		}
		catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Loads the state of the current game (plant) from a file called "save.ser" inside the current folder.
	 * If the file does not exist, the load will not be successful (nothing will happen).
	 * 
	 * @return true if loading a game was successful, false otherwise
	 */
	public boolean loadGame() {
		Plant plant = null;
		FileInputStream fileIn  = null;
		ObjectInputStream in = null;
		try {
			File f = new File("save.ser");
			if(f.exists()) {
				fileIn = new FileInputStream(f);
				in = new ObjectInputStream(fileIn);
				plant = (Plant) in.readObject();
				in.close();
				fileIn.close();
				this.plant = plant;
				uidata = new UIData(plant);
				return true;
			}
			else {
				return false;
			}
		}
		catch (IOException io) {
			io.printStackTrace();
			return false;
		}
		catch (ClassNotFoundException c) {
			c.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Pauses/resumes the game on call.
	 * 
	 * Currently not in used as the game is turn based. Gives the possibility
	 * to easily create a real-time game.
	 */
	public void togglePaused() {
		this.plant.setPaused(!this.plant.isPaused());
	}
	
	/**
	 * Returns the highscores list.
	 * 
	 * @return list of highscores.
	 */
	public List<HighScore> getHighScores() {
		return plant.getHighScores();
	}
	
	/**
	 * Adds a new score to high scores if the new score is in the top 10 of all scores.
	 * 
	 * @param newHighScore the score to add
	 * @return true if adding was successful and the new score is in top 10, false otherwise
	 */
	public boolean addHighScore(HighScore newHighScore) {
		List<HighScore> highScores = plant.getHighScores();
		int size = highScores.size();
		if (newHighScore.getHighScore() > 0) {
			for (int i=0; i < 20; i++) {
				if (i < size) {
					HighScore oldHighScore = highScores.get(i);
					if (oldHighScore.compareTo(newHighScore) < 0) {
						highScores.add(i, newHighScore);
						writeHighScores();
						return true;
					}
				}
				else {
					highScores.add(size, newHighScore);
					writeHighScores();
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Toggles multiplayer mode on/off.
	 * Returns the new boolean state. (True = multiplayer on).
	 */
	public boolean toggleMultiplayer() {
		plant.setMultiplayer(!plant.isMultiplayer());
		return plant.isMultiplayer();
	}
	
	/**
	 * Attempts to quench the reactor.
	 * @return true if the reactor was quenched, false if quench is not available.
	 */
	public boolean quenchReactor() {
		return plant.quenchReactor();
	}
	
	public boolean isQuenchAvailable() {
		return plant.getReactor().isQuenchAvailable();
	}
	
	/**
	 * 
	 * @param valveID the valve to be set
	 * @param open true to open the valve, false to close it
	 * @return true if command was successful, false if a valve with that ID was not found
	 */
	public boolean setValve(int valveID, boolean open) {
		return plant.setValve(valveID, open);
	}
	
	/**
	 * Sets the RPM of a particular pump.
	 * 
	 * @param  pumpID the internal ID of the pump
	 * @param  rpm the new value of the RPM, needs to be in range (0 to MAX_RPM)
	 * @return true if setting the pump was successful
	 */
	public boolean setPumpRpm(int pumpID, int rpm) {
		return plant.setPumpRpm(pumpID, rpm);
	}
	
	/**
	 * Sets a new percentage for the control rods.
	 * 
	 * Setting the percentage to 100 means that the control rods are fully inside
	 * the reactor and the reaction stops. Setting the percentage to 0 means that
	 * the control rods are outside of the reactor.
	 * 
	 * @param percentageLowered the new value of percentageLowered
	 */
	public void setControlRods(int percentageLowered) {
		plant.setControlRods(percentageLowered);
	}
	
	/**
	 * Forces the specified pump (by ID) to fail.
	 * 
	 * @param pumpID ID of the pump to fail.
	 */
	public void failPump(int pumpID) {
		plant.failPump(pumpID);
	}
	
	/**
	 * Forces the failure of the turbine.
	 */
	public void failTurbine() {
		plant.failTurbine();
	}
	
	/**
	 * Forces the failure of the operating software.
	 */
	public void failOS() {
		plant.failOS();
	}
	
	/**
	 * Start the repair of the turbine if it has failed.
	 * 
	 * @return true only if the turbine has failed and is not already being repaired
	 */
	public boolean repairTurbine() {
		return plant.repairTurbine();
	}
	
	/**
	 * Start the repair of a particular pump
	 * 
	 * @param  pumpID the internal ID of the pump to be repaired
	 * @return true only if the pump is found, has failed and is not already being repaired
	 */
	public boolean repairPump(int pumpID) {
		return plant.repairPump(pumpID);
	}
	
	/**
	 * Start the repair of the operating software if it has failed
	 * 
	 * @return true only if the operating software has failed and is not already being repaired
	 */
	public boolean repairOperatingSoftware() {
		return plant.repairOperatingSoftware();
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
		try {
			plant.step(numSteps);
		} catch (GameOverException e) {
			gameOver();
		}
	}
	
	// ----------------		Methods used in systemText (TextUI class)	----------------
	/**
	 * 
	 * @return the current UIData object related to the plant.
	 */
	public UIData getUIData() {
		return this.uidata;
	}
	
	/**
	 * 
	 * @return 
	 */
	public Plant getPlant() {
		return this.plant;
	}
	
	// ----------------		Internal helper methods ------------------
	
	/**
	 * Writes all highscores currently inside plant to a file called "highscores.ser".
	 */
	private void writeHighScores() {
		List<HighScore> highScores = plant.getHighScores();
		FileOutputStream fileOut   = null;
		ObjectOutputStream out = null;
		try {
			fileOut = new FileOutputStream("highscores.ser");
			out = new ObjectOutputStream(fileOut);
			out.writeObject(highScores);
			out.close();
			fileOut.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Loads all saved highscores from a file called "highscore.ser" to plant's highscores list.
	 */
	private void readHighScores() {
		List<HighScore> highScores = null;
		FileInputStream fileIn  = null;
		ObjectInputStream in = null;
		try {
			File f = new File("highscores.ser");
			if(f.exists()) {
				fileIn = new FileInputStream(f);
				in = new ObjectInputStream(fileIn);
				highScores = (List<HighScore>) in.readObject();
				in.close();
				fileIn.close();
				plant.setHighScores(highScores);
			}
		}

		catch (EOFException e)
		{}
		catch (IOException io) {
			io.printStackTrace();
		}
		catch (ClassNotFoundException c) {
			c.printStackTrace();
		}
	}
	
	/**
	 * Sets the game over state of the plant and adds the score of the player to "highscores" if it's big enough.
	 */
	private void gameOver() {
		plant.gameOver();
		HighScore highScore = new HighScore(plant.getOperatorName(), plant.getScore());
		addHighScore(highScore);
	}
	
	// ------------		Update Plant Flow Methods	------------

	
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
	 * Moves water out of the condenser and into the reactor due to the flow in and
	 * out of the components.
	 */
	private void moveWater()
	{
		Condenser condenser = this.plant.getCondenser();
		Reactor reactor = this.plant.getReactor();
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

	/**
	 * Forcefully removes steam from the reactor and places it into the condenser.
	 * Based upon the flow! :) 
	 */
	private void moveSteam()
	{
		Reactor reactor = this.plant.getReactor();
		Condenser condenser = this.plant.getCondenser();
		reactor.removeSteam(reactor.getFlowOut().getRate());
		condenser.addSteam(condenser.getInput().getFlowOut().getRate(), condenser.getInput().getFlowOut().getTemperature());
	}

	/**
	 * Resets all ConnectorPipe paths to unblocked.
	 * We do this to all ConnectorPipes at the beginning of each updatePlant()
	 * before propagating the blockages since valves can change state between 
	 * steps.
	 */
	private void setAllConnectorPipesUnblocked() {
		for (ConnectorPipe cp : this.plant.getConnectorPipes()) {
			cp.resetState();
		}
	}
	
	/**
	 * Iterates through all valves in the system and if they are closed we
	 * propagate the blockage through to the next preceding ConnectorPipe.
	 */
	private void blockFromValves() {
		List<Valve> valves = this.plant.getValves();
		for (Valve v : valves) {
			if (!v.isOpen()) blockToPrecedingConnectorPipe(v);
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
		List<ConnectorPipe> connectorPipes = this.plant.getConnectorPipes();
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
		for (PlantComponent pc : this.plant.getPlantComponents()) {
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
		Reactor reactor = this.plant.getReactor();
		Condenser condenser = this.plant.getCondenser();
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
	 * Sums up the maximum flow possible through all valves that have a clear backward
	 * path to the reactor and if this maximum flow is greater than the amount of steam 
	 * wanting to come out of the reactor due to pressue, the rate is limited. 
	 * 
	 * @param reactor the reactor to limit
	 */
	private void limitReactorFlowDueToValveMaxFlow(Reactor reactor)
	{
		int maxFlow = 0;
		for (Valve v : this.plant.getValves()) {
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
	 * Calculate and return the flow of steam out of the reactor due to the difference in
	 * steam volume between the reactor and condenser.
	 * 
	 * This method ignores any blockages, these are dealt with when the flow is propagated
	 * around the system.
	 *  
	 * @return rate of flow of steam out of the reactor
	 */
	private int calcReactorFlowOut() {
		int steamDifference = Math.abs(plant.getReactor().getSteamVolume() - plant.getCondenser().getSteamVolume());
		return Math.min(steamDifference, Math.min(plant.getReactor().getSteamVolume(), Reactor.getMaxSteamFlowRate()));
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
		List<ConnectorPipe> connectorPipes = this.plant.getConnectorPipes();
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
	 * Set's off the propagation from the condenser to the next ConnectorPipe from 
	 * it's output.
	 */
	private void propagateFlowFromCondenser()
	{
		Condenser condenser = this.plant.getCondenser();
		condenser.getFlowOut().setTemperature(condenser.getTemperature());
		propagateFlowToNextConnectorPipe(condenser);
	}

	/**
	 * Tracks back from a pump and if there is a clear path to the condenser
	 * adds the flow increase at this pump to the flow out of the condenser.
	 * 
	 * This method does not support multiple condensers.
	 */
	private void propagateFlowFromPumpsToCondenser()
	{
		Condenser condenser = this.plant.getCondenser();
		// Iterate through all pumps and start tracking back through the system
		for (Pump p : this.plant.getPumps()) {
			// If the pump is broken, move onto the next one.
			if (!this.plant.getFailedComponents().contains(p) && p.getInput() != null) {
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
		Condenser condenser = this.plant.getCondenser();
		condenser.getFlowOut().setRate(condenser.getFlowOut().getRate() + flowRate);
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
}
