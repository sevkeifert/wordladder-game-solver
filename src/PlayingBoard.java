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

// this class creates the clickable squares with letters

class PlayingBoard extends JPanel {

	int cellsize = 20;
	int cellspacing = 5;
	int wordlength;
	int ladderlength;

	WordLadderGame parent;

	Word[] wordladder;
	WordList dictionary;
	LetterCell [][] board;

	//first open slot
	int defaultrow = 1;
	int defaultcol = 0;

	public PlayingBoard() {
	}

	public PlayingBoard(Word[] wordladder, WordList dictionary, WordLadderGame parent) {
		super(null);
		setBackground(Color.white);
		this.wordladder = wordladder;
		this.dictionary = dictionary;
		this.parent = parent;
		PlayingBoard(wordladder[0].getWord().length(), wordladder.length);
		setLadderBounds(wordladder[0].getWord(),wordladder[wordladder.length-1].getWord());

	}

	public void PlayingBoard(int wordlength, int ladderlength) {
		board = new LetterCell[ladderlength][wordlength];
		this.wordlength = wordlength;
		this.ladderlength = ladderlength; 
		for (int j=0;j<ladderlength ;j++) {
			for (int i=0;i<wordlength;i++) {
				board[j][i] = new LetterCell(i,j);
				board[j][i].setBounds(i * cellsize, j*(cellspacing + cellsize), cellsize, cellsize);
				this.add(board[j][i]);
			}
		}
	}

	//----cell operations---- 
	public boolean inBounds(int row, int col) {
		return (row >= 0 && col >= 0  && row < ladderlength && col < wordlength);	
	}

	public void doKeyPress(int k, int r, int c) {
		int col = c;
		int row = r;	
		if (k == KeyEvent.VK_SPACE) {
			wipeRow(r);
		} else if (k >= KeyEvent.VK_A && k <= KeyEvent.VK_Z) {
			wipeRow(r);
			getCell(r,c).setKeyLetter(true);
			getCell(r,c).setLetter(KeyEvent.getKeyText(k));
			autoFill();
			checkGameOver();
		} else {
			//arrows: increment pointer
			switch(k) {
				case KeyEvent.VK_UP:
					row--;
					break;
				case KeyEvent.VK_DOWN:
					row++;
					break;
				case KeyEvent.VK_LEFT:
					col--;
					break;
				case KeyEvent.VK_RIGHT:
					col++;
					break;
			}
			//check pointer moved
			if (col != c || row != r) {
				cellSelect(row, col);
			}
		}
	}

	public void cellSelect(int row, int col) {
		//sends focus to cell
		//arrow wrapping, jump to next word
		if (col >= wordlength) {
			col = 0;
			row++;
		}
		if (col < 0) {
			col = wordlength-1;
			row--;
		}
		if (inBounds(row, col)&& !getCell(row, col).isLocked())
			getCell(row, col).requestFocus();
	}

	//get reference to cell
	public LetterCell getCell(int row, int col) {
		if (inBounds(row, col)) {
			return board[row][col];
		}
		return null;
	}

	//----row operations----
	public void setWord(int row, String s) {
		int len = s.length();
		if (len == wordlength && inBounds(row,0)) {
			setRowKeyLetter(row,false);
			for (int i=0;i<len;i++) {
				getCell(row,i).setLetter(s.charAt(i));
			}
		}
	}

	public void setRowLock(int row, boolean state) {
		for (int i=0;i<wordlength;i++) {
			getCell(row,i).setLock(state);
		}
	}

	public void setRowKeyLetter(int row, boolean state) {
		for (int i=0;i<wordlength;i++) {
			getCell(row,i).setKeyLetter(state);
		}
	}

	public String getWord(int row) {
		if (inBounds(row,0)) {
			StringBuffer sb = new StringBuffer(wordlength);
			for (int i=0;i<wordlength;i++) {
				sb.append(getCell(row,i).getLetter());
			}
			return new String(sb);
		}
		return "";
	}

	public void setLadderBounds(String s1, String s2) {
		setRowLock(0,true);
		setRowLock(ladderlength-1,true);
		setWord(0,s1);
		setWord(ladderlength-1,s2);
	}

	public void wipeRow(int row) {
		for (int i=0;i<wordlength;i++) {	
			getCell(row,i).setKeyLetter(false);
			getCell(row,i).setLetter("");
		}
	}

	public void hint() {
		setWord(defaultrow, wordladder[defaultrow].getWord());
		checkGameOver();
		cellSelect(defaultrow,defaultcol);
	}

	public boolean rowIncomplete(int row) {
		int len = getWord(row).length();
		return (len > 0 && len < wordlength);
	}

