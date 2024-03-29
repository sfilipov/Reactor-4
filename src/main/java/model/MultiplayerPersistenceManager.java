package model;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiplayerPersistenceManager implements GamePersistence, Serializable {
	private MultiplayerModel model;
	private List<HighScore> highScores;
	
	public MultiplayerPersistenceManager(MultiplayerModel model) {
		this.model = model;
		highScores = new ArrayList<HighScore>();
		readHighScores();
	}

	@Override
	public void saveGame() {
		try {
			FileOutputStream fileOut = new FileOutputStream("save.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(model);
			out.close();
			fileOut.close();
		}
		catch (IOException ex) {
		}
	}

	@Override
	public void loadGame() {
		try {
			File f = new File("save.ser");
			if(f.exists()) {
				FileInputStream fileIn = new FileInputStream(f);
				ObjectInputStream in = new ObjectInputStream(fileIn);
				MultiplayerModel savedModel = (MultiplayerModel) in.readObject();
				in.close();
				fileIn.close();
				this.model.copy(savedModel);
			}
		}
		catch (IOException io) {
		}
		catch (ClassNotFoundException c) {
		}
	}

	@Override
	public void addHighScore(HighScore highScore) {
		if (highScore.getHighScore() > 0) {
			for (int i=0; i < 20; i++) {
				if (i < highScores.size()) {
					HighScore oldHighScore = highScores.get(i);
					if (oldHighScore.compareTo(highScore) < 0) {
						highScores.add(i, highScore);
						writeHighScores();
						break;
					}
				}
				else {
					highScores.add(highScores.size(), highScore);
					writeHighScores();
					break;
				}
			}
		}
	}

	@Override
	public List<HighScore> getHighScores() {
		if(highScores.size() > 20) {
			highScores = highScores.subList(0, 20); //Trims the high scores list to only the first 20 elements
			writeHighScores();
		}
		return highScores;
	}
	
	/**
	 * Loads all saved highscores from a file called "highscore.ser" to plant's highscores list.
	 */
	private void readHighScores() {
		try {
			File f = new File("highscores.ser");
			if(f.exists()) {
				FileInputStream fileIn = new FileInputStream(f);
				ObjectInputStream in = new ObjectInputStream(fileIn);
				List<HighScore> highScores = (List<HighScore>) in.readObject();
				in.close();
				fileIn.close();
				this.highScores = highScores;
			}	
		} catch (EOFException eof) {
		} catch (IOException io) {
		} catch (ClassNotFoundException cnf) {
		}
	}
	
	/**
	 * Writes all highscores currently inside plant to a file called "highscores.ser".
	 */
	private void writeHighScores() {
		try {
			FileOutputStream fileOut = new FileOutputStream("highscores.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(highScores);
			out.close();
			fileOut.close();
		} catch (IOException io) {
		}
	}
}
