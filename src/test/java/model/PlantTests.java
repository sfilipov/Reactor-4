package model;

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
