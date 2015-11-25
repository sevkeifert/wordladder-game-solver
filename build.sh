
# compile
mkdir bin/
javac src/*.java -d bin/
cp -r src/* bin/

# package
cd bin/
jar cvfm ../wordladders.jar META-INF/MANIFEST.MF *.gif *.wav *.class wordlist/ 

cd ..
# run solver
java -cp wordladders.jar  WordLadderSolver &
# run game
java -jar wordladders.jar &



