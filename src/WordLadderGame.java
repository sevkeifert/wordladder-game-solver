/*

Kevin Seifert - GPL 2002 

This was one of my first Java apps, so the code might not be the cleanest. 
It was an applet initially, and was converted to a Swing app.  It contains the
logic for a word ladder solver (which is required, since there's more than one
possible answer to a puzzle).  

*/

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;
import javax.swing.border.*;
import javax.sound.sampled.*;


public class WordLadderGame extends JPanel {

	Random rand = new Random();
	PlayingBoard board = new PlayingBoard();
	WordList dictionary;
	Word [] wordladder;
	JComponent gamecontrol;
	Clip audioclip;

	public static int SPACER = 20;

	public static void main(String[] args)throws Exception {
		Frame f = new Frame("Word Ladder Game");
		f.addWindowListener(new java.awt.event.WindowAdapter() {
					public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
					};
				});
		WordLadderGame wordladder = new WordLadderGame();
		wordladder.setSize(400,300);
		wordladder.setBorder(new EmptyBorder(SPACER,SPACER,SPACER,SPACER));
		f.add(wordladder);
		f.pack();
		wordladder.init();
		f.setSize(400,300 + 20); 
		f.show();
	}

	public JPanel getContentPane() {
		return this;
	}

	public void init()throws Exception {
		gamecontrol = createComponents();
		getContentPane().setBackground(Color.white);
		getContentPane().setLayout(new GridLayout(1, 2));
		getContentPane().add(gamecontrol,BorderLayout.WEST);
		getContentPane().add(board,BorderLayout.EAST);

		start();
	}

	// start application
	public void start() {
		newGame(7,9);
	}

	// stop application
	public void stop() {
	}

	// tada sound
	public void playSound() {
		try { 
			String file = "tada.wav";
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(this.getClass().getResource(file));
			AudioFormat format = inputStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			audioclip =(Clip)AudioSystem.getLine(info);
			audioclip.open(inputStream);
			audioclip.start();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	// start new game
	public void newGame(int wlen , int llen) {
		dictionary = new WordList(wlen);
		wordladder = dictionary.getRandLadder(llen);
		if (wordladder!=null) {
			getContentPane().remove(board);
			board = new PlayingBoard(wordladder, dictionary, this);
			getContentPane().add(board);
			board.revalidate(); 
			board.repaint();
		}
	}

	// init ui widgets
	public JComponent createComponents() {
		final JLabel label1 = new JLabel("Word Size:");
		final JLabel label2 = new JLabel("Ladder Size:");

		final JButton button = new JButton("New Game");
		final JButton button2 = new JButton("Hint");

		String [] items = {"4","5","6","7"};
		String [] items2 = {"5","6","7","8","9"};
		final JComboBox wsgroup = new JComboBox(items);
		final JComboBox lsgroup = new JComboBox(items2);

		//button event handles
		button.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				newGame(Integer.parseInt((String)wsgroup.getSelectedItem()), 
					Integer.parseInt((String)lsgroup.getSelectedItem())
					);
				}
			}
		);
		//button event handles
		button2.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				board.hint();
				}
			}
		);
		JPanel pane = new JPanel();
		pane.setBackground(Color.white);
		pane.setBorder(BorderFactory.createEmptyBorder(
					0, //top
					0, //left
					0, //bottom
					10)//right
				);

		pane.setLayout(new FlowLayout());

		pane.add(label1);
		pane.add(wsgroup);
		pane.add(label2);
		pane.add(lsgroup);
		pane.add(button);
		pane.add(button2);

		return pane;
	}
}


