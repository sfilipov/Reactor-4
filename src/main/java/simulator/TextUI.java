package simulator;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;
import javax.swing.text.NavigationFilter;
import javax.swing.text.Position;

import components.PlantComponent;
import components.Pump;
import components.Turbine;
import components.UpdatableComponent;
import components.Valve;


import model.HighScore;



/**
 * This class is used in the team's implementation
 * of a text based game. It represents the "view" of the
 * team's MVC design of the game. It not recommended to use 
 * this class in future implementations of the game. 
 * Most classes are not documented as they are not to
 * be used by other groups.
 */
public class TextUI extends JFrame implements KeyListener 
{
	private static final long serialVersionUID = -8860972241763753423L;
	
	private static final int MAX_TIME_STEPS_PER_COMMAND = 10; // max n for a 'step n' command
	private static final int MAX_NAME_LENGTH = 30;
	
	private final static String START_TEXT = 
			"To start a name game type\t\t: newgame\n"
            +"To load a saved game type\t\t: loadgame\n"
            +"To view highscore type\t\t\t: highscores\n"
            +"To view credits type\t\t\t: credits\n";
	// This looks weird because the backslashes need escaping!
	private final static String REACTOR_ASCII =  
    " _______  _______  _______  _______ _________ _______  _______\n"
    +"(  ____ )(  ____ \\(  ___  )(  ____ \\\\__   __/(  ___  )(  ____ )\n"
    +"| (    )|| (    \\/| (   ) || (    \\/   ) (   | (   ) || (    )|\n"
    +"| (____)|| (__    | (___) || |         | |   | |   | || (____)|\n"
    +"|     __)|  __)   |  ___  || |         | |   | |   | ||     __)\n"
    +"| (\\ (   | (      | (   ) || |         | |   | |   | || (\\ (\n" 
    +"| ) \\ \\__| (____/\\| )   ( || (____/\\   | |   | (___) || ) \\ \\__\n"
    +"|/   \\__/(_______/|/     \\|(_______/   )_(   (_______)|/   \\__/\n";
	
	private final static String MUSHROOM_ASCII = 
	 "                             ____                          \n"
    +"           ____  , -- -        ---   -.                    \n"
    +"        (((   ((  ///   //   '  \\-\\ \\  )) ))            \n"
    +"    ///    ///  (( _        _   -- \\--     \\\\ \\)       \n"
    +" ((( ==  ((  -- ((             ))  )- ) __   ))  )))       \n"
    +"  ((  (( -=   ((  ---  (          _ ) ---  ))   ))         \n"
    +"     (( __ ((    ()(((  \\  / ///     )) __ )))            \n"
    +"            \\_ (( __  |     | __  ) _ ))                  \n"
    +"                      ,|  |  |                             \n"
    +"                     `-._____,-'                           \n"
    +"                     `--.___,--'                           \n"
    +"                       |     |                             \n"
    +"                       |    ||                             \n"
    +"                       | ||  |                             \n"
    +"             ,    _,   |   | |                             \n"
    +"    (  ((  ((((  /,| __|     |  ))))  )))  )  ))           \n"
    +"  (()))       __/ ||(    ,,     ((//\\     )     ))))      \n"
    +" (( ///_.___ _/    ||,,_____,_,,, (|\\ \\___.....__..  ))  \n"
    +"       ____/      |/______________| \\/_/\\__              \n"
    +"      /                                \\/_/|              \n"
    +"     /  |___|___|__                        ||     ___      \n"
    +"     \\    |___|___|_                       |/\\   /__/|   \n"
    +"     /      |   |                           \\/   |__|/    \n"
    +"                                                           \n";
	
	private final static String LAMPREY_ASCII =           
	 "     _____                  __                              \n"
	+"    |_   _|___ ___ _____   |  |   ___ _____ ___ ___ ___ _ _ \n" 
	+"      | | | -_| .'|     |  |  |__| .'|     | . |  _| -_| | |\n"
	+"      |_| |___|__,|_|_|_|  |_____|__,|_|_|_|  _|_| |___|_  |\n"
	+"                                           |_|         |___|";
	
	// UI variables
	private JTextArea systemText = new JTextArea(10,20);
    private JTextArea outputText = new JTextArea(10,20);
    private JTextField inputBox = new JTextField(30);
    private final static Font default_font = new Font("Monospaced",Font.PLAIN, 12);
    private final static String prompt = "~> ";
    
