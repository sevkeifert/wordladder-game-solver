// list class represents a single word in a tree of word ladders

class Word {

	int id;
	int parentword;
	int [] linkedwords;
	boolean used = false;

	String word;

	Word() {
	}

	Word(int id) {
		this.id = id;
	}

	Word(String word, int [] linkedwords, int id) {
		this.linkedwords = linkedwords;
		this.word = word;
		this.id = id;
	}

	int getId() {
		return id;
	}

	void setId(int id) {
		this.id = id;
	}

	boolean getUsed() {
		return used;
	}

	void setUsed(boolean used) {
		this.used = used;
	}

	String getWord() {
		return word;
	}

	int getNumLinks() {
		return linkedwords.length;
	}

	int getChild(int idx) {
		return linkedwords[idx];
	}

	int getParent() {
		return parentword;
	}

	void setParent(int id) {
		parentword = id;
	}
}
