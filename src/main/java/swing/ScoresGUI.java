package swing;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JTextArea;

import model.HighScore;

import simulator.Multiplayer2Controller;
import simulator.PlantController;


/**
 * This class creates its own gui which has a relative position the the mainGUI.
 * It takes a list of the high scores in the game and displays them.
 */
public class ScoresGUI {

	//the main frame
	private JFrame frame;
	
	//reference to the main gui
	private MainGUI mainGUI;
	
	//reference to the plant
	private Multiplayer2Controller controller;
	
	//where the scores are displayed
	private JTextArea textArea;
	
	//where the background picture is displayed
	private JLabel lblBackground;


	
	/**
	 * instantiates this class' gui, prints the high scores and shows the frame.
	 * @param mainGUI
	 * @param controller
	 */
	public ScoresGUI(MainGUI mainGUI, Multiplayer2Controller controller)
	{
		this.mainGUI = mainGUI;
		this.controller = controller;
		initialize();
		printHighScores();
		frame.setVisible(true);
	}

	/**
	 * Initialise the contents of the frame.
	 * Creates a label with image icon and puts it in the background,
	 * creates a text area with the high scores and puts in foreground.
	 */
	private void initialize() {
		
		//reads the background image
		java.net.URL imageURL = this.getClass().getClassLoader().getResource("scoresBackground.png");
        ImageIcon backgroundImageIcon = new ImageIcon(imageURL);
		
        //creates and sets the frame
        frame = new JFrame();
		frame.setLocation(mainGUI.getFrame().getLocation());
		frame.setBounds(100, 100, 408, 690);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setAlwaysOnTop(true);
		frame.setResizable(false);
		
		//creates and adds the layered pane
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, 403, 665);
		frame.getContentPane().add(layeredPane);
		
		//instantiates and adds the background label
		lblBackground = new JLabel(backgroundImageIcon);
		layeredPane.setLayer(lblBackground, 0);
		lblBackground.setBounds(0, 0, 403, 662);
		layeredPane.add(lblBackground);
		
		//instantiates and adds the text area
	    textArea = new JTextArea();
	    textArea.setFont(new Font("Tahoma", Font.PLAIN, 15));
	    textArea.setOpaque(false);
	    textArea.setBounds(54, 56, 297, 575);
	    textArea.setForeground(new Color(0, 128, 0));
	    textArea.setColumns(30);
	    textArea.setEditable(false);
	    layeredPane.setLayer(textArea, 1);
	    layeredPane.add(textArea);

	}
	
	
	/**
	 * Reads the list of high scores and appends them to the text area 
	 */
	private void printHighScores() {
		textArea.setText("");
		
		List<HighScore> highScores = controller.getHighScores();

		if (!highScores.isEmpty()) {
			int i = 1;
    		for (HighScore highScore : highScores) {
    			textArea.append("" + i + ": " + highScore.getName() + ": " + highScore.getHighScore() + "\n");
    			i++;
    		}
		}
		
	}
}
