package swing;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JLabel;
import java.awt.ComponentOrientation;
import java.awt.Font;

/**
 * This class creates its own gui which has a relative position the the mainGUI.
 * It takes the end game high score and displays it.
 */
public class EndGameGUI {

	//the main frame
	private JFrame frame;
	
	//reference to the main gui
	private MainGUI mainGUI;
	
	//where the background picture is displayed
	private JLabel lblBackground;
	
	//where the score is displayed
	private JLabel lblShowScore;
	
	//takes the score from the mainGUI
	private int score;

	
	
	/**
	 * instantiates this class' gui, shows the high score and shows the frame.
	 * @param mainGUI
	 * @param score
	 */
	public EndGameGUI(MainGUI mainGUI,int score) {
		this.mainGUI = mainGUI;
		this.score=score;
		
		initialize();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);
		
	}

	/**
	 * Initialise the contents of the frame.
	 * Creates a label with image icon and puts it in the background,
	 * creates a label with the score and puts in foreground.
	 */
	private void initialize() 
	{
		//read the background image
		java.net.URL imageURL = this.getClass().getClassLoader().getResource("endGameBackground.png");
		ImageIcon backgroundImageIcon = new ImageIcon(imageURL);
		
		//creates and sets the frame
		frame = new JFrame();
		frame.setBounds(100, 100, 506, 177);
		frame.setLocation(mainGUI.getFrame().getLocation());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		//creates and adds the layered pane
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, 499, 151);
		frame.getContentPane().add(layeredPane);
		
		//instantiates and adds the background label		
		lblBackground = new JLabel(backgroundImageIcon);
		layeredPane.setLayer(lblBackground, 0);
		lblBackground.setBounds(0, 0, 499, 151);
		layeredPane.add(lblBackground);
		
		//instantiates and adds the foreground label
		lblShowScore = new JLabel("0");
		lblShowScore.setText(""+score);
		lblShowScore.setBounds(169, 91, 164, 37);
		lblShowScore.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblShowScore.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		layeredPane.setLayer(lblShowScore, 1);
		layeredPane.add(lblShowScore);
	}
}
