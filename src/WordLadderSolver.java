import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import java.io.*;
import java.net.*;
import java.util.*;

import java.util.zip.*;

// this solves a word ladder puzzle 
// if solution exists within the given dictionaries

public class WordLadderSolver extends JPanel
{
	JTextArea list;
	WordList WL;

	public static int SPACER = 20;

	// for application
    public static void main(String[] args) {

		Frame f = new Frame("Word Ladder Solver");
		f.addWindowListener(new java.awt.event.WindowAdapter() {
					public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
					};
				});

		WordLadderSolver wordladder = new WordLadderSolver();
		wordladder.setSize(400,300);
		wordladder.setBorder(new EmptyBorder(SPACER,SPACER,SPACER,SPACER));
		f.add(wordladder);
		f.pack();
		wordladder.init();
		f.setSize(400,300 + 20); 
		f.show();
	}

	JPanel getContentPane() { 
		return this;
	}
	// end application	

    public void init() {
        getContentPane().add(createComponents(), BorderLayout.CENTER);
    }

	public void println(String str) {
		list.append(str + "\n");
	}

	public void clearScreen() {
		list.setText("");
	}

	public Component createComponents() {

		final JLabel label1 = new JLabel("Starting Word:");
		final JLabel label2 = new JLabel("Ending Word:");
		final JLabel label3 = new JLabel("");
		final JTextField word1 = new JTextField(20);
		final JTextField word2 = new JTextField(20);	
		word1.setFont(new Font("Monospaced",Font.PLAIN,12));
		word2.setFont(new Font("Monospaced",Font.PLAIN,12));
		word1.requestFocus();
		final JButton button = new JButton("Find Word Ladder");

		//button event handles
		button.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					solve(word1.getText(),word2.getText());
				}
			}
		);

		button.addKeyListener(
			new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					solve(word1.getText(),word2.getText());
				}
			}
		);

 		list = new JTextArea(10,24);
		list.setEditable(false);
		list.setFont(new Font("Monospaced",Font.PLAIN,12));
		list.setWrapStyleWord(true);
		list.setLineWrap(true);		
		final JScrollPane scrollingList = new JScrollPane(list);
		scrollingList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		JPanel pane = new JPanel();
		pane.setBorder(BorderFactory.createEmptyBorder (
				10, //top
				10, //left
				10, //bottom
				5) //right
			);

		pane.setLayout(new GridLayout(0, 1));
		label1.setLabelFor(word1);
		label2.setLabelFor(word2);
		label3.setLabelFor(button);

		pane.add(label1);
		pane.add(word2);
		pane.add(label2);
		pane.add(word1);
		pane.add(label3);
		pane.add(button);

		JPanel pane2 = new JPanel();
		pane2.setBorder(BorderFactory.createEmptyBorder(
				15, //top
				5, //left
				10, //bottom
				10) //right
			);

		pane2.add(scrollingList); 
		JPanel pane3 = new JPanel();
		pane3.add(pane);
		pane3.add(pane2);
		return pane3;
	}

	public void solve(String word1, String word2) {

		try {
			clearScreen();
			if (word1.length()==0) {
				println("you must type in a starting word and an ending word.");
			} else if (word1.length() != word2.length()) {
				println("words must be the same length, silly.");
			} else {
				clearScreen();
				//WL = new WordList(word1.length(),getCodeBase());
				WL = new WordList(word1.length());
				int wid2 = WL.getId(word2);
				int wid1 = WL.getId(word1);
				if (wid1 > -1 && wid2 > -1) {	
					Word [] wordladder = WL.findLadder(wid1,wid2);
					if(wordladder!=null) {
						for (int i=0;i<wordladder.length; i++) {
							println(wordladder[i].getWord());
						}
					} else {
						println("no word ladder found.");
					}
				} else {
					println("no word ladder found.");
				}
			}				
		} catch(Exception e) {
				System.out.println(e);
		}
	}

}

