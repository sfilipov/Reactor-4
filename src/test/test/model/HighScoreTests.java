package model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HighScoreTests {	
	@Test
	public void compareTo_firstLessThanSecond_returnsNegative() {
		HighScore firstScore  = new HighScore("Alice", 1000);
		HighScore secondScore = new HighScore("Bob", 2000);
		int result = firstScore.compareTo(secondScore);
		
		assertTrue(result < 0);
	}
	
	@Test
	public void compareTo_firstGreaterThanSecond_returnsPositive() {
		HighScore firstScore  = new HighScore("Alice", 2000);
		HighScore secondScore = new HighScore("Bob", 1000);
		int result = firstScore.compareTo(secondScore);
		
		assertTrue(result > 0);
	}
	
	@Test
	public void compareTo_firstEqualToSecond_returnsZero() {
		HighScore firstScore  = new HighScore("Alice", 1000);
		HighScore secondScore = new HighScore("Bob", 1000);
		int result = firstScore.compareTo(secondScore);
		
		assertTrue(result == 0);
	}
}
