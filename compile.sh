#!/bin/bash
rm src/main/java/scanner/*.java
rm src/main/java/parser/CTdsParser.java
rm src/main/java/parser/CTdsSymbol.java

echo "----------------- Creating java file from jflex file -----------------"
java -jar lib/jflex-1.6.1.jar src/main/jflex/ctdsscanner.flex -d src/main/java/scanner
java -jar lib/jflex-1.6.1.jar src/main/jflex/ctdsscanner-standalone.flex -d src/main/java/scanner/

echo "------------------ Creating java file from cup file ------------------"
java -jar lib/java-cup-11a.jar -parser CTdsParser -symbols CTdsSymbol -destdir src/main/java/parser/ src/main/cup/ctdsparser.cup 

echo "------------------------ Compiling java files ------------------------"
export CLASSPATH=""


for file in `ls lib/`; do export CLASSPATH=$CLASSPATH:lib/$file; done
mkdir -p classes
javac -cp $CLASSPATH src/main/java/scanner/*.java src/main/java/parser/*.java src/main/java/compiler/*.java src/main/java/interpreter/*.java src/test/java/*.java src/main/java/ast/*.java src/main/java/visitor/*.java src/main/java/intermediatecode/*.java -d classes/

echo "Successful compilation"
