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
 	public static void generateAssemblerCode() {
 		
 		try {

 			// Output file
			PrintWriter writer = new PrintWriter("../assembler.s", "UTF-8");
			writer.println("\t.text");
			
			for (IntermediateCodeStatement intermediateCodeStmt: iCodeStatements) {

				// Generate the assemble for each intermediate code statement
				writer.print(generateCodeForStatement(intermediateCodeStmt));

			}

			writer.close();


 		} catch (IOException e) {
 			e.printStackTrace();
 		}

 	}

 	/**
 	 * Generate the assembler code for a given IntermediateCodeStatement
 	 */
 	public static String generateCodeForStatement(IntermediateCodeStatement stmt) {
 		
 		IntermediateCodeInstruction instruction = stmt.getInstruction();

 		switch (instruction) {
 			case ADDI:
 				return generateCodeForAddI((ThreeAddressStatement)stmt);
 			case ASSIGN:
 				return generateCodeForAssign((TwoAddressStatement)stmt);
 			case CALL:
 				return generateCodeForCall(stmt);
 			case INITML: 
 				return generateCodeForInitMl((OneAddressStatement)stmt);
 			case PUSH:
 				return generateCodeForPush((OneAddressStatement)stmt);
 			case RESERVE:
 				return generateCodeForReserve((OneAddressStatement)stmt);
 			case RET:
 				return generateCodeForRet(stmt);
 			default: return "\t"+"def\n";
 		}

 	}

 	/**
 	 * Generate the assembler code for the statement with instruction ADDI
 	 */
 	private static String generateCodeForAddI(ThreeAddressStatement stmt) {
 		Expression expression1 = stmt.getExpressionOne();
 		Expression expression2 = stmt.getExpressionTwo();
 		
 		String mov1 = "\t"+"movl ";
 		String addl = "\t"+"addl ";
 		String mov2 = "\t"+"movl ";
 		if (expression1 instanceof Literal) {
 			// The expression one is a literal
 			mov1 += "$"+expression1.toString()+", %ebx"+"\n";
 			if (expression2 instanceof Literal) {
 				// The expression two is a literal
 				addl += "$"+expression2.toString()+", %ebx"+"\n";
 			} else {
 				// The expression two is not a literal
 				Location location2 = (Location)expression2;
 				addl += location2.getOffset()+"(%ebp), %ebx"+"\n";
 			}
 		
 		} else {
 			// The expression one is not a literal
 			Location location1 = (Location)expression1;
 			mov1 += location1.getOffset()+"(%ebp), %ebx"+"\n"; 
 			if (expression2 instanceof Literal) {
 				// The expression two is a literal
 				addl += "$"+expression2.toString()+", %ebx"+"\n"; 
 			} else {
 				// The expression two is not a literal
 				Location location2 = (Location)expression2;
 				addl += location2.getOffset()+"(%ebp), %ebx"+"\n";
 			}

 		}

 		Location result = (Location)stmt.getResult();
 		mov2 += "%ebx, "+result.getOffset()+"(%ebp)"+"\n";

 		return mov1+addl+mov2;
 	}

 	/**
 	 * Generate the assembler code for the statement with instruction assign
 	 */
 	private static String generateCodeForAssign(TwoAddressStatement stmt) {
 		Expression expression = stmt.getExpression();
 		String movl = "\t"+"movl ";
 		if (expression instanceof Literal) {
 			// The expression is a literal
 			if (expression instanceof IntLiteral) {
 				// The expression is an int literal
 				IntLiteral intLiteral = (IntLiteral)expression;
 				movl += "$"+ intLiteral.getIntegerValue()+", ";
 			} else if (expression instanceof BooleanLiteral) {
 				// The expression is a boolean literal
 			} else {
 				// The expression is a float literal
 			}	
 		
 		} else {
 			// The expression value is stored in a location
 			Location loc = (Location)expression;
 			movl += loc.getOffset()+"(%ebp), %ebx"+"\n";
 			movl += "\t"+"movl %ebx, ";
 		}
 		Location result = (Location)stmt.getResult();
 		movl += result.getOffset()+"(%ebp)"+"\n";
 		return movl;
 	}

 	/**
 	 * Generate the assembler code for the statement with instruction call 
 	 */
 	private static String generateCodeForCall(IntermediateCodeStatement stmt) {
 		Location loc;
 		String call;
 		if (stmt instanceof OneAddressStatement) {
 			// The call does not return anything
 			loc = (Location)((OneAddressStatement)stmt).getExpression();
			call = "\tcall "+loc.getId()+"\n";
 			return call;

 		} else {
			// The call stores the result in the eax register
			loc = (Location)((TwoAddressStatement)stmt).getExpression();
 			call = "\tcall "+loc.getId()+"\n";
 			Location result = (Location)((TwoAddressStatement)stmt).getResult();
 			String mov = "\tmovl %eax, "+result.getOffset()+"(%ebp)"+"\n";
 			return call+mov;
 		
 		}
 	}

 	/**
 	 * Generate the assembler code for the statement with instruction initml
 	 */
 	private static String generateCodeForInitMl(OneAddressStatement stmt) {
 		Location location = (Location)stmt.getExpression(); 
 		String global = "\t"+".globl "+location.getId()+"\n";
 		String type = "\t"+".type "+location.getId()+", @function"+"\n";
 		String label = location.getId()+":\n";
 		String push = "\t"+"pushl %ebp"+"\n";
 		String mov = "\t"+"movl %esp, %ebp"+"\n";
 		return global+type+label+push+mov;
 	}

 	/**
 	 * Generate the assembler code for the statement with instruction push 
 	 */
 	private static String generateCodeForPush(OneAddressStatement stmt) {
 		String push = "\t"+"pushl ";
 		Expression expression = stmt.getExpression();
 		if (expression instanceof Literal) {
 			// The expression is a literal
 			push += "$"+expression.toString()+"\n";
 		} else {
 			// The expression is stored in a location
 			Location loc = (Location)expression;
 			push += loc.getOffset()+"(%ebp)"+"\n";
 		}
 		return push;
 	}

 	/**
 	 * Generate the assembler code for the statement with instruction reserve
 	 */
 	private static String generateCodeForReserve(OneAddressStatement stmt) {
 		VarLocation varLocation = (VarLocation)((OneAddressStatement)stmt).getExpression();
 		int amount = 4*((IntLiteral)varLocation.getValue()).getIntegerValue();
 		String subl = "\t"+"subl $" + amount +", %esp"+"\n";
 		return subl;
 	}

 	/**
 	 * Generate the assembler code for the statement with instruction ret
 	 */
 	private static String generateCodeForRet(IntermediateCodeStatement stmt) {
 		String leave = "\t"+"leave"+"\n";
 		String ret = "\t"+"ret"+"\n";
 		if (stmt instanceof OneAddressStatement) {
 			// The return must return a value stored in a location
 			Location loc = (Location)((OneAddressStatement)stmt).getExpression();
 			String movl = "\tmovl "+loc.getOffset()+"(%ebp), %eax"+"\n";
 			return movl+leave+ret;
 		} else {
 			return leave+ret;
 		}
 	}


}