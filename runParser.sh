#!/bin/bash
echo "----------------- Executing Parser -----------------"
cd classes
export CLASSPATH=""
for file in `ls ../lib/`; do export CLASSPATH=$CLASSPATH:../lib/$file; done

java -cp $CLASSPATH:. CTdsParserRunner ../$1