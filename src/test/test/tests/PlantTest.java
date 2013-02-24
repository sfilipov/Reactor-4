package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import model.HighScore;
import model.Plant;

import org.junit.Before;
import org.junit.Test;

import pcomponents.PlantComponent;

import simulator.PlantController;
import simulator.ReactorUtils;

public class PlantTest {
	
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
	public void testOperatorName() {
		
		plant.setOperatorName("Bob");
		
		assertEquals("Result", "Bob", plant.getOperatorName());
		
	}
	
	@Test
	public void testSetPaused() {
		
		plant.setPaused(true);
		
		assertTrue("Result", plant.isPaused());
		
	}
	
	@Test
	public void testGetReactor() {
		
		assertNotNull(plant.getReactor());
		
	}
	
	@Test
	public void testGetCondenser() {
		
		assertNotNull(plant.getCondenser());
		
	}
	
	@Test
	public void testGetTurbine() {
		
		assertNotNull(plant.getTurbine());
		
	}
	
	@Test
	public void testGetGenerator() {
		
		assertNotNull(plant.getGenerator());
		
	}
	
	@Test
	public void testSetGetHighScores() {
		
		List<HighScore> highScores = new ArrayList<HighScore>();
		highScores.add(new HighScore("Bob", 345));
		highScores.add(new HighScore("George", 1023));
		
		plant.setHighScores(highScores);
		
		assertEquals("Result", highScores, plant.getHighScores());
		
	}
	
	@Test
	public void testUpdateTimeStepsUsed() {
		
		int timeStepsUsed = plant.getTimeStepsUsed();
		int expected = timeStepsUsed + 10;
		
		plant.updateTimeStepsUsed(10);
		
		assertEquals("Result", expected, plant.getTimeStepsUsed());
		
	}
	
	@Test
	public void testAddFailedComponents() {
		
		// add a component to the failed component list
		PlantComponent component = plant.getPlantComponents().get(0);
		plant.addFailedComponent(component);
		
		// the failed component list should now contain the above component
		List<PlantComponent> expectedList = new ArrayList<PlantComponent>();
		expectedList.add(component);
		
		// get the failed component list
		List<PlantComponent> failedComponents = plant.getFailedComponents();
		
		// does the failed component list match the expected list?
		assertEquals("Result", expectedList, failedComponents);
		
	}
	
	@Test
	public void testGameOver() {
		
		plant.gameOver();
		
		assertTrue(plant.isGameOver());
		
	}

}
