package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import components.GameOverException;
import components.OperatingSoftware;
import components.Pump;
import components.Turbine;

public class MultiplayerModel implements Model, Observable, Serializable {
	
	private static final int STEPS_PER_PLAYER = 100; // Number of steps each player plays until the swapping. 
	
	private Plant plantOne;
	private Plant plantTwo;
	private Plant currentlyPlaying;
	
	private GamePersistence persistence;
	
	private int stepCount;
	private boolean multiplayer;
	//private boolean gameOver;
	
	private List<Observer> observers;
	
	public MultiplayerModel() {
		plantOne = new Plant();
		plantTwo = new Plant();
		persistence = new MultiplayerPersistenceManager(this);
		//gameOver = false;
		observers = new ArrayList<Observer>();
	}
	
	public void copy(MultiplayerModel model) {
		this.plantOne = model.plantOne;
		this.plantTwo = model.plantTwo;
		this.currentlyPlaying = model.currentlyPlaying;
		
		this.stepCount = model.stepCount;
		this.multiplayer = model.multiplayer;
		//this.gameOver = model.gameOver;
		
		notifyObservers();
	}

	@Override
	public void newSingleplayerGame(String playerOneName) {
		plantOne.newGame(playerOneName);
		currentlyPlaying = plantOne;
		stepCount = 0;
		multiplayer = false;
		//gameOver = false;
		currentlyPlaying.setRandomFailures(true);
		notifyObservers();
	}

	@Override
	public void newMultiplayerGame(String playerOneName, String playerTwoName) {
		plantOne.newGame(playerOneName);
		plantTwo.newGame(playerTwoName);
		currentlyPlaying = plantOne;
		stepCount = 0;
		multiplayer = true;
		currentlyPlaying.setRandomFailures(false);
		//gameOver = false;
		notifyObservers();
	}

	@Override
	public void saveGame() {
		persistence.saveGame();
	}

	@Override
	public void loadGame() {
		persistence.loadGame();
		notifyObservers();
	}
	
	@Override
	public void setPlayerOneName(String playerOneName) {
		plantOne.setOperatorName(playerOneName);
		notifyObservers();
	}

	@Override
	public void setPlayerTwoName(String playerTwoName) {
		plantTwo.setOperatorName(playerTwoName);
		notifyObservers();
	}
	
	@Override
	public int getCurrentPlayerNumber() {
		return currentlyPlaying.equals(plantOne) ? 1 : 2;
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
	public String getCurrentPlayerName() {
		return currentlyPlaying.getOperatorName();
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
	public int getCurrentPlayerScore() {
		return currentlyPlaying.getScore();
	}
	
	@Override
	public boolean isMultiplayer() {
		return multiplayer;
	}
	
	@Override
	public boolean isGameOver() {
		return currentlyPlaying.isGameOver();
	}
	
	@Override
	public boolean isQuenchAvailable() {
		return currentlyPlaying.isQuenchAvailable();
	}

	public boolean isRandomFailures() {
		return currentlyPlaying.isRandomFailures();
	}

	public void setRandomFailures(boolean randomFailures) {
		currentlyPlaying.setRandomFailures(randomFailures);
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
		} finally {
			swapPlayers();
			notifyObservers();
		}
	}

	@Override
	public void setControlRods(int percentageLowered) {
		currentlyPlaying.setControlRods(percentageLowered);
		notifyObservers();
	}

	@Override
	public void setPumpRpm(int pumpID, int rpm) {
		currentlyPlaying.setPumpRpm(pumpID, rpm);
		notifyObservers();
	}

	@Override
	public void setValve(int valveID, boolean open) {
		currentlyPlaying.setValve(valveID, open);
		notifyObservers();
	}

	@Override
	public void repairPump(int pumpID) {
		currentlyPlaying.repairPump(pumpID);
		notifyObservers();
	}

	@Override
	public void repairTurbine() {
		currentlyPlaying.repairTurbine();
		notifyObservers();
	}

	@Override
	public void repairOperatingSoftware() {
		currentlyPlaying.repairOperatingSoftware();
		notifyObservers();
	}

	@Override
	public void quenchReactor() {
		currentlyPlaying.quenchReactor();
		notifyObservers();
	}
	
	@Override
	public void failPump(int pumpID) {
		//TODO Insert conditional logic
		currentlyPlaying.failPump(pumpID);
		notifyObservers();
	}

	@Override
	public void failTurbine() {
		//TODO Insert conditional logic
		currentlyPlaying.failTurbine();
		notifyObservers();
	}

	@Override
	public void failOS() {
		//TODO Insert conditional logic
		currentlyPlaying.failOS();
		notifyObservers();
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
		//gameOver = true;
		HighScore highScore = new HighScore(currentlyPlaying.getOperatorName(), currentlyPlaying.getScore());
		persistence.addHighScore(highScore);
		notifyObservers();
	}
	
	private void swapPlayers() {
		// assume player 1 starts.
		if(multiplayer && (stepCount >= STEPS_PER_PLAYER || isGameOver())) {
			currentlyPlaying = plantTwo;
			stepCount = 0;
			//gameOver = false;
			notifyObservers();
		}
	}

	@Override
	public boolean isPumpBeingRepaired(int pumpID) {
		List<Repair> repairs = currentlyPlaying.getBeingRepaired();
		for (Repair repair : repairs) {
			if (repair.getPlantComponent() instanceof Pump && ((Pump) repair.getPlantComponent()).getID() == pumpID) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isTurbineBeingRepaired() {
		List<Repair> repairs = currentlyPlaying.getBeingRepaired();
		for (Repair repair : repairs) {
			if (repair.getPlantComponent() instanceof Turbine) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSoftwareBeingRepaired() {
		List<Repair> repairs = currentlyPlaying.getBeingRepaired();
		for (Repair repair : repairs) {
			if (repair.getPlantComponent() instanceof OperatingSoftware) {
				return true;
			}
		}
		return false;
	}

	public int getStepsLeftOfTurn() {
		return STEPS_PER_PLAYER - stepCount;
	}

	public int getStepsPerPlayer() {
		return STEPS_PER_PLAYER;
	}
}
