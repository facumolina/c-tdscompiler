C-TDS Compiler
============
It's a compiler for a simple programming language.

Stack
=====
C-TDS Compiler will use the following open source tools:

* [Java] - Java programming language.
* [Junit] - Framework to write repeatable tests.
* [JFlex] - Lexical analyzer generator (also known as scanner generator) for Java, written in Java.
* [Cup] - CUP stands for Construction of Useful Parsers and is an LALR parser generator for Java.

Installation
============
 - After cloning the repository, compile the sources ``` ./compile.sh ```

 - The compilation will generate the following classes:
 ```
   	CTdsScanner.java (Representing the lexical analyzer defined in the file src/main/jflex/ctdsscanner.flex)
   	CtdsScannerStandalone.java (Representing the lexical analyzaer for testing purposes)
   	CTdsParser.java (Representing the parser defined in the file src/main/cup/ctdsparser.cup)
   	CTdsSymbol.java (Representing the symbols of the language defined in the same file as the parser)
  ```

  [unrc]: http://dc.exa.unrc.edu.ar/ 
  [Java]:  http://www.java.com/en/
  [Junit]: http://junit.org/
  [JFlex]: http://jflex.de/
  [Cup]: http://www2.cs.tum.edu/projekte/cup/index.php


