// this class manages the dictionary of words and contains methods:
//	  getRandLadder - get a random word ladder for new game
//    find ladder - find shortest word ladder between two words if it exists

import java.io.*;
import java.net.*;
import java.util.*;


class WordList {

	Random rand;		
	Word[] words;
	int numwords;
	int wordlength = -1;

	WordList(int wordlength) {

		//only load words if needed.  cache last dictionary in memory.
		if (this.wordlength != wordlength) {
			try {
				rand = new Random(new Date().getTime());

				// file io,
				InputStream in = getClass().getResourceAsStream("wordlist/wordlist" + wordlength + ".txt");
				BufferedReader wordlist = new BufferedReader(new InputStreamReader(in));

				//alt file io for applet: url using compressed stream.
				//URL urlFile = new URL(codebase, "wordlist/wordlist" + wordlength + ".zip");
				//ZipInputStream zi = new ZipInputStream(urlFile.openStream()); 
				//zi.getNextEntry();
				//BufferedReader wordlist = new BufferedReader(new InputStreamReader(zi));

				//head of file contains file length
				numwords = Integer.parseInt(wordlist.readLine());
				words = new Word[numwords];
				for (int i=0; i < numwords; i++) {
					String line = wordlist.readLine();
					StringTokenizer parser =  new StringTokenizer(line);
					//get word
					String word = parser.nextToken();
					//get links to all possible ladder words
					int numlinks = Integer.parseInt(parser.nextToken());
					int [] wordlinks = new int[numlinks];
					for (int j=0; j < numlinks; j++) {
						wordlinks[j] = Integer.parseInt(parser.nextToken());
					}
					//save result
					words[i] = new Word(word, wordlinks,i);
				}

				wordlist.close();
				//zi.close();	 // if using zip

				this.wordlength = wordlength;

			} catch(Exception e) {
				System.out.println(e);
			}
		}
	}

	//look through a forest to find a N-length branch
	//randomly pick seed.  build tree.  inspect.
	//if tree not high enough, inspect the tree next to it
	public Word[] getRandLadder(int desiredlen) {
		int wid = rand.nextInt(numwords);
		Word[] wordladder = null;
		int safety = 0;
		while(wordladder == null) {
			wordladder = getRandLadder(wid, desiredlen);		
			//infinite loop + safety
			if (++wid > numwords) wid = 0;
			if (++safety > numwords) return null;
		}
		return wordladder ;
	}

	//note: a recursive function would be much simpler
	//but would create a tree with flagrant left-bias.
	//resulting ladders would not be as short as possible
	//
	//	w is horizontal slice of tree:
	//
	//	w0	    |	
	//	w1	  /\ \
	//	w2	/\  | \
	public Word[] getRandLadder(int wordidx, int desiredlen) {
		resetUsed(wordidx);
		int lvl = 1;
		int numlinks;
		Word [] w = new Word[1];
		w[0] = getWord(wordidx);
		while(w.length > 0) {
			//word can't appear more than once in tree
			for (int i=0; i<w.length; i++) {
				w[i].setUsed(true);
			}
			//count children at next level down
			numlinks = 0;
			for (int i=0; i<w.length; i++) {
				Word wtemp = w[i];
				for (int j=0; j < wtemp.getNumLinks(); j++) {
					Word child = getWord(wtemp.getChild(j));
					if (!child.getUsed()) {
						numlinks++;
					}		
				}				
			}
			//record children at next level down
			Word [] temp = new Word[numlinks];
			int tmpidx = 0;
			for (int i=0; i<w.length; i++) {
				Word wtemp = w[i];
				for (int j=0; j<wtemp.getNumLinks(); j++) {
					Word child = getWord(wtemp.getChild(j));
					if (!child.getUsed()) {
						//build tree
						child.setParent(wtemp.getId());
						temp[tmpidx++] = child;
					}
				}				
			}		

			//process wordlist
			if (lvl==desiredlen) {
				//bingo! trickle up tree and build wordladder.
				Word [] wordladder = new Word[lvl]; 
				//pick random 0>=i>w.length
				int i = rand.nextInt(w.length);
				int idtemp = w[i].getId();
				for (int j=0; j<lvl; j++) {
					Word wtemp = getWord(idtemp);
					wordladder[j] = wtemp; 
					idtemp = wtemp.getParent();
				}
				return wordladder;
			}


			w = temp;		
			lvl++;
		} 
		return null;
	}

