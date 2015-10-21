import java_cup.runtime.*;
import java.io.*;
import java.util.LinkedList;

/**
 * This class represents the compiler.
 * @author Facundo Molina
 */
public class CTdsCompiler {
	
	private static CTdsParser parser;			// Parser
	private static LinkedList<String> errors; 	// Errors
	private static LinkedList<IntermediateCodeStatement> iCodeStatements; // Intermediate code statements
	
	/* 
 	 * Main method for run the compiler with an input file 
 	 */
 	public static void main(String[] argv) {
 		
 		try {

 			errors = new LinkedList<String>();
 			iCodeStatements = new LinkedList<IntermediateCodeStatement>();

 			// Read file
 			parser = new CTdsParser(new CTdsScanner(new FileReader(argv[0])));
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
 				// Only generate the intermedite code if there are no type errors
 				generateIntermediateCode(program);
 				for (IntermediateCodeStatement i : iCodeStatements) {
 					System.out.println(i.toString());
 				}
 			}

 			if (errors.size()==0) {
 				// Only interpret the program if there are no previous errors.
 				System.out.println("Successful compilation");
 				System.out.println("Executing..");
 				interpret(program);
 			}
 			
 			if (errors.size()==0) {
 				System.out.println("Execution completed");
 			} else {
 				for (String error : errors) {
 					System.out.println(error);
 				}
 			}
 		
 		} catch (Exception e) {
 			e.printStackTrace();
 			System.out.println("Compilation failed");
 		}
 		
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
 	
 	/**
 	 * Generate intermediate code
 	 */
 	public static void generateIntermediateCode(Program p) {
 		IntermediateCodeGeneratorVisitor iCGVisitor = new IntermediateCodeGeneratorVisitor();
 		iCGVisitor.visit(p);
 		iCodeStatements = iCGVisitor.getIntermediateCodeList();
 	}

 	/**
 	 * Interpret the program
 	 */
 	public static void interpret(Program p) {
 		InterpreterVisitor interpreterVisitor = new InterpreterVisitor();
 		errors.addAll(interpreterVisitor.visit(p));
 	}

}