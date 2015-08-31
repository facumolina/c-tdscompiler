#!/bin/bash
echo "---------------------------- Creating java file from jflex file -------------------------------"
java -jar lib/jflex-1.6.1.jar src/main/jflex/ctdsscanner.flex -d src/main/java/

echo "---------------------------------- Compiling java files ---------------------------------------"
export CLASSPATH=""
for file in `ls lib/`; do export CLASSPATH=$CLASSPATH:lib/$file; done
javac -cp $CLASSPATH src/main/java/*.java src/test/java/*.java -d classes/

echo "Successful compilation"