	//note: a recursive function would be much simpler
	//but would create a tree with flagrant left-bias.
	//resulting ladders would not be as short as possible
	//
	//	w is horizontal slice of tree:
	//
	//	w0	    |	
	//	w1	  /\ \
	//	w2	/\  | \
	Word[] findLadder(int wordidx, int wordidx2) {
		resetUsed(wordidx);
		int lvl = 1;
		int numlinks;
		Word [] w = new Word[1];
		w[0] = getWord(wordidx);
		while(w.length > 0) {
			//word can't appear more than once in tree
			for (int i=0; i<w.length; i++) {
				w[i].setUsed(true);
			}
			//count children at next level down
			numlinks = 0;
			for (int i=0; i<w.length; i++) {
				Word wtemp = w[i];
				for (int j=0; j < wtemp.getNumLinks(); j++) {
					Word child = getWord(wtemp.getChild(j));
					if (!child.getUsed()) {
						numlinks++;
					}		
				}				
			}
			//record children at next level down
			Word [] temp = new Word[numlinks];
			int tmpidx = 0;
			for (int i=0; i<w.length; i++) {
				Word wtemp = w[i];
				for (int j=0; j<wtemp.getNumLinks(); j++) {
					Word child = getWord(wtemp.getChild(j));
					if (!child.getUsed()) {
						//build tree
						child.setParent(wtemp.getId());
						temp[tmpidx++] = child;
					}
				}				
			}		

			//process wordlist
			for (int i=0; i<w.length; i++) {
				if (w[i].getId()==wordidx2) {
					//bingo! trickle up tree and build wordladder.
					Word [] wordladder = new Word[lvl]; 
					int idtemp = wordidx2;
					for (int j=0; j<lvl; j++) {
						Word wtemp = getWord(idtemp);
						wordladder[j] = wtemp; 
						idtemp = wtemp.getParent();
					}
					return wordladder;
				}
			}

			w = temp;		
			lvl++;
		} 
		return null;
	}

	//lookup id from string
	int getId(String s) {
		s = s.toLowerCase();
		for (int i=0; i<numwords; i++) {
			Word w = getWord(i);
			if (s.equals(w.getWord())) {
				return w.getId();
			}
		}
		return -1;
	}

	//checks whether jump is valid
	boolean checkRung(String s1,String s2) {
		int w1 = getId(s1);
		int w2 = getId(s2);
		return checkRung(w1, w2);
	}

	boolean checkRung(int w1, int w2) {
		if (w1>-1 && w2>-1) {
			Word wtemp = getWord(w1);
			for (int i=0; i<wtemp.getNumLinks(); i++) {
				if (w2 == wtemp.getChild(i))
					return true;
			}
		}
		return false;
	}

	//resets flags needed for traversing trees
	void resetUsed(int [] wordidxs) {
		for (int i=0; i < wordidxs.length; i++) {
			setUsed(wordidxs[i],false);
		}
	}

	void resetUsed(int wordidx) {
		Word w = getWord(wordidx);
		if (w.getUsed()) {
			w.setUsed(false);
			for (int i=0;i<w.getNumLinks();i++) {
				resetUsed(w.getChild(i));
			}
		}
	}

	void resetUsed() {
		for (int i=0; i<numwords; i++) {
			Word w = getWord(i);
			w.setUsed(false);
		}
	}

	//get / set functions below...
	Word getWord(int wordidx) {
		return words[wordidx];	
	}

	int getWordLength() {
		return wordlength;	
	}

	void setUsed(int wordidx, boolean used) {
		getWord(wordidx).setUsed(used);
	}

	boolean getUsed(int wordidx) {
		return getWord(wordidx).getUsed();
	}
}

