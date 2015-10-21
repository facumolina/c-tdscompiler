echo "----------------- Running Tests -----------------"
cd classes
export CLASSPATH=""
for file in `ls ../lib/`; do export CLASSPATH=$CLASSPATH:../lib/$file; done
java -cp $CLASSPATH:. org.junit.runner.JUnitCore CTdsScannerTest
java -cp $CLASSPATH:. org.junit.runner.JUnitCore CTdsParserTest
java -cp $CLASSPATH:. org.junit.runner.JUnitCore CheckDeclarationVisitorTest
java -cp $CLASSPATH:. org.junit.runner.JUnitCore CheckTypeVisitorTest
java -cp $CLASSPATH:. org.junit.runner.JUnitCore InterpreterVisitorTest