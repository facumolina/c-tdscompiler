#!/bin/bash
echo "---------------------------- Creating java file from jflex file -------------------------------"
java -jar lib/jflex-1.6.1.jar src/main/jflex/ctdsscanner.flex -d src/main/java/

echo "---------------------------- Creating java file from cup file -------------------------------"
java -jar lib/java-cup-11a.jar -parser CTdsParser -destdir src/main/java/ src/main/cup/ctdsparser.cup 

echo "---------------------------------- Compiling java files ---------------------------------------"
export CLASSPATH=""
for file in `ls lib/`; do export CLASSPATH=$CLASSPATH:lib/$file; done
javac -cp $CLASSPATH src/main/java/*.java src/test/java/*.java -d classes/

echo "Successful compilation"
