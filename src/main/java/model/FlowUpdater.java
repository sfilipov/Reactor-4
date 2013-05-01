package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import components.Condenser;
import components.ConnectorPipe;
import components.PlantComponent;
import components.Pump;
import components.Reactor;
import components.Valve;

public class FlowUpdater implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Plant plant;
	
	public FlowUpdater(Plant plant) {
		this.plant = plant;
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
	public void updateFlow() {
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
	 * Resets all ConnectorPipe paths to unblocked.
	 * We do this to all ConnectorPipes at the beginning of each updatePlant()
	 * before propagating the blockages since valves can change state between 
	 * steps.
	 */
	private void setAllConnectorPipesUnblocked() {
		for (ConnectorPipe cp : plant.getConnectorPipes()) {
			cp.resetState();
		}
	}
	
	/**
	 * Iterates through all valves in the system and if they are closed we
	 * propagate the blockage through to the next preceding ConnectorPipe.
	 */
	private void blockFromValves() {
		List<Valve> valves = plant.getValves();
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
		List<ConnectorPipe> connectorPipes = plant.getConnectorPipes();
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
		for (PlantComponent pc : plant.getPlantComponents()) {
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
		Reactor reactor = plant.getReactor();
		Condenser condenser = plant.getCondenser();
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
		int steamDifference = Math.abs(plant.getReactor().getSteamVolume() - plant.getCondenser().getSteamVolume());
		return Math.min(steamDifference, Math.min(plant.getReactor().getSteamVolume(), Reactor.getMaxSteamFlowRate()));
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
		for (Valve v : plant.getValves()) {
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
		Condenser condenser = plant.getCondenser();
		// Iterate through all pumps and start tracking back through the system
		for (Pump p : plant.getPumps()) {
			// If the pump is broken, move onto the next one.
			if (!plant.getFailedComponents().contains(p) && p.getInput() != null) {
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
		Condenser condenser = plant.getCondenser();
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
		Condenser condenser = plant.getCondenser();
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
		List<ConnectorPipe> connectorPipes = plant.getConnectorPipes();
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
		Reactor reactor = plant.getReactor();
		Condenser condenser = plant.getCondenser();
		reactor.removeSteam(reactor.getFlowOut().getRate());
		condenser.addSteam(condenser.getInput().getFlowOut().getRate(), condenser.getInput().getFlowOut().getTemperature());
	}
	
	/**
	 * Moves water out of the condenser and into the reactor due to the flow in and
	 * out of the components.
	 */
	private void moveWater()
	{
		Condenser condenser = plant.getCondenser();
		Reactor reactor = plant.getReactor();
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
