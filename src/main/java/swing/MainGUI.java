package swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import components.OperatingSoftware;
import components.Pump;
import components.Reactor;
import components.Turbine;


import model.Observer;
import model.Repair;

import simulator.Multiplayer2Controller;
import simulator.MultiplayerController;
import simulator.PlantController;


/**
 * This is the main GUI class. Only a reference to the PlantController class is needed when this class is instantiated.
 * It creates all the required components, instantiates them and connects them to the plant.
 * This class is kept as separated from the plant as possible, the only classes it interacts with are the OperatingSoftware, the PlantController and the UIData.
 * At each change in any of the components the gui is updated via dedicated method.
 * When the scores need to be shown this class creates ScoresGUI object, which has its own gui.
 * When the game is over the EndGame class is instantiated which has its own gui.
 * @author 
 */
public class MainGUI implements Observer
{
	// Quench button tooltip
	private static final String quenchToolTip = "Quench!:\n Quench the reactor with a burst of cool water. Use it wisely,\n you only have enough spare water to use it once.";
    
	// Player 2 key bindings.
	private static final int FAIL_PUMP_1 = KeyEvent.VK_1; // 1
	private static final int FAIL_PUMP_2 = KeyEvent.VK_2; // 2
	private static final int FAIL_PUMP_3 = KeyEvent.VK_3; // 3
	private static final int FAIL_TURBINE = KeyEvent.VK_4; // 4
	private static final int FAIL_OS = KeyEvent.VK_5; // 5
	private static final int TOGGLE_RANDOM_FAILURES = KeyEvent.VK_6; //6
	
	// the string that is shown initially in the player name field
    private String initialNameValue = "";
    
    private Multiplayer2Controller controller;
    
    //the main frame
    private JFrame frame;
    
    //the field where the player should write theirs
    private JLabel nameTextField;
    
    //the buttons which effect is not dependent on the operating software
    private JButton btnNewSingleplayerGame;
    private JButton btnNewMultiplayerGame;
    private JButton btnLoad;
    private JButton btnSave;
    private JButton btnShowManual;
    private JButton btnShowScores;
    private JButton btnQuenchReactor;
    
    //make a number of steps
    private JButton btnStep;
    private JButton btnRepairOperatingSoftware;
    
    //the affect of those buttons is dependent on the state of the operating software
    private JButton btnValve1;
    private JButton btnValve2;
    private JButton btnRepairPump1;
    private JButton btnRepairPump2;
    private JButton btnRepairPump3;
    private JButton btnRepairTurbine;
    
    //labels showing the state of the different components that can fail
    private JLabel lblPump1State;
    private JLabel lblPump2State;
    private JLabel lblPump3State;
    private JLabel lblTurbineState;
    private JLabel lblOperatingSoftwareState;
    
    private JLabel lblOtherPlayerScore;
    private JLabel lblScore;
    
    //this label shows how many timesteps will be issued
    private JLabel lblNumberOfSteps;
    
    // How many steps are left in a players go until the swap (Multiplayer only)
    private JLabel lblStepsUntilSwap;
    
    private JLabel lblRandomFailures;
    
    //progress bars showing the temperature, pressure, water level and
    //health of the reactor and the condenser
    private JProgressBar progressBarReactorTemperature;
    private JProgressBar progressBarReactorPressure;
    private JProgressBar progressBarReactorHealth;
    private JProgressBar progressBarCondenserTemperature;
    private JProgressBar progressBarCondenserPressure;    
    private JProgressBar progressBarCondenserHealth;
    private JProgressBar progressBarReactorWaterLevel;
    private JProgressBar progressBarCondenserWaterLevel;
    
    
    //sliders controlling the rpm of the pumps, the level of the
    //control rods and the number of timesteps
    private JSlider sliderPump1RPM;
    private JSlider sliderPump2RPM;
    private JSlider sliderPump3RPM;
    private JSlider sliderRodsLevel;
    private JSlider sliderNumberOfSteps;
    
    //image icons containing the images that are used in the gui
    private ImageIcon repairButtonEnabledImageIcon;
    private ImageIcon repairButtonDisabledImageIcon;
    private ImageIcon stateSafeImageIcon;
    private ImageIcon stateBeingRepairedImageIcon;
    private ImageIcon stateBrokenImageIcon;
    private ImageIcon valveOpenedImageIcon;
    private ImageIcon valveClosedImageIcon;
    private ImageIcon newGameImageIcon;
    private ImageIcon loadGameImageIcon;
    private ImageIcon saveGameImageIcon;
    private ImageIcon viewManualImageIcon;
    private ImageIcon viewScoresImageIcon;
    private ImageIcon nextStepImageIcon;

    //the repair buttons are not disabled directly but a different image is associated
    //with them when they cannot be used - this variable prevents them from being used
    //when they are 'disabled'
    private boolean controlButtonsEnabled = true;

    //a temporary value which has different usages 
    private int tempValue;

	private boolean displayedSwapDialog;
	private boolean displayedEndGameDialog;

	// Steps until failable labels.
	private JLabel lblPump1Failable;
	private JLabel lblPump2Failable;
	private JLabel lblPump3Failable;
	private JLabel lblOSFailable;
	private JLabel lblTurbineFailable;

	private ImageIcon multiGameImageIcon;
	
    /**
     * The constructor sets the controller object, initialises the gui
     * and makes it visible.
     * @param controller
     */
    public MainGUI(Multiplayer2Controller controller)
    {
        this.controller = controller;
        initialize();
        frame.setVisible(true);
        initGame();
    }

    /**
     * Asks the user whether they want to start a new singleplayer game,
     * multiplayer game, or exit. And does the appropriate actions!
     */
    private void initGame() {
    	Object[] options = { "Single-player", "2-player", "Exit" };
    	String titleText = "What would you like to do?";		
    	String messageText = "REACTOR: Extended Edition can be played in one of two modes...\n"+
    						 "Which would you like to play?";		
    	int opt = JOptionPane.showOptionDialog(null, messageText, titleText,
    	JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
    	null, options, options[0]);
    	switch (opt) {
    		case 0: // 1-player
    			startNewSingleplayerGame();
    			break;
    		case 1: // 2-play0r
    			startNewMultiplayerGame();
    			break;
    		case 2: // Exit
    		default:
    			System.exit(0);
    			break;
    	}
    }

