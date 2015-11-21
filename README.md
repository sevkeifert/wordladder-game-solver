# wordladder-game-solver
This is very old word ladder puzzle game (and puzzle solver) I wrote in Java.
A word ladder is a string of words that change one letter at a time. For
example: "cat", "cot", "dot", "dog" is a word ladder that connects "cat" and
"dog".  I made a couple quick tweaks to convert it from an applet to a Swing
application. 

Kevin Seifert - 2002 GPL

-------------------------------------------------------------------------------
SCREENSHOTS
-------------------------------------------------------------------------------

![Alt text](https://github.com/sevkeifert/wordladder-game-solver/blob/master/screenshot.png?raw=true "WordLadder Game")

![Alt text](https://github.com/sevkeifert/wordladder-game-solver/blob/master/screenshot2.png?raw=true "WordLadder Solver")


-------------------------------------------------------------------------------
REQUIREMENTS
-------------------------------------------------------------------------------

	Java 1.2+
	Linux, Mac, Windows, or anything that runs Java

-------------------------------------------------------------------------------
TO COMPILE
-------------------------------------------------------------------------------

There are not many class files in the game/solver.
I included a basic bash script for compiling the code on Linux:

	./build.sh

-------------------------------------------------------------------------------
TO RUN WORDLADDER GAME
-------------------------------------------------------------------------------

run:

	java -jar wordladders.jar

-------------------------------------------------------------------------------
TO RUN WORDLADDER PUZZLE SOLVER
-------------------------------------------------------------------------------

run:

	java  -cp wordladders.jar  WordLadderSolver 

-------------------------------------------------------------------------------
ABOUT THE GAME (AND DESIGN)
-------------------------------------------------------------------------------

This is a very old puzzle game I wrote; it's one of my first Java projects.
The code could  be cleaner, and I didn't spend a lot of time on the graphical
interface. 

I always liked word puzzles, and thought it would be a fun game project (and
excuse to learn a bit more about Java).  As of 2006, my game/solver was number
one in Google's search listing, if you searched for "word ladder" :-) As a
result, I occasionally got email question about how I designed the word ladder
game, and how I optimized the speed.

The game/solver was initially written as an Applet, and was designed to work
over a network connection with very limited bandwidth, using only limited CPU,
with no service on the backend.  I made a couple tweaks and converted it to a
standalone Swing Application.  


Below is a rough explanation of how the game is designed:

To write a word puzzle game, first you need a "good" dictionary of words.
First I looked online, and downloaded some free word lists.  I looked at a lot
of sources, though ended up combining lists from (I think): 

	http://www.puzzlers.org 
	http://wordlist.sourceforge.net/

I thought this would be the simplest part of the problem, it actually turned
out to be the most difficult. It's a fuzzy and vague problem with no definite
solution.  What is a "good" list?  It's a list that has well-known common
words, and a lot of them.  But what is "well-known"?  Obscure words, archaic
words, typos, or abbreviations make no sense in a word ladder puzzle.
Eventually I was forced to combine a few smaller list that had more common
words.  If the solver has flaws, it is mostly because I never found a good
balance between the quality and quantity of words.  It's a fuzzy problem with a
lot of grey area...  and I've never wanted to manually edit a list of 100k's of
words.  The overall quality of the solver will be entirely dependent on the
dictionary of words it's using.

Warning: I've never tried to sanitize the existing word lists.  :-)  Also, if I
looked again (over a decade later), such a list might exist at this point.


Before I could write the game, first I had to write a "solver."   Since
there's more than one possible solution to a given puzzle.  The concept of a
word ladder solver is simple, but the challenge is getting it to run fast.  If
thought isn't put into the core algorithm, the code will probably be incredibly
slow.  Scanning for word ladders is naturally a CPU-intensive operation. Also,
downloading several megabytes of dictionary data will be very slow.  Keep in
mind, this was written when dialup modems were still fairly common. :-)

For optimization, the key design principles here are: 

A. 90% of the words in a dictionary probably will not occur in any word ladder.
Most of the data can simply be ignored.  

B. Most of the time scanning for a solution can be shortened by *precomputing*
as much as possible in advance.  There's only a finite number of words, and
hence only a finite number of word ladders that can be formed from them.

C. The data can be translated into a format that represents the space of all
possible solutions, relative to a given starting point. 

-- 

So, I wrote a small perl script (included as buildwordlist.pl) that broke the
huge word lists into smaller groups by word length (3 letter words, 4 letter
words, etc).  Then linked each word to all possible word ladder kin within this
dictionary.  The flat file data format looks like this:

	number_of_words_in_file
	word_1   number_of_kin	kin_position	kin_position	...
	word_2   number_of_kin	kin_position	kin_position	...
	word_3   number_of_kin	kin_position	kin_position	...
	...

For example:

	3688
	abaca   2       1       2
	abaci   2       0       2
	aback   3       0       1       50
	abase   3       4       5       11
	abash   2       3       166
	abate   2       3       38
	abend   1       77
	abets   1       12
	abide   2       9       130
	abode   3       8       10      89
	above   1       9
	abuse   2       3       82
	abuts   1       7
	abysm   1       14
	...

This is a pretty simple format (and of course could be optimized further).  The
first line is the number of words in the file. For the next lines, the first
field is "the word", followed by the number of kin, and then row numbers of
other words that are only different by one letter (line numbers).

For the Applet, I also zipped the word lists (split by word size) and only
download them as needed.  This reduced the dictionary size by 99% and
also almost completely solved the problem.  Otherwise, the Applet would have
been incredible slow and unusable as it churned through every possible
combination of hundreds of thousands of words.  What would have required a
several megabyte download of dictionary data was reduced to a compressed packet
under 50k.

Some of these optimizations are lost when converting to an  application, since
all wordlists are included in one jar file, rather than being downloaded as
URL's as needed.  I left the initial URL code in, just commented out.

Then, with this data, writing a "solver" is trivial. Given two words, I
start from the first and build (or crawl) a tree of all possible word ladders
starting from that node.  This branches out until all possibilities are found ,
or until a branch contains the end word (a solution).  In this case, I just
bubble back up the tree.  

On thing here that might be tricky is pay attention to how the tree is built.
A recursive function would be much simpler but would create a tree with
flagrant left-bias.  resulting ladders would not be as short as possible.


        (words can only be used once)    	
		level is a horizontal slice of tree:
	
		level 0	       |	
		level 1	     /\ \
		level 2	   /\  | \


After the solver was built, it was possible to write the game.   The game
logic is trivial.  Just find a random word ladder, paint the board, and check
all proposed solutions dynamically (since there is more than one  possible
solution for a puzzle). 

