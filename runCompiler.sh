#!/bin/bash
echo "---------------------------- Compiling file -------------------------------"
cd classes
export CLASSPATH=""
for file in `ls ../lib/`; do export CLASSPATH=$CLASSPATH:../lib/$file; done

java -cp $CLASSPATH:. CTdsCompiler ../$1