    private PlantController controller;
    private UIData uidata;
    private State state = State.Uninitialised;
    private AreYouSureCaller caller = AreYouSureCaller.NoAction;
    
    public TextUI(PlantController presenter)
    {
    	super("REACTOR");
    	this.controller = presenter;
    	this.uidata = presenter.getUIData();
        initWindow();
        startUp();
    }

    private void initWindow() {
    	setSize(1000,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel leftPanel = new JPanel();           
        GridBagLayout leftPanelGridBagLayout = new GridBagLayout();
        leftPanel.setLayout(leftPanelGridBagLayout);
        initInputArea(leftPanel);
        initOutputArea(leftPanel);
        
        JPanel rightPanel = new JPanel();
        initSystemArea(rightPanel);
        GridLayout rightPanelGridLayout = new GridLayout(1,1);
        rightPanel.setLayout(rightPanelGridLayout);
        
        GridLayout grid = new GridLayout(1,2, 0, 0);
        setLayout(grid);
        add(leftPanel);
        add(rightPanel);
        setVisible(true);
        // Auto focus the input box!
        inputBox.grabFocus();
    }
    
    private void initInputArea(JPanel parentPanel) {
    	GridBagLayout layout = (GridBagLayout) parentPanel.getLayout();        
        inputBox.setBackground(Color.BLACK);
        inputBox.setForeground(Color.WHITE);
        inputBox.setFont(default_font);
        inputBox.setCaretColor(inputBox.getForeground());
        
        GridBagConstraints inputBoxConstraints = new GridBagConstraints();
        inputBoxConstraints.gridx = 0;
        inputBoxConstraints.gridy = 2;
        inputBoxConstraints.gridwidth = 1;
        inputBoxConstraints.gridheight = 1;
        inputBoxConstraints.weightx = 10;
        inputBoxConstraints.weighty = 1;
        inputBoxConstraints.fill = GridBagConstraints.BOTH;
        inputBoxConstraints.anchor = GridBagConstraints.SOUTH;
        layout.setConstraints(inputBox, inputBoxConstraints);
        
        // Anti Prompt deletion navigation filter!
        NavigationFilter filter = new NavigationFilter() {
            public void setDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias) {
            	if (dot <= prompt.length()) {
            		fb.setDot(prompt.length(), bias);
            	} else {
            		fb.setDot(dot, bias);
            	}
            }

            public void moveDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias) {
            	if (dot <= prompt.length()) {
            		fb.setDot(prompt.length(), bias);
            	} else {
            		fb.setDot(dot, bias);
            	}
            }
        };
        inputBox.setNavigationFilter(filter);
        
