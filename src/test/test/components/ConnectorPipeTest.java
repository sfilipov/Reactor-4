package components;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Plant;

import org.junit.Before;
import org.junit.Test;

import components.ConnectorPipe;
import components.PlantComponent;


import simulator.PlantController;
import simulator.ReactorUtils;

public class ConnectorPipeTest {

	private PlantController presenter; 
	private ReactorUtils utils;
	private Plant plant;

	@Before
	public void setUp() {
		utils = new ReactorUtils();
		presenter = new PlantController(utils);
		plant = presenter.getPlant();
	}
	
	@Test
	public void testGetInputs() {
		
		ConnectorPipe connectorPipe = new ConnectorPipe();
		
		PlantComponent newInput = plant.getPlantComponents().get(0);
		
		connectorPipe.addInput(newInput);
		
		List<PlantComponent> expectedInputList = new ArrayList<PlantComponent>();
		expectedInputList.add(newInput);
		expectedInputList.add(null);
		
		assertEquals("Result", expectedInputList, connectorPipe.getInputs());
		
	}
	
	@Test
	public void testGetOutputs() {
		
		ConnectorPipe connectorPipe = new ConnectorPipe();
		
		PlantComponent newOutput = plant.getPlantComponents().get(0);
		
		connectorPipe.addOutput(newOutput);
		
		Map<PlantComponent, Boolean> expectedOutputMap = new HashMap<PlantComponent, Boolean>();
		expectedOutputMap.put(newOutput, false);
		
		assertEquals("Result", expectedOutputMap, connectorPipe.getOutputsMap());
		
	}
	
	@Test
	public void testNumOutputs() {
		
		ConnectorPipe connectorPipe = new ConnectorPipe();
		
		PlantComponent newOutput1 = plant.getPlantComponents().get(0);
		PlantComponent newOutput2 = plant.getPlantComponents().get(1);
		
		connectorPipe.addOutput(newOutput1);
		connectorPipe.addOutput(newOutput2);
		
		assertEquals("Result", 2, connectorPipe.numOutputs());
		
	}

}
