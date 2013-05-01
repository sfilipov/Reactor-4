package model;

import java.util.List;

public interface GamePersistence {
	
	public void saveGame();
	
	public void loadGame();
	
	public void addHighScore(HighScore highScore);
	
	public List<HighScore> getHighScores();
	
}
