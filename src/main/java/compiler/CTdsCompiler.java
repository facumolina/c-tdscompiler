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
	private static int labelIndex;

	/* 
 	 * Main method for run the compiler with an input file 
 	 */
 	public static void main(String[] argv) {
 		
 		try {

 			errors = new LinkedList<String>();
 			iCodeStatements = new LinkedList<IntermediateCodeStatement>();
 			labelIndex = -1;

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
 	 * Generate a unique label
 	 */
 	private static String generateLabel() {
 		labelIndex += 1;
 		return "label"+labelIndex;
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

				// Generate the assembler code for each intermediate code statement
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
 				return generateCodeForIntegerArithmeticalOperation((ThreeAddressStatement)stmt,"addl");
 			case AND:
 				return generateCodeForLogicalOperation((ThreeAddressStatement)stmt,"andl");
 			case ASSIGN:
 				return generateCodeForAssign((TwoAddressStatement)stmt);
 			case CALL:
 				return generateCodeForCall(stmt);
 			case DIVI:
 				return generateCodeForIntegerArithmeticalOperation((ThreeAddressStatement)stmt,"idivl");
 			case EQ:
 				return generateCodeForRelationalOperation((ThreeAddressStatement)stmt,"je");
 			case GREAT:
 				return generateCodeForRelationalOperation((ThreeAddressStatement)stmt,"jg");
 			case GREATEQ:
 				return generateCodeForRelationalOperation((ThreeAddressStatement)stmt,
 					"jge");
 			case INITML: 
 				return generateCodeForInitMl((OneAddressStatement)stmt);
 			case JUMP:
 				return generateCodeForJump((OneAddressStatement)stmt);
 			case JUMPF:
 				return generateCodeForJumpF((OneAddressStatement)stmt);
 			case LABEL:
 				return generateCodeForLabel((OneAddressStatement)stmt);
 			case LESS:
 				return generateCodeForRelationalOperation((ThreeAddressStatement)stmt,"jl");
 			case LESSEQ:
 				return generateCodeForRelationalOperation((ThreeAddressStatement)stmt,"jle");
 			case MOD:
 				return generateCodeForIntegerArithmeticalOperation((ThreeAddressStatement)stmt,"mod");
 			case MULTI:
 				return generateCodeForIntegerArithmeticalOperation((ThreeAddressStatement)stmt,"imull");
 			case NEQ:
 				return generateCodeForRelationalOperation((ThreeAddressStatement)stmt,"jne");
 			case NOT:
 				return generateCodeForNot((TwoAddressStatement)stmt);
 			case OR:
 				return generateCodeForLogicalOperation((ThreeAddressStatement)stmt,"orl");
 			case PUSH:
 				return generateCodeForPush((OneAddressStatement)stmt);
 			case RESERVE:
 				return generateCodeForReserve((OneAddressStatement)stmt);
 			case RET:
 				return generateCodeForRet(stmt);
 			case SUBI:
 				return generateCodeForIntegerArithmeticalOperation(stmt,"subl");
 			default: return "\t"+"def\n";
 		}

 	}

 	/**
 	 * Generate the assembler code for the statement with instruction ASSIGN
 	 */
 	private static String generateCodeForAssign(TwoAddressStatement stmt) {
 		Expression expression = stmt.getExpression();
 		String movl = "";
 		if (expression instanceof Literal) {
 			movl = "\t"+"movl ";
 			// The expression is a literal
 			if (expression instanceof IntLiteral) {
 				// The expression is an int literal
 				IntLiteral intLiteral = (IntLiteral)expression;
 				movl += "$"+ intLiteral.getIntegerValue()+", ";
 			} else if (expression instanceof BooleanLiteral) {
 				// The expression is a boolean literal
 				BooleanLiteral boolLiteral = (BooleanLiteral)expression;
 				if (boolLiteral.getBooleanValue()) {
 					// Is true
 					movl += "$1, ";
 				} else {
 					// Is false
 					movl += "$0, ";
 				}
 			} else {
 				// The expression is a float literal
 			}	
 		} else {
 			// The expression value is stored in a location
 			Location loc = (Location)expression;
 			if (loc instanceof VarLocation) {
 				// The location is a var location
 				movl = "\t"+"movl "+loc.getOffset()+"(%ebp), %ebx"+"\n";
 				movl += "\t"+"movl %ebx, ";
 			} else {
 				// The location is a var array location
 				VarArrayLocation arrayLocation = (VarArrayLocation)loc;
 				Expression index = arrayLocation.getExpression();
 				String movIndex = "\t"+"movl ";
 				if (index instanceof Literal) {
 					// The index is a literal
 					movIndex += "$"+((IntLiteral)index).getIntegerValue()+", %ecx"+"\n";
 				} else {
 					// The index is stored in a location
 					movIndex += ((Location)index).getOffset()+"(%ebp), %ecx"+"\n";
 				}
 				movl = movIndex;
 				Integer arraySize = arrayLocation.getDeclaration().getCapacity();
 				Integer correctBase = -1*(loc.getOffset()-(4*(arraySize-1)));
 				movl += "\t"+"movl "+correctBase+"(%ebp,%ecx,4), %ebx"+"\n";
 				movl += "\t"+"movl %ebx, ";
 			}	
 			
 		}
 		Location result = (Location)stmt.getResult();
 		if (result instanceof VarLocation) {
 			// The result is a var location
 			movl += result.getOffset()+"(%ebp)"+"\n";
 			return movl;
 		} else {
 			// The result is a var array location
 			VarArrayLocation arrayLocation = (VarArrayLocation)result;
 			Expression index = arrayLocation.getExpression();
 			String movIndex = "\t"+"movl ";
 			if (index instanceof Literal) {
 				// The index is a literal
 				movIndex += "$"+((IntLiteral)index).getIntegerValue()+", %ecx"+"\n";
 			} else {
 				// The index is stored in a location
 				movIndex += ((Location)index).getOffset()+"(%ebp), %ecx"+"\n";
 			}
 			Integer arraySize = arrayLocation.getDeclaration().getCapacity();
 			Integer correctBase = -1*(result.getOffset()-(4*(arraySize-1)));
 			movl += correctBase+"(%ebp,%ecx,4)"+"\n";
 			return movIndex+movl; 
 		}
 	}

 	/**
 	 * Generate the assembler code for the statement with instruction CALL 
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
 	 * Generate the assembler code for a statement with an instruction for a logical
 	 * operation (AND, OR) 
 	 */
 	private static String generateCodeForLogicalOperation(ThreeAddressStatement stmt,String instructionName) {
 		Expression expression1 = stmt.getExpressionOne();
 		Expression expression2 = stmt.getExpressionTwo();

 		String stringExpr1;
 		String stringExpr2;
 		BooleanLiteral boolLiteral;
 		if (expression1 instanceof Literal) {
 			// The expression one is a literal
 			boolLiteral = (BooleanLiteral)expression1;
 			if (boolLiteral.getBooleanValue()) {
 				stringExpr1 = "$1";
 			} else {
 				stringExpr1 = "$0,";
 			}
 			if (expression2 instanceof Literal) {
 				// The expression two is a literal
 				boolLiteral = (BooleanLiteral)expression2;
 				if (boolLiteral.getBooleanValue()) {
 					stringExpr2 = "$1";
 				} else {
 					stringExpr2 = "$0";
 				}
 	
 			} else {
 				// The expression two is not a literal
 				Location location2 = (Location)expression2;
 				stringExpr2 = location2.getOffset()+"(%ebp)";
 			}
 		} else {
 			// The expression one is not a literal
 			Location location1 = (Location)expression1;
 			stringExpr1 = location1.getOffset()+"(%ebp)"; 
 			if (expression2 instanceof Literal) {
 				// The expression two is a literal
 				boolLiteral = (BooleanLiteral)expression2;
 				if (boolLiteral.getBooleanValue()) {
 					stringExpr2 = "$1";
 				} else {
 					stringExpr2 = "$0";
 				} 
 			} else {
 				// The expression two is not a literal
 				Location location2 = (Location)expression2;
 				stringExpr2 = location2.getOffset()+"(%ebp)";
 			}
 		}
 		Location result = (Location)stmt.getResult();

 		String mov1 = "\t"+"movl "+stringExpr1+", %ebx"+"\n";
 		String ins = "\t"+instructionName+" "+stringExpr2+", %ebx"+"\n";
 		String mov2 = "\t"+"movl %ebx, "+result.getOffset()+"(%ebp)"+"\n";
 		return mov1+ins+mov2;
 	}

 	/**
 	 * Generate the assembler code for a statement with the instruction NOT
 	 */
 	private static String generateCodeForNot(TwoAddressStatement stmt) {
		Expression expr = stmt.getExpression();
 		String stringExpr;
 		if (expr instanceof Literal) {
 			// The expression is a literal
 			BooleanLiteral boolLiteral = (BooleanLiteral)expr;
 			if (boolLiteral.getBooleanValue()) {
 				stringExpr = "$1";
 			} else {
 				stringExpr = "$0";
 			}
 		} else {
 			// The expression two is not a literal
 			Location location = (Location)expr;
 			stringExpr = location.getOffset()+"(%ebp)";
 		}
 		Location result = (Location)stmt.getResult();
 		String mov1 = "\t"+"movl "+stringExpr+", %ebx"+"\n";
 		String not = "\t"+"notl %ebx"+"\n";
 		String shr = "\t"+"shr $1, %ebx"+"\n";
 		String jcLabelName = generateLabel();
 		String endLabelName = generateLabel();
 		String jc = "\t"+"jc "+jcLabelName+"\n";
 		String mov2 = "\t"+"movl $0, %ebx"+"\n";
 		String jmp = "\t"+"jmp "+endLabelName+"\n";
 		String label = jcLabelName+":"+"\n";
 		String mov3 = "\t"+"movl $1, %ebx"+"\n";
 		String labelEnd = endLabelName+":"+"\n";
 		String mov4 = "\t"+"movl %ebx, "+result.getOffset()+"(%ebp)"+"\n";
 		return mov1+not+shr+jc+mov2+jmp+label+mov3+labelEnd+mov4;	
 	}

 	/**
 	 * Generate the assembler code for a statement with an instruction for a relational
 	 * opearation (EQ, LESS, LESSEQ, GREAT, GREATEQ )
 	 */
 	private static String generateCodeForRelationalOperation(ThreeAddressStatement stmt,String instructionName) {
 		Expression expression1 = stmt.getExpressionOne();
 		Expression expression2 = stmt.getExpressionTwo();

 		String mov1 = "\t"+"movl ";
 		String cmp = "\t"+"cmp ";
 		
 		if (expression1 instanceof Literal) {
 			// The expression one is a literal
 			mov1 += "$"+expression1.toString()+", %ebx"+"\n";
 			if (expression2 instanceof Literal) {
 				// The expression two is a literal
 				cmp += "$"+expression2.toString()+", %ebx"+"\n";
 			} else {
 				// The expression two is not a literal
 				Location location2 = (Location)expression2;
 				cmp += location2.getOffset()+"(%ebp), %ebx"+"\n";
 			}
 		} else {
 			// The expression one is not a literal
 			Location location1 = (Location)expression1;
 			mov1 += location1.getOffset()+"(%ebp), %ebx"+"\n"; 
 			if (expression2 instanceof Literal) {
 				// The expression two is a literal
 				cmp += "$"+expression2.toString()+", %ebx"+"\n"; 
 			} else {
 				// The expression two is not a literal
 				Location location2 = (Location)expression2;
 				cmp += location2.getOffset()+"(%ebp), %ebx"+"\n";
 			}
 		}
 		Location result = (Location)stmt.getResult();

 		String jgeLabelName = generateLabel();
 		String endLabelName = generateLabel();

		String jge = "\t"+instructionName+" "+jgeLabelName+"\n";
		String movFalse = "\t"+"movl $0, "+result.getOffset()+"(%ebp)"+"\n";
		String jend = "\t"+"jmp "+endLabelName+"\n";
		String jgeLabel = jgeLabelName+":\n";
		String movTrue = "\t"+"movl $1, "+result.getOffset()+"(%ebp)"+"\n";
 		String endLabel = endLabelName+":\n";	

 		return mov1+cmp+jge+movFalse+jend+jgeLabel+movTrue+endLabel;
 	}

 	/**
 	 * Generate the assembler code for the statement with instruction INITML
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
 	 * Generate the assembler code for the statement with instruction JUMP
 	 */
 	private static String generateCodeForJump(OneAddressStatement stmt) {
 		Label toJump = stmt.getLabelToJump();
 		String jmp = "\t"+"jmp "+toJump.toString()+"\n";
 		return jmp;
 	}

 	/**
 	 * Generate the assembler code for the statement with instruction JUMPF
 	 */
 	private static String generateCodeForJumpF(OneAddressStatement stmt) {
 		Location loc = (Location)stmt.getExpression();
 		Label toJump = stmt.getLabelToJump();

 		String cmp = "\t"+"cmp $0, "+loc.getOffset()+"(%ebp)"+"\n";
 		String je = "\t"+"je "+toJump.toString()+"\n";

 		return cmp + je;
 	}

 	/**
 	 * Generate the assembler code for the statement with instruction LABEL
 	 */
 	private static String generateCodeForLabel(OneAddressStatement stmt) {
 		Label toJump = stmt.getLabelToJump();
 		String label = toJump.toString()+":\n";
 		return label;
 	}

 	/**
 	 * Generate the assembler code for the statement with instruction PUSH 
 	 */
 	private static String generateCodeForPush(OneAddressStatement stmt) {
 		String push = "\t"+"pushl ";
 		Expression expression = stmt.getExpression();
 		if (expression instanceof Literal) {
 			// The expression is a literal
 			push += "$"+expression.toString()+"\n";
 			return push;
 		} else {
 			// The expression is stored in a location
 			Location loc = (Location)expression;
 			if (loc instanceof VarLocation) {
 				// The location is a var location
				push += loc.getOffset()+"(%ebp)"+"\n";
				return push;
 			} else {
 				// The location is a var array location 
 				VarArrayLocation arrayLocation = (VarArrayLocation)loc;
 				Expression index = arrayLocation.getExpression();
 				String movIndex = "\t"+"movl ";
 				if (index instanceof Literal) {
 					// The index is a literal
 					movIndex += "$"+((IntLiteral)index).getIntegerValue()+", %ecx"+"\n";
 				} else {
 					// The index is stored in a location
 					movIndex += ((Location)index).getOffset()+"(%ebp), %ecx"+"\n";
 				}
 				Integer arraySize = arrayLocation.getDeclaration().getCapacity();
 				Integer correctBase = -1*(loc.getOffset()-(4*(arraySize-1)));
 				push += correctBase+"(%ebp,%ecx,4)"+"\n";
 				return movIndex+push;
 			}
 		}
 	}

 	/**
 	 * Generate the assembler code for the statement with instruction RESERVE
 	 */
 	private static String generateCodeForReserve(OneAddressStatement stmt) {
 		VarLocation varLocation = (VarLocation)((OneAddressStatement)stmt).getExpression();
 		int amount = 4*((IntLiteral)varLocation.getValue()).getIntegerValue();
 		String subl = "\t"+"subl $" + amount +", %esp"+"\n";
 		return subl;
 	}

 	/**
 	 * Generate the assembler code for the statement with instruction RET
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
 	
 	/**
 	 * Generate the assembler code for a statement with an instruction for an arithmetical
 	 * opearation with integers (ADDI,SUBI,IMUL,IDIV)
 	 */
 	private static String generateCodeForIntegerArithmeticalOperation(IntermediateCodeStatement stmt,String instructionName) {
 		if (stmt instanceof ThreeAddressStatement) {
 			ThreeAddressStatement threeStmt = (ThreeAddressStatement)stmt;
 			Expression expression1 = threeStmt.getExpressionOne();
 			Expression expression2 = threeStmt.getExpressionTwo();
 			String stringExpr1 = "";
 			String stringExpr2 = "";

 			if (expression1 instanceof Literal) {
 				// The expression one is a literal
 				stringExpr1 = "$"+expression1.toString();
 				if (expression2 instanceof Literal) {
 					// The expression two is a literal
 					stringExpr2 = "$"+expression2.toString();
 				} else {
 					// The expression two is not a literal
 					Location location2 = (Location)expression2;
 					stringExpr2 = location2.getOffset()+"(%ebp)";
 				}
 		
 			} else {
 				// The expression one is not a literal
 				Location location1 = (Location)expression1;
 				stringExpr1 = location1.getOffset()+"(%ebp)"; 
 				if (expression2 instanceof Literal) {
 					// The expression two is a literal
 					stringExpr2 = "$"+expression2.toString(); 
 				} else {
 					// The expression two is not a literal
 					Location location2 = (Location)expression2;
 					stringExpr2 = location2.getOffset()+"(%ebp)"; 
 				}

 			}
 			String mov1 = "\t"+"movl ";
 			String ins;
 			String mov2 = "\t"+"movl ";
 			Location result = (Location)threeStmt.getResult();
 			if (instructionName=="idivl"||instructionName=="mod") {
 				String clearDiv = "\t"+"movl $0, %edx"+"\n";
 				mov1 += stringExpr1+", %eax"+"\n";
 				String movDiv = "\t"+"movl "+stringExpr2+", %ecx"+"\n";
 				ins = "idivl %ecx \n";
 				if (instructionName=="idivl") {
 					// Is div
 					mov2 += "%eax, "+result.getOffset()+"(%ebp)"+"\n";
 				} else {
 					// Is mod
 					mov2 += "%edx, "+result.getOffset()+"(%ebp)"+"\n";
 				}
 				return clearDiv+mov1+movDiv+ins+mov2;
 			} else {
 				ins = "\t"+instructionName+" ";
 				mov1 += stringExpr1+", %ebx"+"\n";
 				ins += stringExpr2+", %ebx"+"\n";
 				mov2 += "%ebx, "+result.getOffset()+"(%ebp)"+"\n";
 				return mov1+ins+mov2;
 			}
 		} else {
 			TwoAddressStatement twoStmt = (TwoAddressStatement)stmt;
 			Expression expr = twoStmt.getExpression();
 			String stringExpr;
 			if (expr instanceof Literal) {
 				// The expression is a literal
 				stringExpr = "$"+expr.toString();
 			} else {
 				// The expression two is not a literal
 				Location location = (Location)expr;
 				stringExpr = location.getOffset()+"(%ebp)";
 			}
 			Location result = (Location)twoStmt.getResult();
 			String ins = "\t"+instructionName+" "+stringExpr+", "+result.getOffset()+"(%ebp)"+"\n";
 			return ins;	
 		}
 	}

}