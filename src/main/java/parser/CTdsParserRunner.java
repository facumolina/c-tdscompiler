import java_cup.runtime.*;
import java.io.*;

/**
 * This class allow to run the CTdsParser alone.
 * @author Facundo Molina
 */
public class CTdsParserRunner {
	
	/* 
 	 * Main method for run the parser with an input file 
 	 */
 	public static void main(String[] argv) {
 		try {
 			CTdsParser parser = new CTdsParser(new CTdsScanner(new FileReader(argv[0])));
 			Program program = (Program)parser.parse().value;
 			System.out.println("Correct input");
 		} catch (Exception e) {
 			System.out.println("Incorrect input");
 		}
 	}

}