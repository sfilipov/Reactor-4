package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import components.Condenser;
import components.PlantComponent;
import components.Reactor;
import components.Turbine;

public class PlantTests {
	
	private Plant plant;

	@Before
	public void setUp() {
		plant = new Plant();
	}
	
	@Test
	public void getReactor_noReactor_returnsNull() {
		assertNull(plant.getReactor());
	}
	
	@Test
	public void getReactor_reactorExists_returnsTheReactor() {
		ArrayList<PlantComponent> components = new ArrayList<PlantComponent>();
		Reactor reactor = new Reactor();
		components.add(reactor);
		plant.setPlantComponents(components);
		
		assertSame(reactor, plant.getReactor());
	}
	
	@Test
	public void getCondenser_noCondenser_returnsNull() {
		assertNull(plant.getCondenser());
	}
	
	@Test
	public void getCondenser_condenserExists_returnsTheCondenser() {
		ArrayList<PlantComponent> components = new ArrayList<PlantComponent>();
		Condenser condenser = new Condenser(null);
		components.add(condenser);
		plant.setPlantComponents(components);
		
		assertSame(condenser, plant.getCondenser());
	}
	
	@Test
	public void getTurbine_noTurbine_returnsNull() {
		assertNull(plant.getTurbine());
	}
	
	@Test
	public void getTurbine_turbineExists_returnsTheTurbine() {
		ArrayList<PlantComponent> components = new ArrayList<PlantComponent>();
		Turbine turbine = new Turbine(0);
		components.add(turbine);
		plant.setPlantComponents(components);
		
		assertSame(turbine, plant.getTurbine());
	}
	
	@Test
	public void getHighScores_noHighScores_returnsEmptyList() {
		assertTrue(plant.getHighScores().isEmpty());
	}
	
	@Test 
	public void getHighScores_aHighScore_returnsTheHighScore() {
		List<HighScore> highScores = new ArrayList<HighScore>();
		HighScore highScore = new HighScore("Alice", 10000);
		highScores.add(highScore);
		plant.setHighScores(highScores);
		
		assertSame(highScore, plant.getHighScores().get(0));
		assertTrue(plant.getHighScores().size() == 1);
	}
	
	//To be implemented soon.
//	@Test 
//	public void getHighScores_twoHighScores_returnsSortedHighScores() {
//		List<HighScore> highScores = new ArrayList<HighScore>();
//		HighScore smallerHighScore = new HighScore("Alice", 10000);
//		HighScore biggerHighScore = new HighScore("Bob", 20000);
//		highScores.add(smallerHighScore);
//		highScores.add(biggerHighScore);
//		plant.setHighScores(highScores);
//		
//		assertSame(biggerHighScore, plant.getHighScores().get(0));
//		assertSame(smallerHighScore, plant.getHighScores().get(1));
//		assertTrue(plant.getHighScores().size() == 2);
//	}
}
