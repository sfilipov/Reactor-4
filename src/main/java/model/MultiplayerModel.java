package model;

import java.io.Serializable;
import java.util.List;

public class MultiplayerModel implements Model, Serializable {
	
	private Plant plantOne;
	private Plant plantTwo;
	private Plant currentlyPlaying;
	
	private GamePersistence persistence;
	
	public MultiplayerModel() {
		plantOne = new Plant();
		plantTwo = new Plant();
		persistence = new MultiplayerPersistenceManager(this);
	}
	
	public void copy(MultiplayerModel model) {
		this.plantOne = model.plantOne;
		this.plantTwo = model.plantTwo;
		this.currentlyPlaying = model.currentlyPlaying;
	}

	@Override
	public void newSingleplayerGame(String playerOneName) {
		plantOne.newGame(playerOneName);
		currentlyPlaying = plantOne;
	}

	@Override
	public void newMultiplayerGame(String playerOneName, String playerTwoName) {
		plantOne.newGame(playerOneName);
		plantTwo.newGame(playerTwoName);
		currentlyPlaying = plantTwo;
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
		currentlyPlaying.step(numSteps);
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
	
	
}
