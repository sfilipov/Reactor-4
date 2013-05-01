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
		// TODO Auto-generated method stub
		// TODO Add step method to Plant
//		currentlyPlaying.step(int numSteps);
	}

	@Override
	public void setControlRods(int percentageLowered) {
//		currentlyPlaying.setControlRods(percentageLowered);
	}

	@Override
	public void setPumpRpm(int pumpID, int rpm) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValve(int valveID, boolean open) {
		// TODO Auto-generated method stub

	}

	@Override
	public void repairPump(int pumpID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void repairTurbine() {
		// TODO Auto-generated method stub

	}

	@Override
	public void repairOperatingSoftware() {
		// TODO Auto-generated method stub

	}

	@Override
	public void quenchReactor() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getPumpRpm(int pumpID) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isValveOpen(int ValveID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPumpOperational(int pumpID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTurbineOperational() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSoftwareOperational() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getControlRodsLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getReactorTemperature() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getReactorPressure() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getReactorWaterLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getReactorHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCondenserTemperature() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCondenserPressure() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCondenserWaterLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCondenserHealth() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
