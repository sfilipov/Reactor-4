package components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.FlowType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import components.ConnectorPipe;
import components.PlantComponent;

public class ConnectorPipeTests {

	// Main ConnectorPipe object for testing.
	ConnectorPipe connectorPipe;

	@Before
	public void setUp() {
		connectorPipe = new ConnectorPipe();
	}

	@After
	public void tearDown() {
		connectorPipe = null;
	}

	@Test
	public void getInputs_shouldReturnCorrectInputs() {

		PlantComponent newInput = new Pump(1);

		connectorPipe.addInput(newInput);

		assertSame(newInput, connectorPipe.getInputs().get(0));
	}

	/*
	 * Asserts that all connected inputs are included in the list returned by
	 * getInputs(). However the order of the objects does not matter.
	 */
	@Test
	public void getInputs_shouldReturnAllConnectedInputs() {

		List<PlantComponent> expectedInputs = setupMultipleInputs();

		assertTrue(connectorPipe.getInputs().containsAll(expectedInputs));
	}

	/*
	 * Asserts that there should not be any extra objects included in the list
	 * of inputs returned by getInputs.
	 */
	@Test
	public void getInputs_shouldReturnExactNumberOfInputs() {
		List<PlantComponent> expectedInputs = setupMultipleInputs();

		assertEquals(expectedInputs.size(), connectorPipe.getInputs().size());
	}

	@Test
	public void getOutputs_shouldReturnCorrectOutputs() {

		PlantComponent newOutput = new Pump(99);

		connectorPipe.addOutput(newOutput);

		assertSame(newOutput, connectorPipe.getOutputs().get(0));
	}

	/*
	 * Asserts that all connected outputs are included in the list returned by
	 * getOutputs(). However the order of the objects does not matter.
	 */
	@Test
	public void getOutputs_shouldReturnAllOutputs() {

		List<PlantComponent> expectedOutputs = setupMultipleOutputs();

		assertTrue(connectorPipe.getOutputs().containsAll(expectedOutputs));

	}

	@Test
	public void getOutputs_shouldReturnExactNumberOfOutputs() {

		List<PlantComponent> expectedOutputs = setupMultipleOutputs();

		// Assert that there should not be any extra objects in the list.
		assertEquals(expectedOutputs.size(), connectorPipe.getOutputs().size());

	}

	@Test
	public void getOutputsMap_allOutputsShouldInitiallyBeUnblocked() {

		setupMultipleOutputs();

		// Assert that every boolean in a fresh outputs map should be false.
		// Hence, all outputs are initially unblocked.
		for (Boolean blocked : connectorPipe.getOutputsMap().values()) {
			assertTrue(!blocked);
		}
		
	}

	@Test
	public void getOutputsMap_shouldReturnMapContainingAllOutputs() {
		
		List<PlantComponent> expectedOutputs = setupMultipleOutputs();

		Map<PlantComponent, Boolean> outputsMap = connectorPipe.getOutputsMap();
		Set<PlantComponent> plantComponentsInMap = outputsMap.keySet();

		assertTrue(plantComponentsInMap.containsAll(expectedOutputs));
	}

	@Test
	public void setComponentBlocked_shouldActuallyBlockComponent() {
		
		List<PlantComponent> outputs = setupMultipleOutputs();
		Map<PlantComponent, Boolean> outputsMap;
		// Assume that initially, all outputs are unblocked.
		// This is tested in getOutputsMap_allOutputsShouldInitiallyBeUnblocked.
		
		for (PlantComponent pc : outputs) {
			connectorPipe.setComponentBlocked(pc);
			outputsMap = connectorPipe.getOutputsMap();
			assertTrue(outputsMap.get(pc));
		}
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void setComponentBlocked_shouldThrowExceptionIfComponentIsNotAnOutput() {
		
		setupMultipleOutputs();
		
		PlantComponent notAConnectedOutput = new Pump(50);
		
		connectorPipe.setComponentBlocked(notAConnectedOutput);
	}
	
	@Test
	public void numOutputs_shouldReturnNumberOfUnblockedOutputs() {
		
		List<PlantComponent> outputs = setupMultipleOutputs();
		
		// Assume that initially, all outputs are unblocked.
		// This is tested in getOutputsMap_allOutputsShouldInitiallyBeUnblocked.
		int numUnblockedOutputs = outputs.size();
		
		assertEquals(numUnblockedOutputs, connectorPipe.numOutputs());
		
		// Block each output in turn and check that there is 1 less open
		// output each time.
		for (PlantComponent pc : outputs) {
			connectorPipe.setComponentBlocked(pc);
			numUnblockedOutputs--;
			assertEquals(numUnblockedOutputs, connectorPipe.numOutputs());
		}
		
		assertEquals(0, connectorPipe.numOutputs());
		
	}

	@Test
	public void resetState_shouldSetAllOutputsToUnblocked() {
		
		List<PlantComponent> outputs = setupMultipleOutputs();
		
		// Block all output paths.
		for (PlantComponent pc : outputs) {
			connectorPipe.setComponentBlocked(pc);
		}
		
		connectorPipe.resetState();
		
		// Assert that all output paths are now unblocked.
		for (Boolean blocked : connectorPipe.getOutputsMap().values()) {
			assertTrue(!blocked);
		}
		
	}

	// --------- Helper Methods :) ---------
	
	/*
	 * Connects several components to cp as outputs, and returns a list 
	 * of those components.
	 * 
	 * returns a list of components that have just been connected to the
	 * ConnectorPipe, cp, as outputs.
	 */
	private List<PlantComponent> setupMultipleOutputs() {
		PlantComponent newOutputA = new Valve(99, FlowType.Water);
		PlantComponent newOutputB = new Reactor();
		PlantComponent newOutputC = new Pump(88);

		connectorPipe.addOutput(newOutputA);
		connectorPipe.addOutput(newOutputB);
		connectorPipe.addOutput(newOutputC);

		List<PlantComponent> expectedOutputList = new ArrayList<PlantComponent>();
		expectedOutputList.add(newOutputA);
		expectedOutputList.add(newOutputB);
		expectedOutputList.add(newOutputC);

		return expectedOutputList;
	}

	private List<PlantComponent> setupMultipleInputs() {
		PlantComponent newInputA = new Valve(99, FlowType.Water);
		PlantComponent newInputB = new Pump(99);
		PlantComponent newInputC = new Reactor();

		connectorPipe.addInput(newInputA);
		connectorPipe.addInput(newInputB);
		connectorPipe.addInput(newInputC);

		List<PlantComponent> expectedInputList = new ArrayList<PlantComponent>();
		expectedInputList.add(newInputA);
		expectedInputList.add(newInputB);
		expectedInputList.add(newInputC);
		return expectedInputList;
	}

}
