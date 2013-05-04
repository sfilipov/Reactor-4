package model;

import java.util.List;

public interface Model {
	
	//Methods relating to the game in general
	
	public void newSingleplayerGame(String playerOneName);
	
	public void newMultiplayerGame(String playerOneName, String playerTwoName);
	
	public void saveGame();
	
	public void loadGame();
	
	public void setPlayerOneName(String playerOneName);
	
	public void setPlayerTwoName(String playerTwoName);
	
	public String getPlayerOneName();
	
	public String getPlayerTwoName();
	
	public int getPlayerOneScore();
	
	public int getPlayerTwoScore();
	
	public List<HighScore> getHighScores();
	
	public void step(int numSteps);
	
	//Methods controlling plant's components
	
	public void setControlRods(int percentageLowered);
	
	public void setPumpRpm(int pumpID, int rpm);
	
	public void setValve(int valveID, boolean open);
	
	public void repairPump(int pumpID);
	
	public void repairTurbine();
	
	public void repairOperatingSoftware();
	
	public void quenchReactor();
	
	//Methods used during multiplayer to fail a component
	
	public void failPump(int pumpID);
	
	public void failTurbine();
	
	public void failOS();
	
	//Methods giving information about randomly failing components
	
	public int getPumpRpm(int pumpID);
	
	public boolean isValveOpen(int ValveID);
	
	public boolean isPumpOperational(int pumpID);
	
	public boolean isTurbineOperational();
	
	public boolean isSoftwareOperational();
	
	//Methods giving information about the reactor
	
	public int getControlRodsLevel();
	
	public int getReactorTemperature();
	
	public int getReactorPressure();
	
	public int getReactorWaterVolume();
	
	public int getReactorHealth();
	
	//Methods giving information about the condenser
	
	public int getCondenserTemperature();
	
	public int getCondenserPressure();
	
	public int getCondenserWaterVolume();
	
	public int getCondenserHealth();
	
}
