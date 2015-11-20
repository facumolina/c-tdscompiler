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
 			DeclarationIdentifier locDeclaration = loc.getDeclaration();
 			if (loc instanceof VarLocation) {
 				// The location is a var location
 				if (locDeclaration.isGlobal()) {
 					movl = "\t"+"movl "+locDeclaration.getId()+", %ebx"+"\n";
 				} else {
					movl = "\t"+"movl "+loc.getOffset()+"(%ebp), %ebx"+"\n";
 				}
 				movl += "\t"+"movl %ebx, ";
 			} else {
 				// The location is a var array location
 				VarArrayLocation arrayLocation = (VarArrayLocation)loc;
 				Expression index = arrayLocation.getExpression();
 				String movIndex = "\t"+"movl ";
 				if (index instanceof Literal) {
 					// The index is a literal
 					movIndex += "$"+((IntLiteral)index).getIntegerValue()+", %ebx"+"\n";
 				} else {
 					// The index is stored in a location
 					movIndex += ((Location)index).getOffset()+"(%ebp), %ebx"+"\n";
 				}
 				movl = movIndex;
 				if (locDeclaration.isGlobal()) {
 					movl += "\t"+"imull $4, %ebx"+"\n";
 					movl += "\t"+"movl "+loc.getId()+"+0(%ebx), %ebx"+"\n";
 					movl += "\t"+"movl %ebx, ";
 				} else {
					Integer arraySize = arrayLocation.getDeclaration().getCapacity();
 					Integer correctBase = loc.getOffset()-(4*(arraySize-1));
 					movl += "\t"+"movl "+correctBase+"(%ebp,%ebx,4), %ebx"+"\n";
 					movl += "\t"+"movl %ebx, ";
 				}
 			}	
 			
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
 			String movIndex = "\t"+"movl ";
 			if (index instanceof Literal) {
 				// The index is a literal
 				movIndex += "$"+((IntLiteral)index).getIntegerValue()+", %ecx"+"\n";
 			} else {
 				// The index is stored in a location
 				movIndex += ((Location)index).getOffset()+"(%ebp), %ecx"+"\n";
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
 		String mov1;
 		String ins = "\t"+instructionName+" ";
 		String mov2 = "\t"+"movl ";
 		String stringExpr1;
 		String stringExpr2;
 		if (expression1 instanceof Literal) {
 			// The expression one is a literal, so move it to the ebx register
 			mov1 = generateCodeForMoveLiteralToRegister((Literal)expression1,"ebx");
 			if (expression2 instanceof Literal) {
 				// The expression two is a literal
 				BooleanLiteral boolLiteral = (BooleanLiteral)expression2;
 				if (boolLiteral.getBooleanValue()) {
 					stringExpr2 = "$1";
 				} else {
 					stringExpr2 = "$0";
 				}
 	
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
 					String movIndex2 = "\t"+"movl ";
 					if (index2 instanceof Literal) {
 						// The index is a literal
 						movIndex2 += "$"+((IntLiteral)index2).getIntegerValue()+", %edx"+"\n";
 					} else {
 						// The index is stored in a location
 						movIndex2 += ((Location)index2).getOffset()+"(%ebp), %edx"+"\n";
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
 				if (locDeclaration1.isGlobal()) {
 					stringExpr1 = locDeclaration1.getId();
 				} else {
 					stringExpr1 = location1.getOffset()+"(%ebp)";
 				}
 				mov1 = "\t"+"movl "+stringExpr1+", %ebx"+"\n";
 			} else {
				// The expression location is a var array location
 				VarArrayLocation arrayLocation1 = (VarArrayLocation)location1;
 				Expression index1 = arrayLocation1.getExpression();
 				String movIndex1 = "\t"+"movl ";
 				if (index1 instanceof Literal) {
 					// The index is a literal
 					movIndex1 += "$"+((IntLiteral)index1).getIntegerValue()+", %ebx"+"\n";
 				} else {
 					// The index is stored in a location
 					movIndex1 += ((Location)index1).getOffset()+"(%ebp), %ebx"+"\n";
 				}
 				mov1 = movIndex1;
 				if (locDeclaration1.isGlobal()) {
 					mov1 += "\t"+"imull $4, %ebx"+"\n";
 					mov1 += "\t"+"movl "+locDeclaration1.getId()+"+0(%ebx), %ebx"+"\n";
 					mov1 += "\t"+"movl ";
 					stringExpr1 = "%ebx";
 				} else {
					Integer arraySize = locDeclaration1.getCapacity();
 					Integer correctBase = locDeclaration1.getOffset()-(4*(arraySize-1));
 					mov1 = "\t"+"movl "+correctBase+"(%ebp,%ebx,4), %ebx"+"\n";
 				}
 			} 
 			if (expression2 instanceof Literal) {
 				// The expression two is a literal
 				BooleanLiteral boolLiteral = (BooleanLiteral)expression2;
 				if (boolLiteral.getBooleanValue()) {
 					stringExpr2 = "$1";
 				} else {
 					stringExpr2 = "$0";
 				}
 	
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
 					String movIndex2 = "\t"+"movl ";
 					if (index2 instanceof Literal) {
 						// The index is a literal
 						movIndex2 += "$"+((IntLiteral)index2).getIntegerValue()+", %edx"+"\n";
 					} else {
 						// The index is stored in a location
 						movIndex2 += ((Location)index2).getOffset()+"(%ebp), %edx"+"\n";
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
 		}
 		Location result = (Location)stmt.getResult();
 		ins += stringExpr2+", %ebx"+"\n";
 		mov2 += "%ebx, "+result.getOffset()+"(%ebp)"+"\n";
 		return mov1+ins+mov2;
 	}

 	/**
 	 * Generate code for move a given var location to a register
 	 */
 	private static String generateCodeForMoveVarLocationToRegister(VarLocation loc, String registerName) {
 		return "";
 	}

 	/**
 	 * Generate code for move a given var array location to a register
 	 */
 	 private static String generateCodeForMoveVarArrayLocationToRegister(VarArrayLocation arrayLoc, String registerName) {
 	 	return "";
 	 }

 	/**
 	 * Generate the assembler code for a statement with the instruction NOT
 	 */
 	private static String generateCodeForNot(TwoAddressStatement stmt) {
		Expression expr = stmt.getExpression();
 		String mov1;
 		if (expr instanceof Literal) {
 			// The expression is a literal, so move it to the ebx register
 			mov1 = generateCodeForMoveLiteralToRegister((Literal)expr,"ebx");
 		} else {
 			// The expression two is not a literal
 			Location location = (Location)expr;
 			String stringExpr = location.getOffset()+"(%ebp)";
 			mov1 = "\t"+"movl "+stringExpr+", %ebx"+"\n";
 		}
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

 		String mov1 = "\t"+"movl ";
 		String cmp = "\t"+"cmp ";
 		if (expression1 instanceof Literal) {
 			// The expression one is a literal, so move it to the ebx register
 			mov1 = generateCodeForMoveLiteralToRegister((Literal)expression1,"ebx");
 			if (expression2 instanceof Literal) {
 				// The expression two is a literal
 				if (expression2 instanceof IntLiteral) {
 					// The expression is an int literal
 					IntLiteral intLiteral2 = (IntLiteral)expression2;
 					cmp += "$"+intLiteral2.getIntegerValue()+", %ebx"+"\n";
 				} else if (expression2 instanceof BooleanLiteral) {
 					// The expression is a boolean literal
 					BooleanLiteral boolLiteral2 = (BooleanLiteral)expression2;
 					if (boolLiteral2.getBooleanValue()) {
 						// Is true
 						cmp += "$1, %ebx"+"\n";
 					} else {
 						// Is false
 						cmp += "$0, %ebx"+"\n";
 					}
 				} else {
 					// The expression is a float literal
 					FloatLiteral floatLiteral2 = (FloatLiteral)expression2;
 					Integer integer2 = Float.floatToRawIntBits(floatLiteral2.getFloatValue());
 					String labelName2 = "."+generateLabel();
 					String floatLabel2 = labelName2+":\n";
 					floatLabel2 += "\t"+".long "+integer2+"\n";
 					floatLabels.add(floatLabel2);
 					cmp += labelName2+", %ebx"+"\n";
 				}
 			} else {
 				// The expression two is not a literal
 				Location location2 = (Location)expression2;
 				DeclarationIdentifier locDeclaration2 = location2.getDeclaration();
 				if (location2 instanceof VarLocation) {
 					// The expression location is a var location
 					if (locDeclaration2.isGlobal()) {
 						cmp += locDeclaration2.getId()+", %ebx"+"\n";
 					} else {
						cmp += location2.getOffset()+"(%ebp), %ebx"+"\n";
 					}
 				} else {
 					// The expression location is a var array location
 					VarArrayLocation arrayLocation2 = (VarArrayLocation)location2;
 					Expression index2 = arrayLocation2.getExpression();
 					String movIndex2 = "\t"+"movl ";
 					if (index2 instanceof Literal) {
 						// The index is a literal
 						movIndex2 += "$"+((IntLiteral)index2).getIntegerValue()+", %edx"+"\n";
 					} else {
 						// The index is stored in a location
 						movIndex2 += ((Location)index2).getOffset()+"(%ebp), %edx"+"\n";
 					}
 					cmp = movIndex2;
 					if (locDeclaration2.isGlobal()) {
 						cmp += "\t"+"imull $4, %edx"+"\n";
 						cmp += "\t"+"cmp "+locDeclaration2.getId()+"+0(%edx), %ebx"+"\n";
 					} else {
						Integer arraySize2 = locDeclaration2.getCapacity();
 						Integer correctBase2 = locDeclaration2.getOffset()-(4*(arraySize2-1));
 						cmp += "\t"+"cmp "+correctBase2+"(%ebp,%edx,4), %ebx"+"\n";
 					}
 				}
 			}
 		} else {
 			// The expression one is not a literal
			Location location1 = (Location)expression1;
 			DeclarationIdentifier locDeclaration1 = location1.getDeclaration();
 			if (location1 instanceof VarLocation) {
 				// The expression location is a var location
 				if (locDeclaration1.isGlobal()) {
 					mov1 += locDeclaration1.getId()+", %ebx"+"\n";
 				} else {
 					mov1 += location1.getOffset()+"(%ebp), %ebx"+"\n";
				}
 			} else {
 				// The expression location is a var array location
 				VarArrayLocation arrayLocation1 = (VarArrayLocation)location1;
 				Expression index1 = arrayLocation1.getExpression();
 				String movIndex1 = "\t"+"movl ";
 				if (index1 instanceof Literal) {
 					// The index is a literal
 					movIndex1 += "$"+((IntLiteral)index1).getIntegerValue()+", %edx"+"\n";
 				} else {
 					// The index is stored in a location
 					movIndex1 += ((Location)index1).getOffset()+"(%ebp), %edx"+"\n";
 				}
 				mov1 = movIndex1;
 				if (locDeclaration1.isGlobal()) {
 					mov1 += "\t"+"imull $4, %edx"+"\n";
 					mov1 += "\t"+"movl "+locDeclaration1.getId()+"+0(%edx), %ebx"+"\n";
 				} else {
					Integer arraySize1 = locDeclaration1.getCapacity();
 					Integer correctBase1 = locDeclaration1.getOffset()-(4*(arraySize1-1));
 					mov1 += "\t"+"movl "+correctBase1+"(%ebp,%edx,4), %ebx";
 				}
 			} 
 			if (expression2 instanceof Literal) {
 				// The expression two is a literal
 				if (expression2 instanceof IntLiteral) {
 					// The expression is an int literal
 					IntLiteral intLiteral2 = (IntLiteral)expression2;
 					cmp += "$"+intLiteral2.getIntegerValue()+", %ebx"+"\n";
 				} else if (expression2 instanceof BooleanLiteral) {
 					// The expression is a boolean literal
 					BooleanLiteral boolLiteral2 = (BooleanLiteral)expression2;
 					if (boolLiteral2.getBooleanValue()) {
 						// Is true
 						cmp += "$1, %ebx"+"\n";
 					} else {
 						// Is false
 						cmp += "$0, %ebx"+"\n";
 					}
 				} else {
 					// The expression is a float literal
 					FloatLiteral floatLiteral2 = (FloatLiteral)expression2;
 					Integer integer2 = Float.floatToRawIntBits(floatLiteral2.getFloatValue());
 					String labelName2 = "."+generateLabel();
 					String floatLabel2 = labelName2+":\n";
 					floatLabel2 += "\t"+".long "+integer2+"\n";
 					floatLabels.add(floatLabel2);
 					cmp += labelName2+", %ebx"+"\n";
 				}
 			} else {
 				// The expression two is not a literal
 				Location location2 = (Location)expression2;
 				DeclarationIdentifier locDeclaration2 = location2.getDeclaration();
 				if (location2 instanceof VarLocation) {
 					// The expression location is a var location
 					if (locDeclaration2.isGlobal()) {
 						cmp += locDeclaration2.getId()+", %ebx"+"\n";
 					} else {
						cmp += location2.getOffset()+"(%ebp), %ebx"+"\n";
 					}
 				} else {
 					// The expression location is a var array location
 					VarArrayLocation arrayLocation2 = (VarArrayLocation)location2;
 					Expression index2 = arrayLocation2.getExpression();
 					String movIndex2 = "\t"+"movl ";
 					if (index2 instanceof Literal) {
 						// The index is a literal
 						movIndex2 += "$"+((IntLiteral)index2).getIntegerValue()+", %edx"+"\n";
 					} else {
 						// The index is stored in a location
 						movIndex2 += ((Location)index2).getOffset()+"(%ebp), %edx"+"\n";
 					}
 					cmp = movIndex2;
 					if (locDeclaration2.isGlobal()) {
 						cmp += "\t"+"imull $4, %edx"+"\n";
 						cmp += "\t"+"cmp "+locDeclaration2.getId()+"+0(%edx), %ebx"+"\n";
 					} else {
						Integer arraySize2 = locDeclaration2.getCapacity();
 						Integer correctBase2 = locDeclaration2.getOffset()-(4*(arraySize2-1));
 						cmp += "\t"+"cmp "+correctBase2+"(%ebp,%edx,4), %ebx"+"\n";
 					}
 				}
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
 				String movIndex = "\t"+"movl ";
 				if (index instanceof Literal) {
 					// The index is a literal
 					movIndex += "$"+((IntLiteral)index).getIntegerValue()+", %edx"+"\n";
 				} else {
 					// The index is stored in a location
 					movIndex += ((Location)index).getOffset()+"(%ebp), %edx"+"\n";
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
 		String push = "\t"+"pushl ";
 		Expression expression = stmt.getExpression();
 		if (expression instanceof Literal) {
 			// The expression is a literal
 			if (expression instanceof IntLiteral) {
 				// The expression is an int literal
 				IntLiteral intLiteral = (IntLiteral)expression;
 				push += "$"+ intLiteral.getIntegerValue()+"\n";
 			} else if (expression instanceof BooleanLiteral) {
 				// The expression is a boolean literal
 				BooleanLiteral boolLiteral = (BooleanLiteral)expression;
 				if (boolLiteral.getBooleanValue()) {
 					// Is true
 					push += "$1"+"\n";
 				} else {
 					// Is false
 					push += "$0"+"\n";
 				}
 			} else {
 				// The expression is a float literal
 				FloatLiteral floatLiteral = (FloatLiteral)expression;
 				Integer integer = Float.floatToRawIntBits(floatLiteral.getFloatValue());
 				String labelName = "."+generateLabel();
 				String floatLabel = labelName+":\n";
 				floatLabel += "\t"+".long "+integer+"\n";
 				floatLabels.add(floatLabel);
 				push += labelName+"\n";
 			}
 			return push;
 		} else {
 			// The expression is stored in a location
 			Location loc = (Location)expression;
 			DeclarationIdentifier declIdentifier = loc.getDeclaration();
 			if (loc instanceof VarLocation) {
 				// The location is a var location
 				if (declIdentifier.isGlobal()) {
 					push += declIdentifier.getId()+" "+"\n";
 				} else {
					push += loc.getOffset()+"(%ebp)"+"\n";
 				}
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
 				if (declIdentifier.isGlobal()) {
 					String mult = "\t"+"imull $4, %ecx"+"\n";
 					push += declIdentifier.getId()+"+0(%ecx)"+"\n";
 					return movIndex+mult+push;
 				} else {
 					Integer arraySize = arrayLocation.getDeclaration().getCapacity();
 					Integer correctBase = loc.getOffset()-(4*(arraySize-1));
 					push += correctBase+"(%ebp,%ecx,4)"+"\n";
 					return movIndex+push;
 				}
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
 		String movl;
 		if (stmt instanceof OneAddressStatement) {
 			// The return must return a value stored in a location
 			Expression expr = ((OneAddressStatement)stmt).getExpression();
 			if (expr instanceof Literal) {
 				// The expression is a literal, so move the literal to the eax
 				movl = generateCodeForMoveLiteralToRegister((Literal)expr,"eax");
 				return movl+leave+ret;
 			} else {
 				// The expression is in a location
 				Location loc = (Location)expr;
 				DeclarationIdentifier declIdentifier = loc.getDeclaration();
 				if (loc instanceof VarLocation) {
 					// The location is a var location
 					if (declIdentifier.isGlobal()) {
 						movl = "\t"+"movl "+declIdentifier.getId()+", %eax"+"\n";
 					} else {
 						movl = "\tmovl "+loc.getOffset()+"(%ebp), %eax"+"\n";
 					}
 					return movl+leave+ret;
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
 					if (declIdentifier.isGlobal()) {
 						String mult = "\t"+"imull $4, %ecx"+"\n";
 						movl = "\t"+"movl "+declIdentifier.getId()+"+0(%ecx), %eax"+"\n";
 					} else {
 						Integer arraySize = arrayLocation.getDeclaration().getCapacity();
 						Integer correctBase = loc.getOffset()-(4*(arraySize-1));
 						movl = "\t"+"movl "+correctBase+"(%ebp,%ecx,4), %eax"+"\n";
 					}
 					return movIndex+movl+leave+ret;
 				}
 			}
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
 			String stringExpr1 = "";
 			String stringExpr2 = "";
 			String mov1 = "\t"+"movl ";
 			String ins = "\t"+instructionName+" ";
 			String mov2 = "\t"+"movl ";
 			boolean considerIns = false;
 			if (expression1 instanceof Literal) {
 				// The expression one is a literal
 				stringExpr1 = "$"+expression1.toString();
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
 						String movIndex2 = "\t"+"movl ";
 						if (index2 instanceof Literal) {
 							// The index is a literal
 							movIndex2 += "$"+((IntLiteral)index2).getIntegerValue()+", %edx"+"\n";
 						} else {
 							// The index is stored in a location
 							movIndex2 += ((Location)index2).getOffset()+"(%ebp), %edx"+"\n";
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
 					if (locDeclaration1.isGlobal()) {
 						stringExpr1 = locDeclaration1.getId();
 					} else {
						stringExpr1 = location1.getOffset()+"(%ebp)"; 
 					}
 				} else {
 					// The expression location is a var array location
 					VarArrayLocation arrayLocation1 = (VarArrayLocation)location1;
 					Expression index1 = arrayLocation1.getExpression();
 					String movIndex1 = "\t"+"movl ";
 					if (index1 instanceof Literal) {
 						// The index is a literal
 						movIndex1 += "$"+((IntLiteral)index1).getIntegerValue()+", %ebx"+"\n";
 					} else {
 						// The index is stored in a location
 						movIndex1 += ((Location)index1).getOffset()+"(%ebp), %ebx"+"\n";
 					}
 					mov1 = movIndex1;
 					if (locDeclaration1.isGlobal()) {
 						mov1 += "\t"+"imull $4, %ebx"+"\n";
 						mov1 += "\t"+"movl "+locDeclaration1.getId()+"+0(%ebx), %ebx"+"\n";
 						mov1 += "\t"+"movl ";
 						stringExpr1 = "%ebx";
 					} else {
						Integer arraySize = locDeclaration1.getCapacity();
 						Integer correctBase = locDeclaration1.getOffset()-(4*(arraySize-1));
 						mov1 = "\t"+"movl "+correctBase+"(%ebp,%ebx,4), %ebx"+"\n";
 						mov1 += "\t"+"movl";
 						stringExpr1 = "%ebx";
 					}
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
 						String movIndex2 = "\t"+"movl ";
 						if (index2 instanceof Literal) {
 							// The index is a literal
 							movIndex2 += "$"+((IntLiteral)index2).getIntegerValue()+", %edx"+"\n";
 						} else {
 							// The index is stored in a location
 							movIndex2 += ((Location)index2).getOffset()+"(%ebp), %edx"+"\n";
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
 				mov1 += stringExpr1+", %eax"+"\n";
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

 	/**
 	 * Generate the assembler code for a statement with an instruction for an arithmetical
 	 * opearation with floats (ADDF,SUBF,MULTF,DIVF)
 	 */
 	private static String generateCodeForFloatArithmeticalOperation(IntermediateCodeStatement stmt,String instructionName) {
 		if (stmt instanceof ThreeAddressStatement) {
 			ThreeAddressStatement threeStmt = (ThreeAddressStatement)stmt;
 			Expression expression1 = threeStmt.getExpressionOne();
 			Expression expression2 = threeStmt.getExpressionTwo();
 			String stringExpr1 = "";
 			String stringExpr2 = "";

 			if (expression1 instanceof Literal) {
 				// The expression one is a literal
 				FloatLiteral floatLiteral1 = (FloatLiteral)expression1;
 				Integer integer1 = Float.floatToRawIntBits(floatLiteral1.getFloatValue());
 				String labelName1 = "."+generateLabel();
 				String floatLabel1 = labelName1+":\n";
 				floatLabel1 += "\t"+".long "+integer1+"\n";
 				floatLabels.add(floatLabel1);
 				stringExpr1 = labelName1;
 				if (expression2 instanceof Literal) {
 					// The expression two is a literal
 					FloatLiteral floatLiteral2 = (FloatLiteral)expression2;
 					Integer integer2 = Float.floatToRawIntBits(floatLiteral2.getFloatValue());
 					String labelName2 = "."+generateLabel();
 					String floatLabel2 = labelName2+":\n";
 					floatLabel2 += "\t"+".long "+integer2+"\n";
 					floatLabels.add(floatLabel2);
 					stringExpr2 = labelName2;
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
 					FloatLiteral floatLiteral2 = (FloatLiteral)expression2;
 					Integer integer2 = Float.floatToRawIntBits(floatLiteral2.getFloatValue());
 					String labelName2 = "."+generateLabel();
 					String floatLabel2 = labelName2+":\n";
 					floatLabel2 += "\t"+".long "+integer2+"\n";
 					floatLabels.add(floatLabel2);
 					stringExpr2 = labelName2;
 				} else {
 					// The expression two is not a literal
 					Location location2 = (Location)expression2;
 					stringExpr2 = location2.getOffset()+"(%ebp)"; 
 				}

 			}
 			Location result = (Location)threeStmt.getResult();
 			String flds = "\t"+"flds "+stringExpr1+"\n";
 			String ins = "\t"+instructionName+" "+stringExpr2+"\n";
 			String fstps = "\t"+"fstps "+result.getOffset()+"(%ebp)"+"\n";
 			return flds+ins+fstps;
 		} else {
 			TwoAddressStatement twoStmt = (TwoAddressStatement)stmt;
 			Expression expr = twoStmt.getExpression();
 			String stringExpr;
 			if (expr instanceof Literal) {
 				// The expression is a literal
 				FloatLiteral floatLiteral = (FloatLiteral)expr;
 				Integer integer = Float.floatToRawIntBits(floatLiteral.getFloatValue());
 				String labelName = "."+generateLabel();
 				String floatLabel = labelName+":\n";
 				floatLabel += "\t"+".long "+integer+"\n";
 				floatLabels.add(floatLabel);
 				stringExpr = labelName;
 			} else {
 				// The expression two is not a literal
 				Location location = (Location)expr;
 				stringExpr = location.getOffset()+"(%ebp)";
 			}
 			Location result = (Location)twoStmt.getResult();
 			String flds = "\t"+"flds "+stringExpr+"\n";
 			String ins = "\t"+instructionName+" "+stringExpr+"\n";
 			String fstps = "\t"+"fstps "+result.getOffset()+"(%ebp)"+"\n";
 			return flds+ins+fstps;	
 		}
 	}

	/**
 	 * Generate code for move a CTds literal to a register
 	 */
	public static String generateCodeForMoveLiteralToRegister(Literal literal,String registerName) {
		String movl;
		if (literal instanceof IntLiteral) {
			// The literal is an int literal
 			movl = "\t"+"movl $"+((IntLiteral)literal).getIntegerValue()+", %"+registerName+"\n";
 		} else if (literal instanceof BooleanLiteral){
 			// The literal is a boolean literal
 			BooleanLiteral boolLiteral = (BooleanLiteral)literal;
 			if (boolLiteral.getBooleanValue()) {
 				movl = "\t"+"movl $1, %"+registerName+"\n";
 			} else {
 				movl = "\t"+"movl $0, %"+registerName+"\n";
 			}
 		} else {
			// The literal is a float literal
 			FloatLiteral floatLiteral = (FloatLiteral)literal;
 			Integer integer = Float.floatToRawIntBits(floatLiteral.getFloatValue());
 			String labelName = "."+generateLabel();
 			String floatLabel = labelName+":\n";
 			floatLabel += "\t"+".long "+integer+"\n";
 			floatLabels.add(floatLabel);
 			movl = "\t"+"movl "+labelName+", %"+registerName+"\n";
 		}
 		return movl;
	}

}