	//----board operations below----
	//ends are always set.  fill incomplete rows with letters from the neighbors
	public void autoFill() {
		//sweep down
		for (int j=1; j<ladderlength-1; j++) {
			if (rowIncomplete(j)) {
				for (int i=0;i<wordlength;i++) {
					if (getCell(j,i).getLetter().length()<= 0) {
						getCell(j,i).setLetter(getCell(j-1,i).getLetter());
					}
				}
			}
		}
		//sweep up
		for (int j=ladderlength-2; j>0; j--) {
			if (rowIncomplete(j)) {
				for (int i=0;i<wordlength;i++) {
					if (getCell(j,i).getLetter().length()<= 0) {
						getCell(j,i).setLetter(getCell(j+1,i).getLetter());
					}
				}
			}
		}
	}

	//----game logic----
	public void checkGameOver() {
		boolean won = true;
		if (won) {
			//person will probably start from top, so, start checking empty
			//rows from bottom up
			for (int j=ladderlength-2; j>0; j--) {
				if (rowIncomplete(j)) {
					won = false;
					break;
				}
			}
		}
		//id lookups...
		//could lookup ids as person types, but cost would be higher. person
		//will make more mistakes upfront.
		int [] wids = new int [ladderlength];
		if (won) {
			for (int j=0; j<ladderlength; j++) {
				wids [j] = dictionary.getId(getWord(j));
				if (wids[j]<0) {
					won = false;
					break;
				}
			}
		}
		if (won) {
			for (int j=1; j<ladderlength; j++) {
				if (!dictionary.checkRung(wids[j-1], wids[j])) {
					won = false;
					break;
				}
			}
		}
		if (won) {
			wonGame();
		}
	}

	//reward screen for winning.
	//spectacular flashing lights, sounds :)
	public void wonGame() {
		for (int j=1; j<ladderlength-1; j++) {
			for (int i=0;i<wordlength;i++) {
				getCell(j,i).setLock(true);
			}
		}
		parent.playSound();
	}


	//----INNER CLASS: CLICKABLE LETTER SQUARE----
	class LetterCell  extends JPanel {
		boolean keyletter = false;
		boolean hasfocus = false;
		String letter;
		boolean lock;
		int row;
		int col;

		Color bordercolor = new Color(163,136,191);
		Color cellbgcolor = new Color(225,225,225);
		Color cellhicolor = new Color(156,154,206);
		Color celllockcolor  = new Color(186,175,200);
		Color fontcolor = Color.black;

		public LetterCell(int col, int row) {
			super();
			setOpaque(true);
			setBackground(cellbgcolor);
			setBorder(BorderFactory.createLineBorder(bordercolor));
			setFont(new Font("Sans-Serif", Font.BOLD, 12));

			lock=false;
			this.row = row;
			this.col = col;
			letter = "";

			addMouseListener(
				new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						if (!lock)requestFocus();
					}
					public void mouseEntered(MouseEvent e) {
						if (!lock)setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					}
					public void mouseExited(MouseEvent e) {
						if (!lock)setCursor(Cursor.getDefaultCursor());
					}
				}
			);

			addFocusListener(
				new FocusAdapter() {
					public void focusGained(FocusEvent evt) {
						hasfocus = true;
						defaultcol = getCol();
						defaultrow = getRow();
						repaint();
					}
					public void focusLost(FocusEvent evt) {
						hasfocus = false;
						repaint();
						}
				}
			);

			addKeyListener(
				new KeyAdapter() {
					public void keyPressed(KeyEvent e) {
						int k = e.getKeyCode();
						doKeyPress(k, getRow(), getCol());
					}
				}
			);	
		}

		public void paint(Graphics g) {
			super.paint(g);

			if (lock) {
				setBackground(celllockcolor);
			} else if (hasfocus) {
				setBackground(cellhicolor);
			} else if (keyletter) {
				setBackground(celllockcolor);
			} else {
				setBackground(cellbgcolor);
			}

			//center and paint letter
			if (letter.length()>0) {
				Graphics2D g2d = (Graphics2D)g;
				FontMetrics fontMetrics = g2d.getFontMetrics();
				int width = fontMetrics.stringWidth(letter);
				int height = fontMetrics.getHeight();
				int ascent = fontMetrics.getAscent();
				int xpos = (cellsize-width)/2 ;
				int ypos = ascent +(cellsize-height)/2;
				setForeground(fontcolor);
				g.drawString(letter, xpos, ypos);
			}
		} 

		public void setLetter(String s) {
			letter = s.toUpperCase();
			repaint();
		}

		public void setLock(boolean lock) {
			this.lock = lock;
			repaint();
		}

		public void setKeyLetter(boolean state) {
			keyletter = state;
			repaint();
		}

		public void setLetter(char c) {
			setLetter(String.valueOf(c));
		}

		public String getLetter() {
			return letter;
		}

		public boolean isLocked() {
			return lock;
		}

		public int getRow() {
			return row;	
		}

		public int getCol() {
			return col;	
		}

	}
	//----END INNER CLASS----

}
