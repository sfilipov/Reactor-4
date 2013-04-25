package simulator;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import model.HighScore;
import model.PlantModel;

import org.junit.Before;
import org.junit.Test;

import components.RandomlyFailableComponent;
import components.Pump;
import components.Valve;

public class PlantControllerTests {
	
	private PlantController controller;
	private PlantModel model;

	@Before
	public void setUp() {
		model = new PlantModel();
		controller = new PlantController(model);
		controller.newGame("Bob");
	}
	
	@Test
	public void testLoadGame() {
		
		File f = new File("save.ser");
		if(f.exists()) {
			f.delete();
		}
		
		// no saved game file so should return false
		assertEquals("Result", false, controller.loadGame());
		
		controller.saveGame();
		
		assertEquals("Result", true, controller.loadGame());
		
	}
	
	@Test
	public void testOperatorName() {
		
		assertEquals("Result", "Bob", this.model.getOperatorName());
		
	}
	
	@Test
	public void testTogglePaused() {
		
		boolean isPaused = model.isPaused();
		
		controller.togglePaused();
		
		assertEquals("Result", !isPaused, model.isPaused());
		
	}
	
	@Test
	public void testAddHighScore() {
		
		model.setHighScores(new ArrayList<HighScore>());
		
		HighScore newHighScore = new HighScore("Bob", 2000);
		
		controller.addHighScore(newHighScore);
		
		List<HighScore> highScores = model.getHighScores();
		
		//expected
		List<HighScore> expected = new ArrayList<HighScore>();
		expected.add(newHighScore);
		
		assertEquals("Result", expected, highScores);
		
	}
	
	@Test
	public void testSetValve() {
		
		controller.setValve(1, false);
		
		List<Valve> valves = model.getValves();
		
		assertEquals("Result", false, valves.get(0).isOpen());
		
	}
	
	@Test
	public void testSetPumpOnOff() {
		
		controller.setPumpOnOff(1, false);
		
		List<Pump> pumps = model.getPumps();
		
		assertEquals("Result", false, pumps.get(0).isOn());
		
	}
	
	@Test
	public void testSetPumpRpm() {
		
		controller.setPumpRpm(1, 127);
		
		List<Pump> pumps = model.getPumps();
		
		assertEquals("Result", 127, pumps.get(0).getRpm());
		
	}
	
	@Test
	public void testSetControlRods() {
		
		controller.setControlRods(57);
		
		assertEquals("Result", 57, model.getReactor().getPercentageLowered());
		
	}
	//4.4 4.8
	@Test
	public void testRepairTurbine() {
		
		assertEquals("Result", false, controller.repairTurbine()); // the turbine hasn't failed so repairTurbine() should return false
		
		// break the turbine
		List<RandomlyFailableComponent> failedComponents = model.getFailedComponents();
		failedComponents.add(model.getTurbine());
		
		assertEquals("Result", true, controller.repairTurbine()); // the turbine is now broken so repairTurbine() should return true
		
		assertEquals("Result", false, controller.repairTurbine()); // the turbine is already being repaired so repairTurbine() should return false again
		
	}
	//4.4 4.8
	@Test
	public void testRepairPump() {
		
		assertEquals("Result", false, controller.repairPump(1));
		
		// break the pump
		List<RandomlyFailableComponent> failedComponents = model.getFailedComponents();
		failedComponents.add(model.getPumps().get(0));
		
		assertEquals("Result", true, controller.repairPump(1));
		
		assertEquals("Result", false, controller.repairPump(1));
		
	}

}
