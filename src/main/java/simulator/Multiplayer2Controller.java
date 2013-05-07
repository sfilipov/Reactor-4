package simulator;

import java.util.List;
import java.util.Random;

import components.OperatingSoftware.OperationRequest;

import model.HighScore;
import model.Model;
import model.Observable;
import model.Observer;

public class Multiplayer2Controller {
	private Model model;
	
	public Multiplayer2Controller(Model model) {
		this.model = model;
	}
	
	public void newSingleplayerGame(String playerOneName) {
		model.newSingleplayerGame(playerOneName);
	}
	
	public void newMultiplayerGame(String playerOneName, String playerTwoName) {
		model.newMultiplayerGame(playerOneName, playerTwoName);
	}
	
	public void saveGame() {
		model.saveGame();
	}
	
	public void loadGame() {
		model.loadGame();
	}
	
	public int getCurrentPlayerNumber() {
		return model.getCurrentPlayerNumber();
	}
	
	public void setPlayerOneName(String playerOneName) {
		model.setPlayerOneName(playerOneName);
	}
	
	public void setPlayerTwoName(String playerTwoName) {
		model.setPlayerTwoName(playerTwoName);
	}
	
	public boolean toggleRandomFailures() {
		if (model.isRandomFailures()) {
			model.setRandomFailures(false);
			return false;
		} else {
			model.setRandomFailures(true);
			return true;
		}
	}
	
	public int getStepsPerPlayer() {
		return model.getStepsPerPlayer();
	}
	
	public boolean isRandomFailures() {
		return model.isRandomFailures();
	}
	
	public String getPlayerOneName() {
		return model.getPlayerOneName();
	}
	
	public String getPlayerTwoName() {
		return model.getPlayerTwoName();
	}
	
	public String getCurrentPlayerName() {
		return model.getCurrentPlayerName();
	}
	
	public int getPlayerOneScore() {
		return model.getPlayerOneScore();
	}
	
	public int getPlayerTwoScore() {
		return model.getPlayerTwoScore();
	}
	
	public int getCurrentPlayerScore() {
		return model.getCurrentPlayerScore();
	}
	
	public boolean isMultiplayer() {
		return model.isMultiplayer();
	}
	
	public boolean isGameOver() {
		return model.isGameOver();
	}
	
	public List<HighScore> getHighScores() {
		return model.getHighScores();
	}
	
	public void step(int numSteps) {
		model.step(numSteps);
	}
	
	public int numberOfStepsUntilSwap() {
		return model.getStepsLeftOfTurn();
	}
	
	public void setControlRods(int percentageLowered) {
		if (model.isSoftwareOperational() || model.isMultiplayer()) {
			model.setControlRods(percentageLowered);
		} else {
			failedSoftwareResponse();
		}
	}
	
	public void setPumpRpm(int pumpID, int rpm) {
		if (model.isSoftwareOperational() || model.isMultiplayer()) {
			model.setPumpRpm(pumpID, rpm);
		} else {
			failedSoftwareResponse();
		};
	}
	
	public void setValve(int valveID, boolean open) {
		if (model.isSoftwareOperational() || model.isMultiplayer()) {
			model.setValve(valveID, open);
		} else {
			failedSoftwareResponse();
		};
	}
	
	public void repairPump(int pumpID) {
		if (model.isSoftwareOperational() || model.isMultiplayer()) {
			model.repairPump(pumpID);
		} else {
			failedSoftwareResponse();
		};
	}
	
	public void repairTurbine() {
		if (model.isSoftwareOperational() || model.isMultiplayer()) {
			model.repairTurbine();
		} else {
			failedSoftwareResponse();
		};
	}
	
	public void repairOperatingSoftware() {
		model.repairOperatingSoftware();
	}
	
	public void quenchReactor() {
		model.quenchReactor();
	}
	
	public void failPump(int pumpID) {
		model.failPump(pumpID);
	}
	
	public void failTurbine() {
		model.failTurbine();
	}
	
	public void failOS() {
		model.failOS();
	}
	
	//Methods giving information about randomly failing components
	
	public int getPumpRpm(int pumpID) {
		return model.getPumpRpm(pumpID);
	}
	
	public boolean isValveOpen(int ValveID) {
		return model.isValveOpen(ValveID);
	}
	
	public boolean isPumpOperational(int pumpID) {
		return model.isPumpOperational(pumpID);
	}
	
	public boolean isTurbineOperational() {
		return model.isTurbineOperational();
	}
	
	public boolean isSoftwareOperational() {
		return model.isSoftwareOperational();
	}

	public boolean isPumpBeingRepaired(int pumpID) {
		return model.isPumpBeingRepaired(pumpID);
	}
	
	public boolean isTurbineBeingRepaired() {
		return model.isTurbineBeingRepaired();
	}
	
	public boolean isSoftwareBeingRepaired() {
		return model.isSoftwareBeingRepaired();
	}
	//Methods giving information about the reactor
	
	public int getControlRodsLevel() {
		return model.getControlRodsLevel();
	}
	
	public int getReactorTemperature() {
		return model.getReactorTemperature();
	}
	
	public int getReactorPressure() {
		return model.getReactorPressure();
	}
	
	public int getReactorWaterVolume() {
		return model.getReactorWaterVolume();
	}
	
	public int getReactorHealth() {
		return model.getReactorHealth();
	}
	
	public boolean isQuenchAvailable() {
		return model.isQuenchAvailable();
	}
	
	//Methods giving information about the condenser
	
	public int getCondenserTemperature() {
		return model.getCondenserTemperature();
	}
	
	public int getCondenserPressure() {
		return model.getCondenserPressure();
	}
	
	public int getCondenserWaterVolume() {
		return model.getCondenserWaterVolume();
	}
	
	public int getCondenserHealth() {
		return model.getCondenserHealth();
	}
	
    /**
     *  If the operating software has failed, this method either stores no command or
     *  stores a random command and generates its data.
     */
    private void failedSoftwareResponse()
    {
    	Random random = new Random();
        int swapWith = 1 + random.nextInt(OperationRequest.values().length - 2);
        
        switch(OperationRequest.values()[swapWith])
        {
            case SetControlRods: 
                model.setControlRods(random.nextInt(101));
                break;
                
            case SetPumpRpm:
                model.setPumpRpm(1 + random.nextInt(3), random.nextInt(1001));
                break;
                
            case SetValve:
                model.setValve(1 + random.nextInt(2), random.nextBoolean());
                break;
                
            case RepairTurbine:
                model.repairTurbine();
                break;
                
            case RepairPump:
            	model.repairPump(1 + random.nextInt(2));
                break;
                
            default:
                break;
    	}
    }
}