        inputBox.addKeyListener(this);
        parentPanel.add(inputBox);
    }
    
    private void initOutputArea(JPanel parentPanel) {
    	GridBagLayout layout = (GridBagLayout) parentPanel.getLayout();
    	
    	//outputText.setBorder(BorderFactory.createMatteBorder(0,0,2,1,Color.GRAY));
        outputText.setBackground(Color.BLACK);
        outputText.setForeground(Color.WHITE); 
        outputText.setFont(default_font);
        outputText.setLineWrap(true);  
        outputText.setWrapStyleWord(true);
        outputText.setEditable(false);
        
        JScrollPane Scroll = new JScrollPane(outputText);
        Scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Auto scroll down!
        DefaultCaret caret = (DefaultCaret)outputText.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
         
        GridBagConstraints OutputTextConstraints = new GridBagConstraints();
        OutputTextConstraints.gridx = 0;
        OutputTextConstraints.gridy = 0;
        OutputTextConstraints.gridwidth = 1;
        OutputTextConstraints.gridheight = 1;
        OutputTextConstraints.weightx = 10;
        OutputTextConstraints.weighty = 100;
        OutputTextConstraints.fill = GridBagConstraints.BOTH;
        OutputTextConstraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(Scroll, OutputTextConstraints);
        parentPanel.add(Scroll);
    }
    
    private void initSystemArea(JPanel parentPanel) {

        systemText.setBackground(Color.BLACK);
        systemText.setForeground(Color.WHITE);
        systemText.setFont(default_font);
        systemText.setEditable(false);
        parentPanel.add(systemText);
    }
    
    private void startUp()
    {
        outputText.setText(START_TEXT + REACTOR_ASCII);
        inputBox.setText(prompt);
        inputBox.setCaretPosition(prompt.length());
    }
    
    public void keyReleased(KeyEvent k)
    {
    	int keyCode = k.getKeyCode();
    	switch (keyCode) {
    		case KeyEvent.VK_BACK_SPACE:
    			persistPrompt();
    			break;
    	}
    }
    
    public void keyTyped(KeyEvent k)
    { 
    }
    
    public void keyPressed(KeyEvent k) 
    {    	
        int keyCode = k.getKeyCode();
        switch (keyCode) {
			case KeyEvent.VK_ENTER:
				actUponInput();
				break;
			case KeyEvent.VK_BACK_SPACE:
    			persistPrompt();
    			break;
        }
    }
    
    private void persistPrompt()
	{
    	int promptLength = prompt.length();
    	if (inputBox.getCaret().getDot() <= promptLength) inputBox.getCaret().setDot(promptLength);
		if (!inputBox.getText().startsWith(prompt)) {
			inputBox.setText(prompt + inputBox.getText().substring(promptLength - 1));
		}
		
	}

	private void actUponInput() {
    	String command = inputBox.getText().substring(prompt.length());
    	if(!(state == State.GameOver) || command.equals("newgame")) {
    		print(prompt + command);
    	}
    	parse(command);
		inputBox.setText(prompt);
		
		if(state == State.Normal || state == State.GameOver)
			updateSystemText();
    }
    
    private void print(String output) {
    	outputText.append(output + "\n");
    }
    
    private void updateSystemText() {
    	String reactorInfo = new String();
    	uidata = controller.getUIData();
    	uidata.updateUIData();
    	if (!uidata.isGameOver()) {
        	reactorInfo += "Operator Name: "  + uidata.getOperatorName() + "\t| SCORE: " + uidata.getScore() + "\n\n";
        	reactorInfo += "PLANT READINGS: \n\n";
        	
        	reactorInfo += "POWER OUTPUT: " + uidata.getPowerOutput() + "\n\n";
        	
        	reactorInfo += "TURBINE : " + ((uidata.isTurbineFunctional()) ? "FUNCTIONAL" : "BROKEN") + "\n";
        	reactorInfo += "Current RPM : " + uidata.getTurbineRpm() + "\n\n";
        	
        	reactorInfo += "REACTOR HEALTH: " + uidata.getReactorHealth() + "\n";  
        	reactorInfo += "Temperature: "    + uidata.getReactorTemperature() + "  \t| Max: "                + uidata.getReactorMaxTemperature()     + "\n";
        	reactorInfo += "Pressure: "       + uidata.getReactorPressure()    + " \t\t| Max: "               + uidata.getReactorMaxPressure()        + "\n";
        	reactorInfo += "Water Volume: "   + uidata.getReactorWaterVolume() + " \t| Minimum Safe Volume: " + uidata.getReactorMinSafeWaterVolume() + "\n\n";
        	
        	reactorInfo += "CONDENSER HEALTH: " + uidata.getCondenserHealth()      + "\n";
        	reactorInfo += "Temperature: "      + uidata.getCondenserTemperature() + "  \t| Max: "                + uidata.getCondenserMaxTemperature()     + "\n";
        	reactorInfo += "Pressure: "         + uidata.getCondenserPressure()    + " \t\t| Max: "               + uidata.getCondenserMaxPressure()        + "\n";
        	reactorInfo += "Water Volume: "     + uidata.getCondenserWaterVolume() + "\n\n";
        	
        	List<Valve> valves = uidata.getValves();
        	for (Valve v : valves) {
        		reactorInfo += "VALVE ID: " + v.getID() + " | ";
        		reactorInfo += "POSITION: " + (v.isOpen() ? "OPEN\n" : "CLOSED\n");
        	}
        	reactorInfo += "\n";
        	
        	List<Pump> pump = uidata.getPumps();
        	for (Pump p : pump) {
        		reactorInfo += "PUMP ID: " + p.getID() + "  | ";
        		reactorInfo += "STATUS: " + ((p.isOperational()) ? "FUNCTIONAL | " : "BROKEN | ");
        		reactorInfo += "POWER STATE: " + (p.isOn() ? "ON | " : "OFF | ");
        		reactorInfo += "RPM: " + p.getRpm() + "\n";
        	}
        	reactorInfo += "\n";
        	
        	reactorInfo += "CONTROL RODS PERCENT INTO CORE: " + uidata.getControlRodsPercentage() + "%\n";
    	}
    	else {
    		state = State.GameOver;
    		reactorInfo = MUSHROOM_ASCII + "Your final score is: " + uidata.getScore();
    		
    	}
    	
    	systemText.setText(reactorInfo);
    }
    
    //--------------- Parsing ----------------
    
	private void parse(String input) {
		if (state == State.Normal)
			parseNormal(input.toLowerCase());
		else if (state == State.Uninitialised)
			parseUninitialised(input.toLowerCase());
		else if (state == State.NewGame)
			parseNewGame(input);
		else if (state == State.AreYouSure)
			parseAreYouSure(input.toLowerCase());
		else if (state == State.GameOver)
			parseUninitialised(input);
	}
	
	private void parseNormal(String input) {
		Scanner scanner = new Scanner(input);
		if (!scanner.hasNext()) {
			print("");
		}
		else {
			String command = scanner.next();
			if (command.equals("newgame") && !scanner.hasNext()) {
				caller = AreYouSureCaller.Newgame;
				doAreYouSure();
			}
	    	else if (command.equals("loadgame") && !scanner.hasNext()) {
				caller = AreYouSureCaller.Loadgame;
				doAreYouSure();
	    	}
	    	else if (command.equals("savegame") && !scanner.hasNext()) {
				caller = AreYouSureCaller.Savegame;
				doAreYouSure();
	    	}
	    	else if (command.equals("highscores") && !scanner.hasNext()) {
	    		printHighScores();
	    	}
	    	else if (command.equals("credits") && !scanner.hasNext()) {
	    		printCredits();
	    	}
	    	else if (command.equals("step")) {
	    		if (!scanner.hasNext()) {
	    			doStep(1);
	    		} else if (scanner.hasNextInt()) {
	    			int n = scanner.nextInt();
	    			doStep(n);
	    		} else {
	    			print("Invalid usage of step command - step or step n");
	    		}
	    	}
	    	else if (command.equals("set") && scanner.hasNext()) {
	    		String component = scanner.next();
	    		if (component.equals("valve") && scanner.hasNextInt()) {
	    			int valveID = scanner.nextInt();
	    			if (scanner.hasNext()) {
	    				String valveCommand = scanner.next();
		    			if (valveCommand.equals("open") || valveCommand.equals("close")) {
		    				boolean success;
		    				if (valveCommand.equals("open")) {
		    					success = controller.setValve(valveID, true);
		    				}
		    				else { //close
		    					success = controller.setValve(valveID, false);
		    				}
		    				if (success)
		    					print("Valve was successfully set");
		    				else
		    					print("Valve was not successfully set. Try another (smaller) valve ID.");
		    			}
	    				else {
	    					printNotValidValve();
	    				}
	    			}
		    		else {
		    			printNotValidValve();
		    		}
	    		}
	    		else if (component.equals("valve") && !scanner.hasNextInt()) {
	    			printNotValidValve();
	    		}
	    		else if (component.equals("pump") && scanner.hasNextInt()) {
	    			int pumpID = scanner.nextInt();
	    			if (scanner.hasNext()) {
	    				String pumpCommand = scanner.next();
		    			if (pumpCommand.equals("on") || pumpCommand.equals("off")) {
		    				boolean success;
		    				if (pumpCommand.equals("on")) {
		    					success = controller.setPumpOnOff(pumpID, true);
		    				}
		    				else { //close
		    					success = controller.setPumpOnOff(pumpID, false);
		    				}
		    				if (success) {
		    					print("Pump was successfully set");
		    				}
		    				else {
		    					print("Pump was not successfully set. Try another pump ID.");
		    				}
		    			}
	    				else if (pumpCommand.equals("rpm") && scanner.hasNextInt()) {
	    					int newRpm = scanner.nextInt();
	    					boolean success;
	    					try {
	    						success = controller.setPumpRpm(pumpID, newRpm);
	    						if (success) {
		    						print("Pump rpm was successfully set.");
		    					} else {
		    						print("Pump rpm was not successfully set. Try another pump ID.");
		    					}
	    					} catch (IllegalArgumentException iae) {
	    						print("Pump rpm was not successfully set.");
	    						print(iae.getMessage());
	    					}
	    				}
	    				else
	    				{
	    					printNotValidPump();
	    				}
	    			}
		    		else {
		    			printNotValidPump();
		    		}
	    		}
	    		else if (component.equals("pump") && !scanner.hasNextInt()) {
	    			printNotValidPump();
	    		}
	    		else if ((component.equals("controlrods") || component.equals("cr")) && scanner.hasNextInt()) {
	    			int percentageLowered = scanner.nextInt();
	    			if (percentageLowered >= 0 && percentageLowered <= 100 && !scanner.hasNext()) {
	    				controller.setControlRods(percentageLowered);
	    				print("Control rods were successfully set.");
	    			}
	    			else {
	    				print("Control rods have to be set to a value between 0 and 100 (i.e. \"set control rods 50\")");
	    			}
	    		}
	    		else if (component.equals("controlrods") && !scanner.hasNextInt()) {
	    			print("Please select a value for the control rods: set controlrods n (n is a number between 0 and 100)");
	    			print("You can also use \"set cr\" instead of \"set controlrods\"");
	    		}
	    		else {
	    			print("Incorrect usage of set command - set valve, set controlrods (set cr), set pump");
	    		}
	    	}
	    	else if (command.equals("set") && !scanner.hasNext()) {
	    		print("Incorrect usage of set command - set valve, set controlrods (set cr), set pump");
	    	}
	    	else if (command.equals("repair") && scanner.hasNext()) {
	    		String component = scanner.next();
	    		if (component.equals("turbine") && !scanner.hasNext()) {
	    			doRepairTurbine();
	    		}
	    		else if (component.equals("pump") && scanner.hasNextInt()) {
	    			int pumpID = scanner.nextInt();
	    			doRepairPump(pumpID);
	    		}
	    		else {
	    			print("Not a valid command. You can use \"set\" a pump or valve, \"repair\" a pump or the turbine " +
	    					"and \"step\" once or up to 10 times. You can also start a \"newgame\", save or load a game, " +
	    					"check the \"highscores\" or the \"credits\".");
	    		}
	    	}
	    	else if ( (command.equals("exit") || command.equals("quit")) && !scanner.hasNext()) {
	    		caller = AreYouSureCaller.Exit;
	    		doAreYouSure();
	    	}
			else {
    			print("Not a valid command. You can use \"set\" a pump or valve, \"repair\" a pump or the turbine " +
    					"and \"step\" once or up to 10 times. You can also start a \"newgame\", save or load a game, " +
    					"check the \"highscores\" or the \"credits\".");
			}
		}
		scanner.close();
	}

	private void parseUninitialised(String input) {
    	Scanner scanner = new Scanner(input);
		if (!scanner.hasNext()) {
			//Nothing
		}
		else {
	    	String command = scanner.next();
	    	if (command.equals("newgame") && !scanner.hasNext()) {
	    		doNewGame();
	    	}
	    	else if (command.equals("loadgame") && !scanner.hasNext()) {
	    		doLoadGame();
	    	}
	    	else if (command.equals("highscores") && !scanner.hasNext()) {
	    		printHighScores();
	    		//print("Ask for high scores after the game is initialised.");
	    	}
	    	else if (command.equals("credits") && !scanner.hasNext()) {
	    		printCredits();
	    	}
	    	else if ( (command.equals("exit") || command.equals("quit")) && !scanner.hasNext()) {
	    		doExit();
	    	}
	    	else {
	    		print("The game is uninitialised. Please use one of the following commands: newgame, loadgame, highscores, credits, exit.");
	    	}
		}
		scanner.close();
    }
	
	private void parseNewGame(String input) {
		if (input.length() > MAX_NAME_LENGTH) {
			print("Your name is too long - please use a name shorter than " + MAX_NAME_LENGTH + " characters.");
		}
		else {
			controller.newGame(input);
			state = State.Normal;
			print("New game started.");
		}
	}
	
	private void parseAreYouSure(String input) {
		Scanner scanner = new Scanner(input);
		if (scanner.hasNext()) {
			String next = scanner.next();
			if( (next.equals("yes") || next.equals("y")) && !scanner.hasNext()) {
				state = State.Normal;
				if      (caller == AreYouSureCaller.Newgame)	doNewGame();
				else if (caller == AreYouSureCaller.Savegame)	doSaveGame();
				else if (caller == AreYouSureCaller.Loadgame)	doLoadGame();
				else if (caller == AreYouSureCaller.Exit)		doExit();
			}
			else {
				state = State.Normal;
				print("Action not confirmed.");
			}
		}
		caller = AreYouSureCaller.NoAction;
		scanner.close();
	}
	
	//-------------- Methods used inside parsing -------------------
	
	private void doStep(int numSteps)
	{
		if (numSteps >= 0 && numSteps <= MAX_TIME_STEPS_PER_COMMAND) {
			controller.step(numSteps);
			List<PlantComponent> brokenOnStep = uidata.getBrokenOnStep();
			for (PlantComponent broken : brokenOnStep) {
				if (broken instanceof Turbine) {
					print("The turbine has broken " + randomReason());  
				}
				else if (broken instanceof Pump) {
					Pump pBroken = (Pump) broken;
					print("Pump " + pBroken.getID() + " has broken " + randomReason());
				}
			}
			uidata.resetBrokenOnStep();
			print("Game advanced " + numSteps + " steps.");
		}
		else {
			print("Too many steps. Try a number between 0 and " + MAX_TIME_STEPS_PER_COMMAND + ".");
		}
	}
	
	private void doNewGame() {
		state = State.NewGame;
		print("Please enter your name.");
	}
	
	private void doLoadGame() {
		if (controller.loadGame()) {
			state = State.Normal;
			print("Game loaded from file.");
		}
		else {
			print("Loading a game was not successful: check if a savegame file exists or start a new game.");
		}
	}
	
	private void doSaveGame() {
		if (controller.saveGame() && state == State.Normal) {
			print("Game saved to file.");
		}
		else {
			print("Saving a game was not successful.");
		}
	}
	
	private void doRepairTurbine() {
		if (controller.repairTurbine()) {
			print("Repair on the turbine has began.");
		}
		else {
			print("Turbine is either functional or already being repaired.");
		}
	}
	
	private void doRepairPump(int pumpID) {
		if (controller.repairPump(pumpID)) {
			print("Repair on pump " + pumpID + " has began.");
		}
		else {
			print("Pump " + pumpID + " is either functional or already being repaired.");
		}
	}
	
	private void doExit() {
		System.exit(0);
	}
	
	private void doAreYouSure() {
		state = State.AreYouSure;
		print("Are you sure (Y/n)");
	}
	
	private void printHighScores() {
		List<HighScore> highScores = controller.getHighScores();
		if (!highScores.isEmpty()) {
			int i = 1;
    		for (HighScore highScore : highScores) {
    			print(i + ": " + highScore.getName() + ": " + highScore.getHighScore());
    			i++;
    		}
		}
		else {
			print("No high scores yet.");
		}
	}
	
	private void printCredits() {
		print("~-----------~        Lovingly crafted by:        ~-----------~");
		print(LAMPREY_ASCII);
		print("~-----~      Ali, Brad, James, John, Simeon & Will     ~-----~");
	}
	
	private void printNotValidValve() {
		print("Not a valid command.");
		print("Format for setting a valve: set valve <id> [open | close]");
		print("\tWhere \"id\" is the ID of the valve and \"newState\" is either \"open\" or \"close\"");
		print("\ti.e. \"set valve 2 open\" or \"set valve 4 close\"");
	}
	
	private void printNotValidPump() {
		print("Not a valid command.");
		print("Format for setting a pump on or off: set pump <id> [on | off]");
		print("\tWhere <id> is the ID of the pump.");
		print("\ti.e. \"set pump 2 on\" or \"set pump 1 off\"");
		print("Format for setting a pump's rpm: set pump <id> rpm <newRpm>");
		print("\ti.e. \"set pump 2 rpm 400\" or \"set pump 1 rpm 300\"");
	}
	
	private String randomReason() {
		Random random = new Random();
		List<String> reasons = new ArrayList<String>();
		reasons.add("because of malfunction.");
		reasons.add("because of bad weather conditions outside the plant.");
		reasons.add("because of big stress on the component");
		reasons.add("because of a big earthquake.");
		int selection = random.nextInt(reasons.size());
		return reasons.get(selection);
	}
	
	private enum State {
		Uninitialised, NewGame, Normal, AreYouSure, GameOver;
	}
	
	private enum AreYouSureCaller {
		Newgame, Savegame, Loadgame, Exit, NoAction;
	}
}
