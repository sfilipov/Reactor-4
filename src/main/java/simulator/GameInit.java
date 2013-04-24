package simulator;

import swing.MainGUI;


/**
 * GameInit class bootstraps the entire game.
 * 
 * It first instantiates a ReactorUtils object that can create 
 * new plant objects.
 * The controller takes a reference to the ReactorUtils object 
 * so that it can get a new Plant if and when they're needed.
 * It also instantiates the UI and gives it a reference to
 * the controller for routing user commands. 
 *
 *@author Lamprey
 */
public class GameInit {	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		ReactorUtils utils = new ReactorUtils();
		PlantController controller = new PlantController(utils);
		MainGUI newView = new MainGUI(controller);
	}
}