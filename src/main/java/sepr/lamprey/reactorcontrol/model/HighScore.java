package sepr.lamprey.reactorcontrol.model;

import java.io.Serializable;

/**
 * HighScore class is just a pair of String and integer (int). It's purpose is to create
 * records of a player's score after game over. HighScore implements Comparable - objects
 * of type HighScore can be compared based on the value of the integer (highScore).
 * 
 * @author Lamprey
 */
public class HighScore implements Comparable<HighScore>, Serializable {
	/**
	 * serialVersionUID: http://docs.oracle.com/javase/7/docs/api/java/io/Serializable.html
	 */
	private static final long serialVersionUID = 3713046493639284784L;
	
	private String name;
	private int highScore;
	
	/**
	 * Creates a new HighScore pair of string and integer.
	 * @param name        name of the player (operator).
	 * @param highScore   final score (after game over).
	 */
	public HighScore(String name, int highScore) {
		this.name = name;
		this.highScore = highScore;
	}
	
	/**
	 * 
	 * @return name of the player
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @return player's high score
	 */
	public int getHighScore() {
		return highScore;
	}
	
	/**
	 * Compares the current HighScore object to another HighScore.
	 * @return a negative integer if the first object's score is smaller than the other object's score.
	 */
	@Override
	public int compareTo(HighScore h) {
		return this.highScore - h.highScore;
	}
}
