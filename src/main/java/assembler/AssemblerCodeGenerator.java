import java.io.*;
import java.util.List;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This class provides some methods for generate the assembler code for the CTds language
 * @author Facundo Molina
 */
public class AssemblerCodeGenerator {

	private static int labelIndex;					// Index labels counter
	private static LinkedList<String> floatLabels; 	// Float labels to add at the end
	
	/**
 	 * Generate assembler code from a given list of intermediate code statements
 	 */
 	public static void generateAssemblerCode(List<IntermediateCodeStatement> iCodeStatements) {
 		
 		labelIndex = 0;
 		floatLabels = new LinkedList<String>();

 		try {

 			// Output file
			PrintWriter writer = new PrintWriter("../assembler.s", "UTF-8");
			writer.println("\t.text");
			
			for (IntermediateCodeStatement intermediateCodeStmt: iCodeStatements) {

				// Generate the assembler code for each intermediate code statement
				writer.print(generateCodeForStatement(intermediateCodeStmt));

			}

			
			for (String floatLbl : floatLabels) {	
				
				// Write each float label at the end of the file
				writer.print(floatLbl);
			
			}

			writer.close();


 		} catch (IOException e) {
 			e.printStackTrace();
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
 	 * Generate the assembler code for a given IntermediateCodeStatement
 	 */
 	private static String generateCodeForStatement(IntermediateCodeStatement stmt) {
 		
 		IntermediateCodeInstruction instruction = stmt.getInstruction();

 		switch (instruction) {
 			case ADDI:
 				return generateCodeForIntegerArithmeticalOperation((ThreeAddressStatement)stmt,"addl");
 			case ADDF:
				return generateCodeForFloatArithmeticalOperation((ThreeAddressStatement)stmt,"fadds");
 			case AND:
 				return generateCodeForLogicalOperation((ThreeAddressStatement)stmt,"andl");
 			case ASSIGN:
 				return generateCodeForAssign((TwoAddressStatement)stmt);
 			case CALL:
 				return generateCodeForCall(stmt);
 			case DIVI:
 				return generateCodeForIntegerArithmeticalOperation((ThreeAddressStatement)stmt,"idivl");
 			case DIVF:
 				return generateCodeForFloatArithmeticalOperation((ThreeAddressStatement)stmt,"fdivs");
 			case EQ:
 				return generateCodeForRelationalOperation((ThreeAddressStatement)stmt,"je");
 			case GLOBAL:
 				return generateCodeForGlobal((OneAddressStatement)stmt);
 			case GREAT:
 				return generateCodeForRelationalOperation((ThreeAddressStatement)stmt,"jg");
 			case GREATEQ:
 				return generateCodeForRelationalOperation((ThreeAddressStatement)stmt,"jge");
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
 			case MULTF:
 				return generateCodeForFloatArithmeticalOperation((ThreeAddressStatement)stmt,"fmuls");
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
 			case SUBF:
 				return generateCodeForFloatArithmeticalOperation(stmt,"fsubs");
 			default: return "\t"+"def\n";
 		}

 	}

 	/**
 	 * Generate the assembler code for the statement with instruction ASSIGN
 	 */
 	private static String generateCodeForAssign(TwoAddressStatement stmt) {
 		Expression expression = stmt.getExpression();
 		String movl = "";
 		boolean isFloat = false;
 		String floatLabel= "";
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
 				isFloat = true;
 				FloatLiteral floatLiteral = (FloatLiteral)expression;
 				Integer integer = Float.floatToRawIntBits(floatLiteral.getFloatValue());
 				String labelName = "."+generateLabel();
 				floatLabel = labelName+":\n";
 				floatLabel += "\t"+".long "+integer+"\n";
 				floatLabels.add(floatLabel);
 				floatLabel = "\t"+"movl "+labelName+", %ecx"+"\n";
 				movl += "%ecx, ";
 			}
 			
 		} else {
 			// The expression value is stored in a location
 			Location loc = (Location)expression;
 			if (loc instanceof VarLocation) {
 				// The location is a var location
 				movl = generateCodeForBinaryInstructionToRegister("movl",(VarLocation)loc,"ebx");
 			} else {
 				// The location is a var array location
 				movl = generateCodeForBinaryInstructionToRegister("movl",(VarArrayLocation)loc,"ebx","ebx");
 			}	
 			movl += "\t"+"movl %ebx, ";
 		}
 		Location result = (Location)stmt.getResult();
 		if (result instanceof VarLocation) {
 			// The result is a var location
 			DeclarationIdentifier declIdentifier = result.getDeclaration();
 			if (declIdentifier.isGlobal()) {
 				movl += declIdentifier.getId()+"\n";
 				return movl;
 			} else {
 				movl += result.getOffset()+"(%ebp)"+"\n";
 				if (!isFloat) {
					return movl;
 				} else {
 					return floatLabel+movl;
 				}
 			}
 		} else {
 			// The result is a var array location
 			VarArrayLocation arrayLocation = (VarArrayLocation)result;
 			Expression index = arrayLocation.getExpression();
 			String movIndex = "";
 			if (index instanceof Literal) {
 				// The index is a literal
 				movIndex = generateCodeForBinaryInstructionToRegister("movl",(Literal)index,"ecx");
 			} else {
 				// The index is stored in a location
 				movIndex = generateCodeForBinaryInstructionToRegister("movl",(VarLocation)index,"ecx"); 
 			}
 			DeclarationIdentifier declIdentifier = arrayLocation.getDeclaration();
 			if (declIdentifier.isGlobal()) {
 				String mul = "\t"+"imull $4, %ecx"+"\n";
 				movl += declIdentifier.getId()+"+0(%ecx)"+"\n";
 				return movIndex+mul+movl;
 			} else {
				Integer arraySize = arrayLocation.getDeclaration().getCapacity();
 				Integer correctBase = result.getOffset()-(4*(arraySize-1));
 				movl += correctBase+"(%ebp,%ecx,4)"+"\n";
 				return movIndex+movl; 
 			}
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
			call = "\t"+"call "+loc.getId()+"\n";
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
 	 * Generate the assebler code for global declarations
 	 */
 	private static String generateCodeForGlobal(OneAddressStatement stmt) {
 		Location loc = (Location)stmt.getExpression();
 		DeclarationIdentifier declIdentifier = loc.getDeclaration();
 		String comm;
 		if (declIdentifier.isArrayDeclarationId()) {
 			comm = "\t"+".comm "+declIdentifier.getId()+","+ (declIdentifier.getCapacity()*4) +",32"+"\n";
 		} else {
			comm = "\t"+".comm "+declIdentifier.getId()+",4,4"+"\n";
 		}
 		return comm;
 	}

 	/**
 	 * Generate the assembler code for a statement with an instruction for a logical
 	 * operation (AND, OR) 
 	 */
 	private static String generateCodeForLogicalOperation(ThreeAddressStatement stmt,String instructionName) {
 		Expression expression1 = stmt.getExpressionOne();
 		Expression expression2 = stmt.getExpressionTwo();

 		// Move the expression one to the ebx register
 		String mov1 = generateCodeForBinaryInstructionToRegister("movl",expression1,"ebx","ebx");

 		// Apply the instruction with the expression two to the ebx register in which
 		String ins = generateCodeForBinaryInstructionToRegister(instructionName,expression2,"edx","ebx");

 		Location result = (Location)stmt.getResult();
 		String mov2 = "\t"+"movl %ebx, "+result.getOffset()+"(%ebp)"+"\n";
 		return mov1+ins+mov2;
 	}

 	/**
 	 * Generate the assembler code for a statement with the instruction NOT
 	 */
 	private static String generateCodeForNot(TwoAddressStatement stmt) {
		Expression expr = stmt.getExpression();

 		// Move the expression to the ebx register
 		String mov1 = generateCodeForBinaryInstructionToRegister("movl",expr,"ebx","ebx");
 		
 		Location result = (Location)stmt.getResult();
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

 		// Move the expression one to the ebx register
 		String mov1 = generateCodeForBinaryInstructionToRegister("movl",expression1,"edx","ebx");
 		
 		// Apply a cmp with the expression two to the ebx register
 		String cmp = generateCodeForBinaryInstructionToRegister("cmp",expression2,"edx","ebx");

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
 		Expression expr = stmt.getExpression();
 		String cmp;
 		if (expr instanceof Literal) {
 			// The expression is a literal
 			BooleanLiteral boolLiteral = (BooleanLiteral)expr;
 			if (boolLiteral.getBooleanValue()) {
 				cmp = "\t"+"cmp $0, $1"+"\n";
 			} else {
 				cmp = "\t"+"cmp $0, $0"+"\n";
 			}	
 		} else {
 			// The expression is in a location
 			Location loc = (Location)expr;
 			DeclarationIdentifier declIdentifier = loc.getDeclaration();
 			
 			if (loc instanceof VarLocation) {
 				if (declIdentifier.isGlobal()) {
 					cmp = "\t"+"cmp $0, "+declIdentifier.getId()+"\n";
 				} else {
 					cmp = "\t"+"cmp $0, "+loc.getOffset()+"(%ebp)"+"\n";	
 				}
 			} else {
 				// The expression location is a var array location
 				VarArrayLocation arrayLocation = (VarArrayLocation)loc;
 				Expression index = arrayLocation.getExpression();
 				String movIndex = "";
 				if (index instanceof Literal) {
 					// The index is a literal
 					movIndex = generateCodeForBinaryInstructionToRegister("movl",(Literal)index,"edx");
 				} else {
 					// The index is stored in a location
 					movIndex = generateCodeForBinaryInstructionToRegister("movl",(VarLocation)index,"edx");
 				}
 				cmp = movIndex;
 				if (declIdentifier.isGlobal()) {
 					cmp += "\t"+"imull $4, %edx"+"\n";
 					cmp += "\t"+"cmp $0, "+declIdentifier.getId()+"+0(%edx)"+"\n";
 				} else {
					Integer arraySize = declIdentifier.getCapacity();
 					Integer correctBase = declIdentifier.getOffset()-(4*(arraySize-1));
 					cmp += "\t"+"cmp $0, "+correctBase+"(%ebp,%edx,4)"+"\n";

 				}
 			}	
 		}
 		Label toJump = stmt.getLabelToJump();
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
 		Expression expression = stmt.getExpression();
 		// Push the expression
 		String push = generateCodeForUnaryInstruction("pushl",expression,"ecx");
 		return push;
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
 		String movl;
 		if (stmt instanceof OneAddressStatement) {
 			// The return must return a value stored in a location
 			Expression expr = ((OneAddressStatement)stmt).getExpression();
 			// Move the expression to the eax register
 			movl = generateCodeForBinaryInstructionToRegister("movl",expr,"ecx","eax");
 			return movl+leave+ret;
 		} else {
 			return leave+ret;
 		}
 	}
 	
 	/**
 	 * Generate the assembler code for a statement with an instruction for an arithmetical
 	 * opearation with integers (ADDI,SUBI,MULTI,DIVI)
 	 */
 	private static String generateCodeForIntegerArithmeticalOperation(IntermediateCodeStatement stmt,String instructionName) {
 		if (stmt instanceof ThreeAddressStatement) {
 			ThreeAddressStatement threeStmt = (ThreeAddressStatement)stmt;
 			Expression expression1 = threeStmt.getExpressionOne();
 			Expression expression2 = threeStmt.getExpressionTwo();
 			String stringExpr2 = "";
 			String mov1;
 			String registerToMove1 = "";
 			if (needsMakeDivision(instructionName)) {
 				registerToMove1="eax";
 			} else {
 				registerToMove1="ebx";
 			}
 			String ins = "\t"+instructionName+" ";
 			String mov2 = "\t"+"movl ";
 			boolean considerIns = false;
 			if (expression1 instanceof Literal) {
 				// The expression one is a literal, so move it to a register
 				mov1 = generateCodeForBinaryInstructionToRegister("movl",(Literal)expression1,registerToMove1);
 				if (expression2 instanceof Literal) {
 					// The expression two is a literal
 					stringExpr2 = "$"+expression2.toString();
 				} else {
 					// The expression two is not a literal
 					Location location2 = (Location)expression2;
 					DeclarationIdentifier locDeclaration2 = location2.getDeclaration();
 					if (location2 instanceof VarLocation) {
 						// The expression location is a var location
 						if (locDeclaration2.isGlobal()) {
 							stringExpr2 = locDeclaration2.getId();
 						} else {
							stringExpr2 = location2.getOffset()+"(%ebp)"; 
 						}
 					} else {
 						// The expression location is a var array location
 						VarArrayLocation arrayLocation2 = (VarArrayLocation)location2;
 						Expression index2 = arrayLocation2.getExpression();
 						String movIndex2= "";
 						if (index2 instanceof Literal) {
 							// The index is a literal
 							movIndex2 = generateCodeForBinaryInstructionToRegister("movl",(Literal)index2,"edx");
 						} else {
 							// The index is stored in a location
 							movIndex2 = generateCodeForBinaryInstructionToRegister("movl",(VarLocation)index2,"edx");
 						}
 						ins = movIndex2;
 						if (locDeclaration2.isGlobal()) {
 							ins += "\t"+"imull $4, %edx"+"\n";
 							ins += "\t"+instructionName+" ";
 							stringExpr2 = locDeclaration2.getId()+"+0(%edx)";
 						} else {
							Integer arraySize2 = locDeclaration2.getCapacity();
 							Integer correctBase2 = locDeclaration2.getOffset()-(4*(arraySize2-1));
 							ins += "\t"+instructionName+" ";
 							stringExpr2 = correctBase2+"(%ebp,%edx,4)";
 						}
 					}
 				}
 		
 			} else {
 				// The expression one is not a literal
 				Location location1 = (Location)expression1;
 				DeclarationIdentifier locDeclaration1 = location1.getDeclaration();
 				if (location1 instanceof VarLocation) {
 					// The expression location is a var location
 					mov1 = generateCodeForBinaryInstructionToRegister("movl",(VarLocation)location1,registerToMove1);
 				} else {
 					// The expression location is a var array location
 					mov1 = generateCodeForBinaryInstructionToRegister("movl",(VarArrayLocation)location1,"ebx",registerToMove1);
 				}
 				if (expression2 instanceof Literal) {
 					// The expression two is a literal
 					stringExpr2 = "$"+expression2.toString(); 
 				} else {
 					// The expression two is not a literal
 					Location location2 = (Location)expression2;
 					DeclarationIdentifier locDeclaration2 = location2.getDeclaration();
 					if (location2 instanceof VarLocation) {
 						// The expression location is a var location
 						if (locDeclaration2.isGlobal()) {
 							stringExpr2 = locDeclaration2.getId();
 						} else {
							stringExpr2 = location2.getOffset()+"(%ebp)"; 
 						}
 					} else {
 						// The expression location is a var array location
 						VarArrayLocation arrayLocation2 = (VarArrayLocation)location2;
 						Expression index2 = arrayLocation2.getExpression();
 						String movIndex2= "";
 						if (index2 instanceof Literal) {
 							// The index is a literal
 							movIndex2 = generateCodeForBinaryInstructionToRegister("movl",(Literal)index2,"edx");
 						} else {
 							// The index is stored in a location
 							movIndex2 = generateCodeForBinaryInstructionToRegister("movl",(VarLocation)index2,"edx");
 						}
 						ins = movIndex2;
 						if (locDeclaration2.isGlobal()) {
 							ins += "\t"+"imull $4, %edx"+"\n";
 							if (instructionName!="idivl"&&instructionName!="mod") {
 								ins += "\t"+instructionName+" ";
 							} else {
 								considerIns = true;
 							}
 							stringExpr2 = locDeclaration2.getId()+"+0(%edx)";
 						} else {
							Integer arraySize2 = locDeclaration2.getCapacity();
 							Integer correctBase2 = locDeclaration2.getOffset()-(4*(arraySize2-1));
 							if (instructionName!="idivl"&&instructionName!="mod") {
 								ins += "\t"+instructionName+" ";
 							} else {
 								considerIns = true;
 							}
 							stringExpr2 = correctBase2+"(%ebp,%edx,4)";
 						}
 					}
 				}
 			}
 			Location result = (Location)threeStmt.getResult();
 			if (instructionName=="idivl"||instructionName=="mod") {
 				String prevIns = "";
 				if ((instructionName=="idivl")&&considerIns) {
 					prevIns = ins;
 				}
 				String clearDiv = "\t"+"movl $0, %edx"+"\n";
 				String movDiv = "\t"+"movl "+stringExpr2+", %ecx"+"\n";
 				ins = "\t"+"idivl %ecx \n";
 				if (instructionName=="idivl") {
 					// Is div
 					mov2 += "%eax, "+result.getOffset()+"(%ebp)"+"\n";
 				} else {
 					// Is mod
 					mov2 += "%edx, "+result.getOffset()+"(%ebp)"+"\n";
 				}
 				return mov1+prevIns+movDiv+clearDiv+ins+mov2;
 			} else {
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

 	/**
 	 * Returns true if the given instruction needs to perform a div
 	 */
 	private static boolean needsMakeDivision(String instructionName) {
 		return (instructionName=="idivl"||instructionName=="mod"); 
 	}

 	/**
 	 * Generate the assembler code for a statement with an instruction for an arithmetical
 	 * opearation with floats (ADDF,SUBF,MULTF,DIVF)
 	 */
 	private static String generateCodeForFloatArithmeticalOperation(IntermediateCodeStatement stmt,String instructionName) {
 		String flds = "";
 		String ins = "";
 		String fstps = "";
 		if (stmt instanceof ThreeAddressStatement) {
 			ThreeAddressStatement threeStmt = (ThreeAddressStatement)stmt;
 			Expression expression1 = threeStmt.getExpressionOne();
 			Expression expression2 = threeStmt.getExpressionTwo();
 			
 			// Load the expression one using the ebx register if it is necessary
 			flds = generateCodeForUnaryInstruction("flds",expression1,"ebx");

 			// Apply the instruction with the expression two using the edx register if it is necessary
 			ins = generateCodeForUnaryInstruction(instructionName,expression2,"edx");

 			Location result = (Location)threeStmt.getResult();
 			fstps = generateCodeForUnaryInstruction("fstps",(VarLocation)result);
 			return flds+ins+fstps;
 			
 		} else {
 			TwoAddressStatement twoStmt = (TwoAddressStatement)stmt;
 			Expression expr = twoStmt.getExpression();
 			String stringExpr;
 			if (expr instanceof Literal) {
 				// The expression is a literal
 				flds = generateCodeForUnaryInstruction("flds",(Literal)expr);
 			} else {
 				// The expression two is not a literal
 				if (expr instanceof VarLocation) {
 					// The expression is a var location
 					flds = generateCodeForUnaryInstruction(instructionName,(VarLocation)expr);
 				} else {
 					// The expression is a var array location
 					Location location = (Location)expr;
 					stringExpr = location.getOffset()+"(%ebp)";
 					ins = "\t"+instructionName+" "+stringExpr+"\n";
 				}
 				
 			}

 			Location result = (Location)twoStmt.getResult();
 			fstps = generateCodeForUnaryInstruction("fstps",(VarLocation)result);
 			return flds+ins+fstps;	
 		}
 	}

 	/**
 	 * Generate code for apply a unary instrction to an expression
 	 */
 	private static String generateCodeForUnaryInstruction(String instructionName, Expression operand, String auxiliarRegister) {
 		String ins;
 		if (operand instanceof Literal) {
 			// The expression is a literal
 			ins = generateCodeForUnaryInstruction(instructionName,(Literal)operand);
 		} else {
 			// The expression is stored in a location
 			Location loc = (Location)operand;
 			if (loc instanceof VarLocation) {
 				// The location is a var location
 				ins = generateCodeForUnaryInstruction(instructionName,(VarLocation)loc);
 			} else {
 				// The location is a var array location 
 				ins = generateCodeForUnaryInstruction(instructionName,(VarArrayLocation)loc,auxiliarRegister);
 			}
 		}
 		return ins;
 	}

 	/**
 	 * Generate code for apply a unary instruction to a given literal
 	 */
 	private static String generateCodeForUnaryInstruction(String instructionName, Literal literal) {
 		String ins = "\t"+instructionName+" ";
 		if (literal instanceof IntLiteral) {
 			// The literal is an int literal
 			IntLiteral intLiteral = (IntLiteral)literal;
 			ins += "$"+ intLiteral.getIntegerValue()+"\n";
 		} else if (literal instanceof BooleanLiteral) {
 			// The literal is a boolean literal
 			BooleanLiteral boolLiteral = (BooleanLiteral)literal;
 			if (boolLiteral.getBooleanValue()) {
 				// Is true
 				ins += "$1"+"\n";
 			} else {
 				// Is false
 				ins += "$0"+"\n";
 			}
 		} else {
 			// The literal is a float literal
 			FloatLiteral floatLiteral = (FloatLiteral)literal;
 			Integer integer = Float.floatToRawIntBits(floatLiteral.getFloatValue());
 			String labelName = "."+generateLabel();
 			String floatLabel = labelName+":\n";
 			floatLabel += "\t"+".long "+integer+"\n";
 			floatLabels.add(floatLabel);
 			ins += labelName+"\n";
 		}
 		return ins;
 	}

 	/**
 	 * Generate code for apply a unary instruction to a given var location
 	 */
 	private static String generateCodeForUnaryInstruction(String instructionName, VarLocation loc) {
 		String ins = "\t"+instructionName+" ";
 		DeclarationIdentifier declIdentifier = loc.getDeclaration();
 		if (declIdentifier.isGlobal()) {
 			// The declaration is global		
 			ins += declIdentifier.getId()+" "+"\n";
 		} else {
 			// The declaration is local
			ins += loc.getOffset()+"(%ebp)"+"\n";
 		}
		return ins;
 	}

 	/**
 	 * Generate the code for apply a unary instruction to a given var array location
 	 */
 	private static String generateCodeForUnaryInstruction(String instructionName, VarArrayLocation loc,String auxiliarRegister) {
 		DeclarationIdentifier declIdentifier = loc.getDeclaration();
 		Expression index = loc.getExpression();
 		String ins;
 		String movIndex = "";
 		if (index instanceof Literal) {
 			// The index is a literal
 			movIndex = generateCodeForBinaryInstructionToRegister("movl",(Literal)index,auxiliarRegister);
 		} else {
 			// The index is stored in a location
 			movIndex = generateCodeForBinaryInstructionToRegister("movl",(VarLocation)index,auxiliarRegister);
 		}
 		if (declIdentifier.isGlobal()) {
 			String mult = "\t"+"imull $4, %"+auxiliarRegister+"\n";
 			ins = "\t"+instructionName+" "+declIdentifier.getId()+"+0(%"+auxiliarRegister+")"+"\n";
 			return movIndex+mult+ins;
 		} else {
 			Integer arraySize = declIdentifier.getCapacity();
 			Integer correctBase = loc.getOffset()-(4*(arraySize-1));
 			ins = "\t"+instructionName+" "+correctBase+"(%ebp,%"+auxiliarRegister+",4)"+"\n";
 			return movIndex+ins;
 		}
 	}

 	/**
 	 * Generate code for apply a unary instruction to a given register
 	 */
 	private static String generateCodeForUnaryInstruction(String instructionName, String registerName) {
 		return "\t"+instructionName+" %"+registerName+"\n";
 	}

 	/**
 	 * Generate code for apply a binary instruction with an expression as operand to a 
 	 * given register 
 	 */
 	private static String generateCodeForBinaryInstructionToRegister(String instructionName,Expression operand, String auxiliarRegister, String registerToApply) {
 		String ins;
 		if (operand instanceof Literal) {
 			// The expression is a literal
 			ins = generateCodeForBinaryInstructionToRegister(instructionName,(Literal)operand,registerToApply);
 		} else {
 			// The expression is not a literal
 			Location loc = (Location)operand;
 			if (loc instanceof VarLocation) {
 				// The location is a var location
 				ins = generateCodeForBinaryInstructionToRegister(instructionName,(VarLocation)loc,registerToApply);
 			} else {
 				// The location is a var array location
 				ins = generateCodeForBinaryInstructionToRegister(instructionName,(VarArrayLocation)loc,auxiliarRegister,registerToApply);
 			}
 		}
 		return ins;
 	}

 	/**
 	 * Generate code for apply a binary instruction with a literal as operand
 	 * to a given register
 	 */
 	private static String generateCodeForBinaryInstructionToRegister(String instructionName, Literal operand, String registerName) {
 		String ins;
		if (operand instanceof IntLiteral) {
			// The literal is an int literal
 			ins = "\t"+instructionName+" $"+((IntLiteral)operand).getIntegerValue()+", %"+registerName+"\n";
 		} else if (operand instanceof BooleanLiteral){
 			// The literal is a boolean literal
 			BooleanLiteral boolLiteral = (BooleanLiteral)operand;
 			if (boolLiteral.getBooleanValue()) {
 				ins = "\t"+instructionName+" $1, %"+registerName+"\n";
 			} else {
 				ins = "\t"+instructionName+" $0, %"+registerName+"\n";
 			}
 		} else {
			// The literal is a float literal
 			FloatLiteral floatLiteral = (FloatLiteral)operand;
 			Integer integer = Float.floatToRawIntBits(floatLiteral.getFloatValue());
 			String labelName = "."+generateLabel();
 			String floatLabel = labelName+":\n";
 			floatLabel += "\t"+".long "+integer+"\n";
 			floatLabels.add(floatLabel);
 			ins = "\t"+instructionName+" "+labelName+", %"+registerName+"\n";
 		}
 		return ins;
 	}

 	/**
 	 * Generate code for apply a binary instruction with a var location as operand
 	 * to a given register
 	 */
 	private static String generateCodeForBinaryInstructionToRegister(String instructionName, VarLocation operand, String registerName) {
 		DeclarationIdentifier locDeclaration = operand.getDeclaration();
 		String ins = "\t"+instructionName+" ";
 		if (locDeclaration.isGlobal()) {
 			ins += locDeclaration.getId()+", %"+registerName+"\n";
 		} else {
			ins += operand.getOffset()+"(%ebp), %"+registerName+"\n";
 		}
 		return ins;
 	}

 	/**
 	 * Generate code for apply a binary instruction with a var array location as operand
 	 * to a given register
 	 */
 	private static String generateCodeForBinaryInstructionToRegister(String instructionName, VarArrayLocation operand, String registerForIndex,String registerName) {
 		DeclarationIdentifier locDeclaration = operand.getDeclaration();
 		Expression index = operand.getExpression();
 		String movIndex;
 		String ins;
 		if (index instanceof Literal) {
 			// The index is a literal
 			movIndex = generateCodeForBinaryInstructionToRegister("movl",(Literal)index,registerForIndex);
 		} else {
 			// The index is stored in a location
 			movIndex = generateCodeForBinaryInstructionToRegister("movl",(VarLocation)index,registerForIndex);
 		}
 		if (locDeclaration.isGlobal()) {
 			// The declaration is global
 			ins = "\t"+"imull $4, %edx"+"\n";
 			ins += "\t"+instructionName+" "+locDeclaration.getId()+"+0(%"+registerForIndex+"), %"+registerName+"\n";
 		} else {
 			// The declaration is local
			Integer arraySize = locDeclaration.getCapacity();
 			Integer correctBase = locDeclaration.getOffset()-(4*(arraySize-1));
 			ins = "\t"+instructionName+" "+correctBase+"(%ebp,%"+registerForIndex+",4), %"+registerName+"\n";
 		}
 		return movIndex+ins;
 	}

}