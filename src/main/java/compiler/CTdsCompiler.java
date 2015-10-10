import java_cup.runtime.*;
import java.io.*;
import java.util.LinkedList;

/**
 * This class represents the compiler.
 */
public class CTdsCompiler {
	
	private static CTdsParser parser;	// Parser
	private static LinkedList<String> errors; // Errors
	
	/* 
 	 * Main method for run the compiler with an input file 
 	 */
 	public static void main(String[] argv) {
 		
 		try {

 			// Read file
 			parser = new CTdsParser(new CTdsScanner(new FileReader(argv[0])));
 			errors = new LinkedList<String>();
 			Program program = (Program)parser.parse().value;

 			// Check Main Method
 			checkMains(program);
 			
 			// Check declarations
 			checkDeclarations(program);
 			
 			if (errors.size()==0) {
 				// Only check types if there are no previous declarations errors.
 				checkTypes(program); 			
 			}
 			
 			
 			if (errors.size()==0) {
 				System.out.println("Successful compilation");
 			} else {
 				for (String error : errors) {
 					System.out.println(error);
 				}
 			}
 		} catch (Exception e) {
 			System.out.println("Compilation failed");
 		}
 		
 	}

 	/**
 	 * Print program
 	 */
 	public static void printProgram(Program p) {
 		//PrintVisitor<String> printVisitor = new PrintVisitor<String>();
 		PrintVisitor printVisitor = new PrintVisitor();
 		System.out.println(printVisitor.visit(p));
 	}

 	/**
 	 * Check that for each class the amount of mains methods is one.
 	 */
 	public static void checkMains(Program p) {
 		CheckMainVisitor mainVisitor = new CheckMainVisitor();
 		Integer amountOfMains = mainVisitor.visit(p);
 		if (amountOfMains != 1) {
 			errors.add("Error: There must be only one main method without arguments");
 		}
 	}

 	/**
 	 * Check declarations and fill some type informations.
  	 */
 	public static void checkDeclarations(Program p) {
 		CheckDeclarationVisitor declarationVisitor = new CheckDeclarationVisitor();
 		errors.addAll(declarationVisitor.visit(p));
 	}

 	/**
 	 * Check types
 	 */
 	public static void checkTypes(Program p) {
 		CheckTypeVisitor checkTypeVisitor = new CheckTypeVisitor();
 		errors.addAll(checkTypeVisitor.visit(p));
 	}
 	
}