    /**
     * Initialises the contents of the frame.
     */
    private void initialize()
    {	
    	//instantiates the main frame
        frame = new JFrame();
        frame.setBounds(100, 100, 1049, 740);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //which is layered - two layers: background picture - layer 0
        //all interactive components - layer 1
        JLayeredPane layeredPane = new JLayeredPane();
        frame.getContentPane().add(layeredPane, BorderLayout.CENTER);
        
        //loads and sets the background image
        java.net.URL imageURL = this.getClass().getClassLoader().getResource("plantBackground.png");
        ImageIcon backgroundImageIcon = new ImageIcon(imageURL);
        JLabel backgroundImageLabel = new JLabel(backgroundImageIcon);
        backgroundImageLabel.setBackground(new Color(0, 153, 0));
        backgroundImageLabel.setBounds(0, 0, 1040, 709);
        layeredPane.add(backgroundImageLabel);

        //loads all the images that are required for the image labels
        //the path is relative to the project
        imageURL = this.getClass().getClassLoader().getResource("btnRepairEnabled.png");
        repairButtonEnabledImageIcon = new ImageIcon(imageURL);
        imageURL = this.getClass().getClassLoader().getResource("btnRepairDisabled.png");
        repairButtonDisabledImageIcon = new ImageIcon(imageURL);
        imageURL = this.getClass().getClassLoader().getResource("stateSafe.png");
        stateSafeImageIcon = new ImageIcon(imageURL);
        imageURL = this.getClass().getClassLoader().getResource("stateBeingRepaired.png");
        stateBeingRepairedImageIcon = new ImageIcon(imageURL);
        imageURL = this.getClass().getClassLoader().getResource("stateBroken.png");
        stateBrokenImageIcon = new ImageIcon(imageURL);  
        imageURL = this.getClass().getClassLoader().getResource("valveOpened.png");
        valveOpenedImageIcon = new ImageIcon(imageURL);
        imageURL = this.getClass().getClassLoader().getResource("valveClosed.png");
        valveClosedImageIcon = new ImageIcon(imageURL);
        
        imageURL = this.getClass().getClassLoader().getResource("newButtonLabel.png");
        newGameImageIcon = new ImageIcon(imageURL);
        imageURL = this.getClass().getClassLoader().getResource("newMultiButtonLabel.png");
        multiGameImageIcon = new ImageIcon(imageURL);
        imageURL = this.getClass().getClassLoader().getResource("loadButtonLabel.png");
        loadGameImageIcon = new ImageIcon(imageURL);
        imageURL = this.getClass().getClassLoader().getResource("saveButtonLabel.png");
        saveGameImageIcon = new ImageIcon(imageURL);
        imageURL = this.getClass().getClassLoader().getResource("manualButtonLabel.png");
        viewManualImageIcon = new ImageIcon(imageURL);
        imageURL = this.getClass().getClassLoader().getResource("scoresButtonLabel.png");
        viewScoresImageIcon = new ImageIcon(imageURL);
        imageURL = this.getClass().getClassLoader().getResource("nextButtonLabel.png");
        nextStepImageIcon = new ImageIcon(imageURL);
        
        //initialises the label that shows the score
        lblScore = new JLabel("0");
        lblScore.setFont(new Font("Tahoma", Font.PLAIN, 30));
        lblScore.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        lblScore.setBounds(860, 81, 160, 23);
        layeredPane.setLayer(lblScore, 1);
        layeredPane.add(lblScore);
        
        //initialises the label that shows the score
        lblOtherPlayerScore = new JLabel("");
        lblOtherPlayerScore.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblOtherPlayerScore.setHorizontalAlignment(SwingConstants.LEFT);
        lblOtherPlayerScore.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        lblOtherPlayerScore.setForeground(new Color(20,220,0));
        lblOtherPlayerScore.setBounds(320, 10, 160, 23);
        layeredPane.setLayer(lblOtherPlayerScore, 2);
        layeredPane.add(lblOtherPlayerScore);
        
        
        //initialises the label that shows the score
        lblRandomFailures = new JLabel("");
        lblRandomFailures.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblRandomFailures.setHorizontalAlignment(SwingConstants.LEFT);
        lblRandomFailures.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        lblRandomFailures.setForeground(new Color(0,255,0));
        lblRandomFailures.setBounds(580, 530, 160, 23);
        layeredPane.setLayer(lblRandomFailures, 2);
        layeredPane.add(lblRandomFailures);
        
        nameTextField = new JLabel(initialNameValue);
        nameTextField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        nameTextField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        nameTextField.setOpaque(false);
        nameTextField.setBorder(null);
        nameTextField.setBounds(10, 11, 102, 20);
        layeredPane.setLayer(nameTextField, 1);
        layeredPane.add(nameTextField);
        
        //instantiation of the label showing the number of time steps
        lblNumberOfSteps = new JLabel("1");
        lblNumberOfSteps.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNumberOfSteps.setFont(new Font("Tahoma", Font.PLAIN, 30));
        lblNumberOfSteps.setBounds(369, 499, 40, 40);
        lblNumberOfSteps.setBackground(new Color(18, 140, 0));
        lblNumberOfSteps.setOpaque(false);
        layeredPane.setLayer(lblNumberOfSteps, 1);
        layeredPane.add(lblNumberOfSteps);
        
      //instantiation of the label showing the number of time steps
        lblStepsUntilSwap = new JLabel("");
        lblStepsUntilSwap.setHorizontalAlignment(SwingConstants.LEFT);
        lblStepsUntilSwap.setFont(new Font("Tahoma", Font.PLAIN, 30));
        lblStepsUntilSwap.setBounds(495, 499, 80, 40);
        lblStepsUntilSwap.setForeground(new Color(20,220,0));
        lblStepsUntilSwap.setOpaque(false);
        layeredPane.setLayer(lblStepsUntilSwap, 1);
        layeredPane.add(lblStepsUntilSwap);
        
        
        //all the labels that show component states are
        //initialised with green light showing
        lblPump1State = new JLabel(stateSafeImageIcon);
        lblPump1State.setBounds(273, 592, 78, 23);
        layeredPane.setLayer(lblPump1State, 1);
        layeredPane.add(lblPump1State);
        
        lblPump2State = new JLabel(stateSafeImageIcon);
        lblPump2State.setBounds(496, 592, 78, 23);
        layeredPane.setLayer(lblPump2State, 1);
        layeredPane.add(lblPump2State);
        
        lblPump3State = new JLabel(stateSafeImageIcon);
        lblPump3State.setBounds(716, 592, 78, 23);
        layeredPane.setLayer(lblPump3State, 1);
        layeredPane.add(lblPump3State);
        
        lblTurbineState = new JLabel(stateSafeImageIcon);
        lblTurbineState.setBounds(826, 592, 78, 23);
        layeredPane.setLayer(lblTurbineState, 1);
        layeredPane.add(lblTurbineState);
        
        lblOperatingSoftwareState = new JLabel(stateSafeImageIcon);
        lblOperatingSoftwareState.setBounds(927, 592, 78, 23);
        layeredPane.setLayer(lblOperatingSoftwareState, 1);
        layeredPane.add(lblOperatingSoftwareState);
        
        
        //initialises the label that shows how many steps until pump 1 can be forcably failed.
        lblPump1Failable = new JLabel("");
        lblPump1Failable.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblPump1Failable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        lblPump1Failable.setForeground(new Color(50,120,0));
        lblPump1Failable.setBounds(112, 555, 160, 23);
        layeredPane.setLayer(lblPump1Failable, 2);
        layeredPane.add(lblPump1Failable);
      
        //initialises the label that shows how many steps until pump 1 can be forcably failed.
        lblPump2Failable = new JLabel("");
        lblPump2Failable.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblPump2Failable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        lblPump2Failable.setForeground(new Color(50,120,0));
        lblPump2Failable.setBounds(333, 555, 160, 23);
        layeredPane.setLayer(lblPump2Failable, 2);
        layeredPane.add(lblPump2Failable);
        
      //initialises the label that shows how many steps until pump 1 can be forcably failed.
        lblPump3Failable = new JLabel("");
        lblPump3Failable.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblPump3Failable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        lblPump3Failable.setForeground(new Color(50,120,0));
        lblPump3Failable.setBounds(554, 555, 160, 23);
        layeredPane.setLayer(lblPump3Failable, 2);
        layeredPane.add(lblPump3Failable);
        
      //initialises the label that shows how many steps until pump 1 can be forcably failed.
        lblTurbineFailable = new JLabel("");
        lblTurbineFailable.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblTurbineFailable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        lblTurbineFailable.setForeground(new Color(50,120,0));
        lblTurbineFailable.setBounds(666, 555, 160, 23);
        layeredPane.setLayer(lblTurbineFailable, 2);
        layeredPane.add(lblTurbineFailable);
        
      //initialises the label that shows how many steps until pump 1 can be forcably failed.
        lblOSFailable = new JLabel("");
        lblOSFailable.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblOSFailable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        lblOSFailable.setForeground(new Color(50,120,0));
        lblOSFailable.setBounds(3775, 555, 160, 23);
        layeredPane.setLayer(lblOSFailable, 2);
        layeredPane.add(lblOSFailable);
        
        //creation and instantiation of the progress bars
        //change state listeners added at the end of this method
        progressBarReactorTemperature = new JProgressBar();
        progressBarReactorTemperature.setBounds(781, 168, 234, 14);
        layeredPane.setLayer(progressBarReactorTemperature, 1);
        layeredPane.add(progressBarReactorTemperature);
        
        progressBarReactorPressure = new JProgressBar();
        progressBarReactorPressure.setBounds(781, 203, 234, 14);
        layeredPane.setLayer(progressBarReactorPressure, 1);
        layeredPane.add(progressBarReactorPressure);
        
        progressBarReactorHealth = new JProgressBar();
        progressBarReactorHealth.setBounds(781, 273, 234, 14);
        layeredPane.setLayer(progressBarReactorHealth, 1);
        layeredPane.add(progressBarReactorHealth);
        
        progressBarReactorWaterLevel = new JProgressBar();
        progressBarReactorWaterLevel.setBounds(781, 237, 234, 14);
        layeredPane.setLayer(progressBarReactorWaterLevel, 1);
        layeredPane.add(progressBarReactorWaterLevel);
        
        progressBarCondenserTemperature = new JProgressBar();
        progressBarCondenserTemperature.setBounds(781, 359, 234, 14);
        layeredPane.setLayer(progressBarCondenserTemperature, 1);
        layeredPane.add(progressBarCondenserTemperature);
        
        progressBarCondenserPressure = new JProgressBar();
        progressBarCondenserPressure.setBounds(781, 394, 234, 14);
        layeredPane.setLayer(progressBarCondenserPressure, 1);
        layeredPane.add(progressBarCondenserPressure);
        
        progressBarCondenserHealth = new JProgressBar();
        progressBarCondenserHealth.setBounds(781, 468, 234, 14);
        layeredPane.setLayer(progressBarCondenserHealth, 1);
        layeredPane.add(progressBarCondenserHealth);
        
        progressBarCondenserWaterLevel = new JProgressBar();
        progressBarCondenserWaterLevel.setBounds(781, 430, 234, 14);
        progressBarCondenserWaterLevel.setForeground(new Color(0, 255, 0));
        layeredPane.setLayer(progressBarCondenserWaterLevel, 1);
        layeredPane.add(progressBarCondenserWaterLevel);
        
        //creation and instantiation of the sliders
        //every slider calls the appropriate method in the OperatingSoftware
        //requests its execution from the controller
        //and updates the gui
        sliderPump1RPM = new JSlider();
        sliderPump1RPM.setOpaque(false);
        sliderPump1RPM.setBounds(173, 581, 25, 108);
        sliderPump1RPM.setOrientation(SwingConstants.VERTICAL);
        sliderPump1RPM.setMaximum(1000);
        sliderPump1RPM.setValue(0);
        sliderPump1RPM.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                if (controller.isPumpOperational(1) && controlButtonsEnabled) {
                	controller.setPumpRpm(1, sliderPump1RPM.getValue());
                    updateGUI();
                }
            }
        });
        layeredPane.setLayer(sliderPump1RPM, 1);
        layeredPane.add(sliderPump1RPM);
          
        sliderPump2RPM = new JSlider();
        sliderPump2RPM.setBounds(384, 581, 25, 108);
        sliderPump2RPM.setOrientation(SwingConstants.VERTICAL);
        sliderPump2RPM.setOpaque(false);
        sliderPump2RPM.setValue(0);
        sliderPump2RPM.setMaximum(1000);
        sliderPump2RPM.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                if (controller.isPumpOperational(2) && controlButtonsEnabled) {
                	controller.setPumpRpm(2, sliderPump2RPM.getValue());
                    updateGUI();
                }
            }
        });
        layeredPane.setLayer(sliderPump2RPM, 1);
        layeredPane.add(sliderPump2RPM);
        
        sliderPump3RPM = new JSlider();
        sliderPump3RPM.setBounds(597, 581, 42, 108);
        sliderPump3RPM.setOpaque(false);
        sliderPump3RPM.setOrientation(SwingConstants.VERTICAL);
        sliderPump3RPM.setMaximum(1000);
        sliderPump3RPM.setValue(0);
        sliderPump3RPM.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                if (controller.isPumpOperational(3) && controlButtonsEnabled) {
                	controller.setPumpRpm(3, sliderPump3RPM.getValue());
                    updateGUI();
                }
            }
        });
        layeredPane.setLayer(sliderPump3RPM, 1);
        layeredPane.add(sliderPump3RPM);
        
        sliderRodsLevel = new JSlider();
        sliderRodsLevel.setOpaque(false);
        sliderRodsLevel.setBounds(63, 592, 25, 106);
        sliderRodsLevel.setOrientation(SwingConstants.VERTICAL);
        sliderRodsLevel.setValue(0);
        sliderRodsLevel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if(controlButtonsEnabled)
                {
                    controller.setControlRods(100-sliderRodsLevel.getValue());
                    updateGUI();
                }
                
            }
        });
        layeredPane.setLayer(sliderRodsLevel, 1);
        layeredPane.add(sliderRodsLevel);
        
        sliderNumberOfSteps = new JSlider();
        sliderNumberOfSteps.setBounds(46, 508, 305, 23);
        sliderNumberOfSteps.setOpaque(false);
        sliderNumberOfSteps.setValue(0);
        sliderNumberOfSteps.setMinimum(1);
        sliderNumberOfSteps.setMaximum(10);
        sliderNumberOfSteps.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                lblNumberOfSteps.setText("" + sliderNumberOfSteps.getValue());
            }
        });
        layeredPane.setLayer(sliderNumberOfSteps, 1);
        layeredPane.add(sliderNumberOfSteps);
        
        //starts a new game when pressed
        //and updates the gui
        btnNewSingleplayerGame = new JButton(newGameImageIcon);
        btnNewSingleplayerGame.setToolTipText("New Singleplayer Game");
        btnNewSingleplayerGame.setMargin(new Insets(0,0,0,0));
        btnNewSingleplayerGame.setBorder(null);
        btnNewSingleplayerGame.setBounds(749, 17, 40, 40);
        btnNewSingleplayerGame.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNewSingleplayerGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                btnNewSingleplayerGame.setEnabled(false);
                startNewSingleplayerGame();
                btnNewSingleplayerGame.setEnabled(true);
                sliderNumberOfSteps.setValue(1);
            }
        });
        layeredPane.setLayer(btnNewSingleplayerGame, 1);
        layeredPane.add(btnNewSingleplayerGame);
        
     // Button that toggles multiplayer on and off!
        btnNewMultiplayerGame = new JButton(multiGameImageIcon);
        btnNewMultiplayerGame.setToolTipText("Start new multiplayer game");
        btnNewMultiplayerGame.setMargin(new Insets(0,0,0,0));
        btnNewMultiplayerGame.setBorder(null);
        btnNewMultiplayerGame.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNewMultiplayerGame.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		btnNewMultiplayerGame.setEnabled(false);
                startNewMultiplayerGame();
                btnNewMultiplayerGame.setEnabled(true);
                sliderNumberOfSteps.setValue(1);
        	}
        });
        btnNewMultiplayerGame.setBounds(799, 17, 40, 40);
        layeredPane.setLayer(btnNewMultiplayerGame, 1);
        layeredPane.add(btnNewMultiplayerGame);
        
        
        //loads the saved game and updates the gui
        btnLoad = new JButton(loadGameImageIcon);
        btnLoad.setToolTipText("Load Game");
        btnLoad.setMargin(new Insets(0,0,0,0));
        btnLoad.setBorder(null);
        btnLoad.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLoad.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		controller.loadGame();
        		updateGUI();
        	}
        });
        btnLoad.setBounds(849, 17, 40, 40);
        layeredPane.setLayer(btnLoad, 1);
        layeredPane.add(btnLoad);
        
        //if the current game is not over it saves it
        btnSave = new JButton(saveGameImageIcon);
        btnSave.setToolTipText("Save Game");
        btnSave.setMargin(new Insets(0,0,0,0));
        btnSave.setBorder(null);
        btnSave.setBounds(899, 17, 40, 40);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnSave.setEnabled(false);
                if(!controller.isGameOver())
                	controller.saveGame();
                btnSave.setEnabled(true);
            }
        });
        layeredPane.setLayer(btnSave, 1);
        layeredPane.add(btnSave);
        
        //shows the scores so far
        //by calling a function that instantiates the scoresGUI
        btnShowScores = new JButton(viewScoresImageIcon);
        btnShowScores.setToolTipText("Leaderboard");
        btnShowScores.setMargin(new Insets(0,0,0,0));
        btnShowScores.setBorder(null);
        btnShowScores.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnShowScores.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		showScores();
        	}
        });
        btnShowScores.setBounds(999, 17, 40, 40);
        layeredPane.setLayer(btnShowScores, 1);
        layeredPane.add(btnShowScores);
        
        //displays the user manual by opening it with its default program
        btnShowManual = new JButton(viewManualImageIcon);
        btnShowManual.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		java.net.URL manualURL = this.getClass().getClassLoader().getResource("Manual.pdf");
                try{
                	 Desktop.getDesktop().open(new File(manualURL.getPath()));
                }catch (IOException e)
                {
                    e.printStackTrace();
                }

        	}
        });
        btnShowManual.setToolTipText("Manual");
        btnShowManual.setMargin(new Insets(0,0,0,0));
        btnShowManual.setBorder(null);
        btnShowManual.setBounds(949, 17, 40, 40);
        btnShowManual.setCursor(new Cursor(Cursor.HAND_CURSOR));
        layeredPane.setLayer(btnShowManual, 1);
        layeredPane.add(btnShowManual);
        
        //when this button is pressed it takes the value of the sliderNumber of time steps,
        //and issues a single time step at a time to the plant
        //if the plant has not failed and updates the gui
        //if the plant has failed - invokes the end game handler
        btnStep = new JButton(nextStepImageIcon);
        btnStep.setToolTipText("Step");
        btnStep.setOpaque(false);
        btnStep.setBounds(426, 500, 49, 39);
        btnStep.setMargin(new Insets(0,0,0,0));
        btnStep.setBorder(null);
        btnStep.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnStep.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	controller.step(sliderNumberOfSteps.getValue());
            	updateGUI();
                detectSwapAndNotify();
            }
        });
        layeredPane.setLayer(btnStep, 1);
        layeredPane.add(btnStep);


        //used to open and close the first valve
        btnValve1 = new JButton(valveOpenedImageIcon);
        btnValve1.setBounds(860, 508, 59, 23);
        btnValve1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnValve1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (controlButtonsEnabled)
                {
                	//checks if the valve 1 state and alternates it
                    if (controller.isValveOpen(1))
                    {
                    	controller.setValve(1, false);
                        updateGUI();
                        
                    } else
                    {
                    	controller.setValve(1, true);
                        updateGUI();
                    }
                }
            }
        });
        layeredPane.setLayer(btnValve1, 1);
        layeredPane.add(btnValve1);
        
        //used to open and close the second valve
        btnValve2 = new JButton(valveOpenedImageIcon);
        btnValve2.setBounds(968, 508, 59, 23);
        btnValve2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnValve2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (controlButtonsEnabled)
                {
                	//checks if the valve 1 state and alternates it
                    if (controller.isValveOpen(2))
                    {
                    	controller.setValve(2, false);
                        updateGUI();
                        
                    } else
                    {
                    	controller.setValve(2, true);
                        updateGUI();
                    }
                }
            }
        });
        layeredPane.setLayer(btnValve2, 1);
        layeredPane.add(btnValve2);
        
        
        // issues a repair command to pump 3 if it is not operational
        btnQuenchReactor = new JButton("Quench!");
        btnQuenchReactor.setBackground(new Color(30,255,30)); // Green
        btnQuenchReactor.setToolTipText(quenchToolTip);
        btnQuenchReactor.setBounds(38, 440, 100, 38);
        btnQuenchReactor.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuenchReactor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.quenchReactor();
                updateGUI();
            }
        });
        layeredPane.setLayer(btnQuenchReactor, 1);
        layeredPane.add(btnQuenchReactor);

        //issues a repair command to pump 1 if it is not operational
        btnRepairPump1 = new JButton(repairButtonDisabledImageIcon);
        btnRepairPump1.setBounds(283, 626, 59, 57);
        btnRepairPump1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRepairPump1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!controller.isPumpOperational(1) && controlButtonsEnabled)
                {
                	controller.repairPump(1);
                    updateGUI();
                    
                } 
            }
        });
        btnRepairPump1.setMargin(new Insets(0,0,0,0));
        btnRepairPump1.setBorder(null);
        layeredPane.setLayer(btnRepairPump1, 1);
        layeredPane.add(btnRepairPump1);
        
        //issues a repair command to pump 2 if it is not operational
        btnRepairPump2 = new JButton(repairButtonDisabledImageIcon);
        btnRepairPump2.setBounds(506, 626, 59, 57);
        btnRepairPump2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRepairPump2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!controller.isPumpOperational(2) && controlButtonsEnabled)
                {
                	controller.repairPump(2);
                    updateGUI();
                    
                } 
            }
        });
        btnRepairPump2.setMargin(new Insets(0,0,0,0));
        btnRepairPump2.setBorder(null);
        layeredPane.setLayer(btnRepairPump2, 1);
        layeredPane.add(btnRepairPump2);
        
        //issues a repair command to pump 3 if it is not operational
        btnRepairPump3 = new JButton(repairButtonDisabledImageIcon);
        btnRepairPump3.setBounds(726, 626, 59, 57);
        btnRepairPump3.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRepairPump3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!controller.isPumpOperational(3) && controlButtonsEnabled)
                {
                	controller.repairPump(3);
                    updateGUI();
                    
                } 
            }
        });
        btnRepairPump3.setMargin(new Insets(0,0,0,0));
        btnRepairPump3.setBorder(null);
        layeredPane.setLayer(btnRepairPump3, 1);
        layeredPane.add(btnRepairPump3);
        
        //issues a repair command to the turbine if it is not operational
        btnRepairTurbine = new JButton(repairButtonDisabledImageIcon);
        btnRepairTurbine.setBounds(836, 626, 59, 57);
        btnRepairTurbine.setMargin(new Insets(0,0,0,0));
        btnRepairTurbine.setBorder(null);
        btnRepairTurbine.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRepairTurbine.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!controller.isTurbineOperational()  && controlButtonsEnabled)
                {
                	controller.repairTurbine();
                    updateGUI();
                    
                } 
            }
        });
        layeredPane.setLayer(btnRepairTurbine, 1);
        layeredPane.add(btnRepairTurbine);
        
        //directly repairs the operating software - commands from this button are directly
        //executed by the plant and cannot fail
        btnRepairOperatingSoftware = new JButton(repairButtonDisabledImageIcon);
        btnRepairOperatingSoftware.setPreferredSize(new Dimension(93, 71));
        btnRepairOperatingSoftware.setBounds(937, 626, 59, 57);
        btnRepairOperatingSoftware.setMargin(new Insets(0,0,0,0));
        btnRepairOperatingSoftware.setBorder(null);
        btnRepairOperatingSoftware.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRepairOperatingSoftware.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!controller.isSoftwareOperational())
                {
                    controller.repairOperatingSoftware();
                    updateGUI();
                    
                } 
            }
        });
        layeredPane.setLayer(btnRepairOperatingSoftware, 1);
        layeredPane.add(btnRepairOperatingSoftware);
      
        //adds change listeners to the progress bars. Every time a bar's value is changed,
        //the colour of the bar changes depending on what is its value
        //temperature and pressure bars change colour smoothly from blue to red
        //health pressure bars change colour smoothly from red to green
        progressBarCondenserHealth.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                colourProgressBarRedToGreen(progressBarCondenserHealth);
            }
        });
        progressBarCondenserPressure.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                colourProgressBarBlueToRed(progressBarCondenserPressure);
            }
        });
        progressBarCondenserTemperature.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                colourProgressBarBlueToRed(progressBarCondenserTemperature);
            }
        });
        progressBarReactorWaterLevel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                colourProgressBarRedToGreen(progressBarReactorWaterLevel);
            }
        });
        progressBarReactorHealth.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                colourProgressBarRedToGreen(progressBarReactorHealth);
            }
        });
        progressBarReactorPressure.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                colourProgressBarBlueToRed(progressBarReactorPressure);
            }
        });
        progressBarReactorTemperature.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                colourProgressBarBlueToRed(progressBarReactorTemperature);
            }
        });
        
        //after everything but the name is set the gui is updates so it
        //synchronises with the plant
        updateGUI();
        nameTextField.setText(initialNameValue);
        
        // Init listeners for player 2.
        initialiseOpponentKeyListeners();
    }
    
    
    /**
     * This method takes a progress bar, gets its value and based on it
     * sets the bars colour, from blue to red, the calculations involved
     * give the desired colour
     * @param pb
     */
    private void colourProgressBarBlueToRed(JProgressBar pb)
    {
        int pbv = pb.getValue();
        
        //red green and blue colour components
        //used to create the new colour
        int r=0,g=0,b=255;
        
        if(pbv>0 && pbv<=20)
        {
            g=(int) (pbv*12.75);
            //casting is needed because of the new Colour()
            //constructor type - takes int values
        }
        else if(pbv>20 && pbv<=45)
        {
            r=0;g=255;b=(int) (255-((pbv-20)*10.2));
        }
        else if(pbv>45 && pbv<=65)
        {
            r= (int) ((pbv-45)*12.75);g=255;b=0;
        }
        else if(pbv>65 && pbv<=90)
        {
            r=255;g=(int) (255-((pbv-65)*10.2));b=0;
        }
        else if(pbv>90 && pbv<=100)
        {
            r=255;g=0;b=0;
        }
        pb.setForeground(new Color(r, g, b));
        
    }

    /**
     * This method takes a progress bar, gets its value and based on it
     * sets the bars colour, from red to green, the calculations involved
     * give the desired colour
     * @param pb
     */
    private void colourProgressBarRedToGreen(JProgressBar pb)
    {
        int pbv = pb.getValue();
        int r=255,g=0,b=0;
        
        if(pbv>0 && pbv<=20)
        {
            r=255; g=0; b=0;
        }
        else if(pbv>20 && pbv<=60)
        {
            r=255;g = (int) ((pbv-20)*6.375);
        }
        else if(pbv>60 && pbv<=90)
        {
            r= (int) (255-((pbv-60)*8.5));g=255;b=0;
        }
        else if(pbv>90 && pbv<=100)
        {
            r=0;g=255;b=0;
        }
        pb.setForeground(new Color(r, g, b));
    }

    /**
     * This method updates the appearance of the gui
     * synchronising it with the plant
     */
    private void updateGUI()
    {
        //restores the state of the control buttons and sliderRodsLevel variables to true
        controlButtonsEnabled = true;
        sliderRodsLevel.setEnabled(true);
        
        //updates the operators name that is shown to that that is stored,
        //useful when a game is being loaded
        nameTextField.setText(controller.getCurrentPlayerName());
        
        //updates the score and enables the buttons the control the valves
        //they can be disabled if the operatingSoftware is being repaired
        lblScore.setText(""+controller.getCurrentPlayerScore());
        btnValve1.setEnabled(true);
        btnValve2.setEnabled(true);
        
        //sets the button valve icons appropriately
        if(controller.isValveOpen(1))
            btnValve1.setIcon(valveOpenedImageIcon);
        else
            btnValve1.setIcon(valveClosedImageIcon);
        
        if(controller.isValveOpen(2))
            btnValve2.setIcon(valveOpenedImageIcon);
        else
            btnValve2.setIcon(valveClosedImageIcon);
        
        //sets the level of the control rods appropriately 
        tempValue = controller.getControlRodsLevel();
        if(tempValue >=0 && tempValue <= 100)
        	
        	//it is 100 - tempValue because in the plant it is shown how much
        	//the control rods are inside the rods while in the gui it shows how
        	//much the control rods are out of the rods
            sliderRodsLevel.setValue(100 - tempValue);

        //sets the values of the progress bars by scaling the value to 100
        tempValue = controller.getReactorHealth();
        if(tempValue >=0 && tempValue <= 100)
            progressBarReactorHealth.setValue(tempValue);
        
        tempValue = controller.getCondenserHealth();
        if(tempValue >=0 && tempValue <= 100)
            progressBarCondenserHealth.setValue(tempValue);
        
        tempValue = controller.getReactorTemperature();
        if(tempValue >=0 && tempValue <= 3000)
            progressBarReactorTemperature.setValue((int) tempValue/30);
        else if(tempValue > 3000)
        		progressBarReactorTemperature.setValue(100);
        
        tempValue = controller.getCondenserTemperature();
        if(tempValue >=0 && tempValue <= 2000)
            progressBarCondenserTemperature.setValue((int) tempValue/20);
        else if(tempValue > 2000)
    		progressBarCondenserTemperature.setValue(100);
        
        tempValue = controller.getReactorPressure();
        if(tempValue >=0 && tempValue <= 2000)
            progressBarReactorPressure.setValue((int) tempValue/20);
        else if(tempValue > 2000)
    		progressBarReactorPressure.setValue(100);
        
        tempValue = controller.getCondenserPressure();
        if(tempValue >=0 && tempValue <= 2000)
            progressBarCondenserPressure.setValue((int) tempValue/20);
        else if(tempValue > 2000)
    		progressBarCondenserPressure.setValue(100);
        
        tempValue = controller.getReactorWaterVolume();
        if(tempValue >=0 && tempValue <= 10000)
            progressBarReactorWaterLevel.setValue((int) tempValue/100);  
        
        tempValue = controller.getCondenserWaterVolume();
        if(tempValue >=0 && tempValue <= 10000)
            progressBarCondenserWaterLevel.setValue((int) tempValue/100);
        
        tempValue = controller.getControlRodsLevel();
        if(tempValue >=0 && tempValue <= 100)
            sliderRodsLevel.setValue((int) 100 - tempValue);
        
        tempValue = controller.getPumpRpm(1);
        if(tempValue >=0 && tempValue <= 1000)
            sliderPump1RPM.setValue((int) tempValue);
        
        tempValue = controller.getPumpRpm(2);
        if(tempValue >=0 && tempValue <= 1000)
            sliderPump2RPM.setValue((int) tempValue);
        
        tempValue = controller.getPumpRpm(3);
        if(tempValue >=0 && tempValue <= 1000)
            sliderPump3RPM.setValue((int) tempValue);
        
        //checks which components are being repaired and updates the gui in an appropriate way
        //if a component is being repaired its controls are disabled and a yellow light is showing
        if(controller.isPumpBeingRepaired(1))
        {
            lblPump1State.setIcon(stateBeingRepairedImageIcon);
            sliderPump1RPM.setEnabled(false);
            btnRepairPump1.setIcon(repairButtonDisabledImageIcon);
        }//if a component has failed and is not repaired its controls are disabled and red light is showing
        else if(!controller.isPumpOperational(1))
        {
            lblPump1State.setIcon(stateBrokenImageIcon);
            sliderPump1RPM.setEnabled(false);
            btnRepairPump1.setIcon(repairButtonEnabledImageIcon);
        }else//the component is in its normal safe operating state
        	 //its controls are enabled and green light is showing
        {
            lblPump1State.setIcon(stateSafeImageIcon);
            sliderPump1RPM.setEnabled(true);
            sliderPump1RPM.setValue(controller.getPumpRpm(1));
            btnRepairPump1.setIcon(repairButtonDisabledImageIcon);
        }
        
        if(controller.isPumpBeingRepaired(2))
        {
            lblPump2State.setIcon(stateBeingRepairedImageIcon);
            sliderPump2RPM.setEnabled(false);
            btnRepairPump2.setIcon(repairButtonDisabledImageIcon);
        }else if(!controller.isPumpOperational(2))
        {
            lblPump2State.setIcon(stateBrokenImageIcon);
            sliderPump2RPM.setEnabled(false);
            btnRepairPump2.setIcon(repairButtonEnabledImageIcon);
        }else
        {
            lblPump2State.setIcon(stateSafeImageIcon);
            sliderPump2RPM.setEnabled(true);
            sliderPump2RPM.setValue(controller.getPumpRpm(2));
            btnRepairPump2.setIcon(repairButtonDisabledImageIcon);
        }
        
        if(controller.isPumpBeingRepaired(3))
        {   lblPump3State.setIcon(stateBeingRepairedImageIcon);
            sliderPump3RPM.setEnabled(false);
            btnRepairPump3.setIcon(repairButtonDisabledImageIcon);
        }else if(!controller.isPumpOperational(3))
        {   
            lblPump3State.setIcon(stateBrokenImageIcon);
            sliderPump3RPM.setEnabled(false);
            btnRepairPump3.setIcon(repairButtonEnabledImageIcon);
        }else
        {
           
            lblPump3State.setIcon(stateSafeImageIcon);
            sliderPump3RPM.setEnabled(true);
            sliderPump3RPM.setValue(controller.getPumpRpm(3));
            btnRepairPump3.setIcon(repairButtonDisabledImageIcon);
        }
        
        if(controller.isTurbineBeingRepaired())
        {
            lblTurbineState.setIcon(stateBeingRepairedImageIcon);
            btnRepairTurbine.setIcon(repairButtonDisabledImageIcon);
        }else if(!controller.isTurbineOperational())
        {
            lblTurbineState.setIcon(stateBrokenImageIcon);
            btnRepairTurbine.setIcon(repairButtonEnabledImageIcon);
        }else
        {
            lblTurbineState.setIcon(stateSafeImageIcon);
            btnRepairTurbine.setIcon(repairButtonDisabledImageIcon);
        }
        
        //if the operating software is being repaired all components that rely on it for their commands to
        //be executed are disabled
        if(controller.isSoftwareBeingRepaired())
        {
            lblOperatingSoftwareState.setIcon(stateBeingRepairedImageIcon);
            btnRepairOperatingSoftware.setIcon(repairButtonDisabledImageIcon);
            controlButtonsEnabled = false;
            sliderPump1RPM.setEnabled(false);
            sliderPump2RPM.setEnabled(false);
            sliderPump3RPM.setEnabled(false);
            sliderRodsLevel.setEnabled(false);
            btnRepairPump1.setIcon(repairButtonDisabledImageIcon);
            btnRepairPump2.setIcon(repairButtonDisabledImageIcon);
            btnRepairPump3.setIcon(repairButtonDisabledImageIcon);
            btnRepairTurbine.setIcon(repairButtonDisabledImageIcon);
            btnValve1.setEnabled(false);
            btnValve2.setEnabled(false);
        }else if(!controller.isSoftwareOperational())
        {
        	//otherwise just set its light to show red and enable its repair button
            lblOperatingSoftwareState.setIcon(stateBrokenImageIcon);
            btnRepairOperatingSoftware.setIcon(repairButtonEnabledImageIcon);
        }else
        {   //otherwise just set its light to show green and disable its repair button
            lblOperatingSoftwareState.setIcon(stateSafeImageIcon);
            btnRepairOperatingSoftware.setIcon(repairButtonDisabledImageIcon);
        }
        
        // Quench button color.
        if (controller.isQuenchAvailable()) {
        	btnQuenchReactor.setBackground(new Color(30,255,30)); // Green
        } else {
        	btnQuenchReactor.setBackground(new Color(255,30,30)); // Red
        }
        
        if (controller.isMultiplayer()) {
        	lblRandomFailures.setText("Random Failures : " + 
        							  (controller.isRandomFailures() ? "On!" : "Off"));
        	lblStepsUntilSwap.setText(zeroToBlankString(controller.numberOfStepsUntilSwap()));
        	lblPump1Failable.setText(zeroToBlankString(controller.getNumStepsUntilPumpFailable(1)));
        	lblPump2Failable.setText(zeroToBlankString(controller.getNumStepsUntilPumpFailable(2)));
        	lblPump3Failable.setText(zeroToBlankString(controller.getNumStepsUntilPumpFailable(3)));
        	lblTurbineFailable.setText(zeroToBlankString(controller.getNumStepsUntilTurbineFailable()));
        	lblOSFailable.setText(zeroToBlankString(controller.getNumStepsUntilOSFailable()));
        } else { 
        	lblStepsUntilSwap.setText("");
        	lblRandomFailures.setText("");
        	lblPump1Failable.setText("");
        	lblPump2Failable.setText("");
        	lblPump3Failable.setText("");
        	lblTurbineFailable.setText("");
        	lblOSFailable.setText("");
        }
        
    }
    
    private String zeroToBlankString(int i) {
    	return (i == 0) ? "" : "" + i;
    }
    
    private void checkEndGameAndHandleIt()
    {
    	if (controller.isGameOver() && !displayedEndGameDialog) {
	    	if (controller.isMultiplayer()) {
	    		System.out.println("1");
	    		if (controller.getCurrentPlayerNumber() == 1) { 
	    			// Swapped!
	    			System.out.println("2");
	    		} else {
	    			// Player 2 must've died... 
	    			System.out.println("3");
	    			updateGUI();
	    			showMultiplayerEndGameDialog();
	    			initGame();
	    		}
	    	} else {
	    		// Single player
	    		updateGUI();
	    		showSingleplayerEndGameDialog();
	    		//EndGameGUI endGameGui = new EndGameGUI(this, controller.getPlayerOneScore());
	    	}
			sliderNumberOfSteps.setValue(1);
    	}
    }

	/**
     * 
     * @return the main frame - used for relative positioning
     */
    public JFrame getFrame()
    {
    	return frame;
    }
    
    
    /**
     * called when the the scores should be shown - creates a new ScoresGUI object passing a reference to this object,
     * and the plantControllers object
     */
    private void showScores()
    {
    	ScoresGUI scoresGui = new ScoresGUI(this, controller);
    }
    

    private void showSingleplayerEndGameDialog() {
		String messageText = controller.getPlayerOneName() + 
							 ", you scored:\n" +
							 controller.getPlayerOneScore() +
							 "\n\nWhat would you like to do now?";
		Object[] options = {"Play again", "Play 2-player",  "Show Highscores", "Exit" };
    	String titleText = "Nice score!";		
    	int opt = JOptionPane.showOptionDialog(null, messageText, titleText,
    	JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
    	null, options, options[0]);
    	switch (opt) {
    		case 0: // Play again
    			startNewSingleplayerGame();
    			break;
    		case 1: // Play wid dem ova playaz
    			startNewMultiplayerGame();
    			break;
    		case 2: // show highscores 
    			showScores();
    			break;
    		case 3: // Exit!
    		default:
    			System.exit(0);
    			break;
    	}
    	
	}
    
    private void showMultiplayerEndGameDialog() {
    	String messageText = controller.getPlayerOneName() + 
    						 " finished with a score of:\n" +
    						 controller.getPlayerOneScore() + "\n" +
    						 controller.getPlayerTwoName() + 
    						 " finished with a score of:\n" +
    						 controller.getPlayerTwoScore() + "\n\n";
    	String titleText;
    	if (controller.getPlayerOneScore() > controller.getPlayerTwoScore()) {
    		// Player 1 wins!
    		messageText += controller.getPlayerOneName() + " wins!";
    		titleText = controller.getPlayerOneName() + " won!";
    	} else {
    		// Player 2 wins!
    		messageText += controller.getPlayerTwoName() + " wins!";
    		titleText = controller.getPlayerTwoName() + " won!";
    	}
		displayedEndGameDialog = true;
    	JOptionPane.showMessageDialog(null, messageText, titleText, JOptionPane.PLAIN_MESSAGE);
    }


	@Override
	public void update() {
		checkEndGameAndHandleIt();
		updateGUI();
	}
 
	public void detectSwapAndNotify() {
		if (controller.isMultiplayer()) {
			if (controller.getCurrentPlayerNumber() == 2) { // Swapped!
				if (!displayedSwapDialog) { // Need to notify players of swap 
					displayedSwapDialog = true;
					JOptionPane.showMessageDialog(null, controller.getPlayerOneName() +
														" finished with a score of:\n" +
														controller.getPlayerOneScore() + "\n" +
														"Please swap controls. " +  
														"Try to beat that, " + controller.getPlayerTwoName() 
														+ ".");
					lblOtherPlayerScore.setText(controller.getPlayerOneName() + " : " + 
												controller.getPlayerOneScore());
					sliderNumberOfSteps.setValue(1);
				}
				
			}
		}
	}
	
	private void startNewSingleplayerGame() {
		String playerName = JOptionPane.showInputDialog(null,
				  "Please enter your name",
				  "Enter your name",
				  JOptionPane.QUESTION_MESSAGE);
		controller.newSingleplayerGame(playerName);
		displayedSwapDialog = false;
		displayedEndGameDialog = false;
		updateGUI();
	}
	
	private void startNewMultiplayerGame() {
		String playerOneName = JOptionPane.showInputDialog(null,
		        "Player 1, please enter your name",
		        "Enter your name",
		        JOptionPane.QUESTION_MESSAGE);
		String playerTwoName = JOptionPane.showInputDialog(null,
		      	"Player 2, please enter your name",
		      	"Enter your name",
		      	JOptionPane.QUESTION_MESSAGE);
		String messageText = playerOneName + ", you will be first to operate the plant.\n" +
		      				 "Use the mouse to control the pumps, valves and control rods.\n" +
		      				 "Choose how many timesteps you would like to advance by and press\n" +
		      				 "the step button to progress through the game. If you lose control\n" +
		      				 "of the reactor, there is a tank of emergency cooling water that\n" +
		      				 "can be introduced into the core - this is the Quench feature, use\n" +
		      				 "wisely - you only have enough water for one usage!" +
		      				 "\n\n" +
		      				 playerTwoName + ", you will be working to destroy the reactor by\n" +
		      				 "causing failures in critical components. Use the number keys 1-5 to\n" +
		      				 "fail the components that appear across the bottom of the screen.\n" +
		      				 "Optionally, you can toggle random failures on/off by pressing 6 but\n" +
		      				 "you will not be able to force failure of any components while random\n" +
		      				 "failures are enabled." +
		      				 "\n\n" +
		      				 "After " + controller.getStepsPerPlayer() + " timesteps, you will swap controls and " +
		      				 playerTwoName + " will attempt\nto set a higher score." ;
		
		JOptionPane.showMessageDialog(null, messageText);
		controller.newMultiplayerGame(playerOneName, playerTwoName);
		displayedSwapDialog = false;
		displayedEndGameDialog = false;
		updateGUI();
	}
    
	
	// ------- Player 2 key listeners! -------

		/**
		 * Initialises a key press listener for the entire application, not just
		 * specific components.
		 */
		private void initialiseOpponentKeyListeners() {
			KeyboardFocusManager.getCurrentKeyboardFocusManager()
					.addKeyEventDispatcher(new KeyEventDispatcher() {
						@Override
						public boolean dispatchKeyEvent(KeyEvent event) {
							// Only capture and act upon KEY_PRESSED events.
							if (event.getID() == KeyEvent.KEY_PRESSED)
								keyPressActionHandler(event);
							return false;
						}
					});
		}

		/**
		 * Acts upon KeyEvents that it is passed. ie fails the relevant component.
		 * 
		 * @param event KeyEvent
		 */
		private void keyPressActionHandler(KeyEvent event) {
			if (controller.isMultiplayer()) {
				switch (event.getKeyCode()) {
				case FAIL_PUMP_1:
					controller.failPump(1);
					break;
				case FAIL_PUMP_2:
					controller.failPump(2);
					break;
				case FAIL_PUMP_3:
					controller.failPump(3);
					break;
				case FAIL_TURBINE:
					controller.failTurbine();
					break;
				case FAIL_OS:
					controller.failOS();
					break;
				case TOGGLE_RANDOM_FAILURES:
					controller.toggleRandomFailures();
					updateGUI();
				default:
					break;
				}
			}
		}

}
