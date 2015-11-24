import java_cup.runtime.*;
import java.io.*;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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
 			FileReader file = new FileReader(argv[0]);
 			parser = new CTdsParser(new CTdsScanner(file));
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

 				// Generate assembler code
 				generateAssemblerCode();
 			}

 			if (errors.size()==0) {
 				// Compile the assembler
 				compileAssemblerCode();
 				System.out.println("Compilation completed");
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
 	 * Generate assembler code
 	 */
 	private static void generateAssemblerCode() {
 		AssemblerCodeGenerator.generateAssemblerCode(iCodeStatements);
 	}

 	/**
 	 * Compile the generated assembler code
  	 */
 	private static void compileAssemblerCode() {
 		StringBuffer output = new StringBuffer();
 		Process p;
 		try {
 			String[] envs;	
 			p = Runtime.getRuntime().exec("gcc assembler.s -o exec",null,new File(System.getProperty("user.dir")));
 			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 		System.out.println(output.toString());
 	}

}