package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import components.GameOverException;

public class MultiplayerModel implements Model, Observable, Serializable {
	
	private Plant plantOne;
	private Plant plantTwo;
	private Plant currentlyPlaying;
	
	private GamePersistence persistence;
	
	private int stepCount;
	private boolean multiplayer;
	private boolean gameOver;
	
	private List<Observer> observers;
	
	public MultiplayerModel() {
		plantOne = new Plant();
		plantTwo = new Plant();
		persistence = new MultiplayerPersistenceManager(this);
		gameOver = false;
		observers = new ArrayList<Observer>();
	}
	
	public void copy(MultiplayerModel model) {
		this.plantOne = model.plantOne;
		this.plantTwo = model.plantTwo;
		this.currentlyPlaying = model.currentlyPlaying;
		
		this.stepCount = model.stepCount;
		this.multiplayer = model.multiplayer;
		this.gameOver = model.gameOver;
	}

	@Override
	public void newSingleplayerGame(String playerOneName) {
		plantOne.newGame(playerOneName);
		currentlyPlaying = plantOne;
		stepCount = 0;
		multiplayer = false;
		gameOver = false;
	}

	@Override
	public void newMultiplayerGame(String playerOneName, String playerTwoName) {
		plantOne.newGame(playerOneName);
		plantTwo.newGame(playerTwoName);
		currentlyPlaying = plantTwo;
		stepCount = 0;
		multiplayer = true;
		gameOver = false;
	}

	@Override
	public void saveGame() {
		persistence.saveGame();
	}

	@Override
	public void loadGame() {
		persistence.loadGame();
	}

	@Override
	public String getPlayerOneName() {
		return plantOne.getOperatorName();
	}

	@Override
	public String getPlayerTwoName() {
		return plantTwo.getOperatorName();
	}

	@Override
	public int getPlayerOneScore() {
		return plantOne.getScore();
	}

	@Override
	public int getPlayerTwoScore() {
		return plantTwo.getScore();
	}

	@Override
	public List<HighScore> getHighScores() {
		return persistence.getHighScores();
	}

	@Override
	public void step(int numSteps) {
		stepCount += numSteps;
		try {
			currentlyPlaying.step(numSteps);
		} catch (GameOverException e) {
			gameOver();
		}
		
		if(multiplayer && stepCount > 50 && !gameOver) {
			swapPlayers();
		}
	}

	@Override
	public void setControlRods(int percentageLowered) {
		currentlyPlaying.setControlRods(percentageLowered);
	}

	@Override
	public void setPumpRpm(int pumpID, int rpm) {
		currentlyPlaying.setPumpRpm(pumpID, rpm);

	}

	@Override
	public void setValve(int valveID, boolean open) {
		currentlyPlaying.setValve(valveID, open);
	}

	@Override
	public void repairPump(int pumpID) {
		currentlyPlaying.repairPump(pumpID);
	}

	@Override
	public void repairTurbine() {
		currentlyPlaying.repairTurbine();
	}

	@Override
	public void repairOperatingSoftware() {
		currentlyPlaying.repairOperatingSoftware();
	}

	@Override
	public void quenchReactor() {
		currentlyPlaying.quenchReactor();
	}
	
	@Override
	public void failPump(int pumpID) {
		//TODO Insert conditional logic
		currentlyPlaying.failPump(pumpID);
	}

	@Override
	public void failTurbine() {
		//TODO Insert conditional logic
		currentlyPlaying.failTurbine();
	}

	@Override
	public void failOS() {
		//TODO Insert conditional logic
		currentlyPlaying.failOS();
	}
	
	@Override
	public int getPumpRpm(int pumpID) {
		return currentlyPlaying.getPumpRpm(pumpID);
	}

	@Override
	public boolean isValveOpen(int valveID) {
		return currentlyPlaying.isValveOpen(valveID);
	}

	@Override
	public boolean isPumpOperational(int pumpID) {
		return currentlyPlaying.isPumpOperational(pumpID);
	}

	@Override
	public boolean isTurbineOperational() {
		return currentlyPlaying.isTurbineOperational();
	}

	@Override
	public boolean isSoftwareOperational() {
		return currentlyPlaying.isSoftwareOperational();
	}

	@Override
	public int getControlRodsLevel() {
		return currentlyPlaying.getControlRodsLevel();
	}

	@Override
	public int getReactorTemperature() {
		return currentlyPlaying.getReactorTemperature();
	}

	@Override
	public int getReactorPressure() {
		return currentlyPlaying.getReactorPressure();
	}

	@Override
	public int getReactorWaterVolume() {
		return currentlyPlaying.getReactorWaterVolume();
	}

	@Override
	public int getReactorHealth() {
		return currentlyPlaying.getReactorHealth();
	}

	@Override
	public int getCondenserTemperature() {
		return currentlyPlaying.getCondenserTemperature();
	}

	@Override
	public int getCondenserPressure() {
		return currentlyPlaying.getCondenserPressure();
	}

	@Override
	public int getCondenserWaterVolume() {
		return currentlyPlaying.getCondenserWaterVolume();
	}

	@Override
	public int getCondenserHealth() {
		return currentlyPlaying.getCondenserHealth();
	}
	
	@Override
	public void addObserver(Observer o) {
		observers.add(o);
	}

	@Override
	public void removeObserver(Observer o) {
		observers.remove(o);
	}

	@Override
	public void notifyObservers() {
		for (Observer o : observers) {
			o.update();
		}
	}
	
	private void gameOver() {
		gameOver = true;
		HighScore highScore = new HighScore(currentlyPlaying.getOperatorName(), currentlyPlaying.getScore());
		persistence.addHighScore(highScore);
	}
	
	private void swapPlayers() {
		//TODO Insert swapPlayers logic
	}


}
