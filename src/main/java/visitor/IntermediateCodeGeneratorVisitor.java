import java.util.List;
import java.util.LinkedList;

/**
 * This class represents the intermediate code generator visitor for generate 
 * the intermediate code of a CTds program
 * @author Facundo Molina
 */
public class IntermediateCodeGeneratorVisitor implements ASTVisitor<Location> {

	private LinkedList<IntermediateCodeStatement> intermediateCodeStatements;			// Errors
	private int temporalVarsCounter = 0;

	/**
	 * Constructor
	 */
	public IntermediateCodeGeneratorVisitor() {
		intermediateCodeStatements = new LinkedList<IntermediateCodeStatement>();
	}

	/**
	 * Get the next temporal variable name
	 */
	private String getTempVarName() {
		String tempName = "t"+temporalVarsCounter;
		temporalVarsCounter++;
		return tempName;
	}

	/**
	 * Get the intermediate code statement list
	 */
	public LinkedList<IntermediateCodeStatement> getIntermediateCodeList() {
		return intermediateCodeStatements;
	}

	/**
	 * Visit a program accepting each class
	 */
	public Location visit(Program p) {
		for (ClassDeclaration classDeclaration : p.getClassDeclarations()) {
			classDeclaration.accept(this);
		}
		return null;
	}

	/**
	 * Visit a class declaration accepting each method
	 */
	public Location visit(ClassDeclaration decl) {
		for (MethodDeclaration methodDeclaration : decl.getMethodDeclarations()) {
			methodDeclaration.accept(this);
		}
		return null;
	}

	/**
	 * Visit a field declaration 
	 */
	public Location visit(FieldDeclaration decl) {
		return null;
	}

	/**
	 * Visit a method declaration, accepting the block only if the method
	 * is the main method 
	 */
	public Location visit(MethodDeclaration decl) {
		if (decl.getId().equals("main")) {
		 	decl.getBlock().accept(this);
		}
		return null;
 	}

	/**
	 * Visit an argument
	 */
	public Location visit(Argument arg) {
		return null;	
	}

	/**
	 * Visit a declaration identifier
	 */
	public Location visit(DeclarationIdentifier ident) {
		return null;
	}
 	
	/**
	 * Visit an assign statement
	 */
	public Location visit(AssignStatement stmt) {
		IntermediateCodeStatement assignICStmt;
		String tempVarName;
		VarLocation temporalLocation;
		VarLocation expressionLocation;
		switch (stmt.getOperator()) {
			case INCREMENT:
				// The assignment opertator is +=. So create one instruction for solve
				// the addition storing the result in a temporal location 
				// and create other instruction for solve the assignment
				IntermediateCodeStatement addICStmt;
				tempVarName = getTempVarName();
				temporalLocation = new VarLocation(tempVarName,stmt.getLineNumber(),stmt.getColumnNumber());
				temporalLocation.setDeclaration(new DeclarationIdentifier(tempVarName,stmt.getLineNumber(),stmt.getColumnNumber()));
				expressionLocation = (VarLocation)stmt.getExpression().accept(this);
				if (stmt.getLocation().getType().equals(Type.INT)) {
					// The location type is int
					temporalLocation.getDeclaration().setType(Type.INT);
					if (expressionLocation!=null) {
						// The expression result was stored in a temporal location
						addICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.ADDI,stmt.getLocation(),expressionLocation,temporalLocation);
					} else {
						// The expression alone does not need a temporal location
						addICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.ADDI,stmt.getLocation(),stmt.getExpression(),temporalLocation);
					}
				} else {
					// The location type is float
					temporalLocation.getDeclaration().setType(Type.FLOAT);
					if (expressionLocation!=null) {
						// The expression result was stored in a temporal location
						addICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.ADDF,stmt.getLocation(),expressionLocation,temporalLocation);
					} else {
						// The expression alone does not need a temporal location
						addICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.ADDF,stmt.getLocation(),stmt.getExpression(),temporalLocation);
					}
				}
				intermediateCodeStatements.add(addICStmt);
				assignICStmt = new TwoAddressStatement(IntermediateCodeInstruction.ASSIGN,temporalLocation,stmt.getLocation());
				intermediateCodeStatements.add(assignICStmt);
				break;
			case DECREMENT: 
				// The assignment opertator is -=. So create one instruction for solve
				// the substraction storing the result in a temporal location 
				// and create other instruction for solve the assignment
				IntermediateCodeStatement subICStmt;
				tempVarName = getTempVarName();
				temporalLocation = new VarLocation(tempVarName,stmt.getLineNumber(),stmt.getColumnNumber());
				temporalLocation.setDeclaration(new DeclarationIdentifier(tempVarName,stmt.getLineNumber(),stmt.getColumnNumber()));
				expressionLocation = (VarLocation)stmt.getExpression().accept(this);
				if (stmt.getLocation().getType().equals(Type.INT)) {
					// The location type is int
					temporalLocation.getDeclaration().setType(Type.INT);
					if (expressionLocation!=null) {
						// The expression result was stored in a temporal location
						subICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.SUBI,stmt.getLocation(),expressionLocation,temporalLocation);
					} else {
						// The expression alone does not need a temporal location
						subICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.SUBI,stmt.getLocation(),stmt.getExpression(),temporalLocation);
					}
				} else {
					// The location type is float
					temporalLocation.getDeclaration().setType(Type.FLOAT);
					if (expressionLocation!=null) {
						// The expression result was stored in a temporal location
						subICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.SUBF,stmt.getLocation(),expressionLocation,temporalLocation);
					} else {
						// The expression alone does not need a temporal location
						subICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.SUBF,stmt.getLocation(),stmt.getExpression(),temporalLocation);
					}
				}
				intermediateCodeStatements.add(subICStmt);
				assignICStmt = new TwoAddressStatement(IntermediateCodeInstruction.ASSIGN,temporalLocation,stmt.getLocation());
				intermediateCodeStatements.add(assignICStmt);
				break;
			case ASSIGN:
				expressionLocation = (VarLocation)stmt.getExpression().accept(this);
				if (expressionLocation!=null) {
					// The expression result was stored in a temporal location
					assignICStmt = new TwoAddressStatement(IntermediateCodeInstruction.ASSIGN,expressionLocation,stmt.getLocation());
				} else {
					// The expression alone does not need a temporal location
					assignICStmt = new TwoAddressStatement(IntermediateCodeInstruction.ASSIGN,stmt.getExpression(),stmt.getLocation());
				}
				intermediateCodeStatements.add(assignICStmt);
				break;
			default: break;
		}
		return stmt.getLocation();
	}

	/**
	 * Visit a method call statement
	 */
	public Location visit(MethodCallStatement stmt) {
		return null;
	}

	/**
	 * Visit a return statement accepting the return expression and
	 * setting the value to the associated method 
	 */
	public Location visit(ReturnStatement stmt) {
		return null;
	}

	/**
	 * Visit an if statement accepting the expression and taking the ifblock or
	 * the else block according to the expression value
	 */
	public Location visit(IfStatement stmt) {
		return null;
	}	

	/**
	 * Visit a for statement accepting the block 
	 */
	public Location visit(ForStatement stmt) {
		return null;
	}

	/**
	 * Visit a while statement 
	 */
	public Location visit(WhileStatement stmt){
		return null;
	}

	/**
	 * Visit break statement
	 */
	public Location visit(BreakStatement stmt) {
		return null;
	}

	/**
	 * Visit continue statement
	 */
	public Location visit(ContinueStatement stmt) {
		return null;
	}
	
	/**
	 * Visit a binary expression accepting the left and right operands and calculating
	 * the value
	 */
	public Location visit(BinOpExpr expr) {
		IntermediateCodeStatement exprICStmt;
		IntermediateCodeInstruction instruction = getInstruction(expr.getOperator(),expr.getType());
		VarLocation leftExpressionLocation = (VarLocation)expr.getLeftOperand().accept(this);
		VarLocation rightExpressionLocation = (VarLocation)expr.getRightOperand().accept(this);
		String tempVarName = getTempVarName();
		VarLocation temporalLocation = new VarLocation(tempVarName,expr.getLineNumber(),expr.getColumnNumber());
		temporalLocation.setDeclaration(new DeclarationIdentifier(tempVarName,expr.getLineNumber(),expr.getColumnNumber()));
		temporalLocation.getDeclaration().setType(expr.getType());
		if (leftExpressionLocation!=null) {
			if (rightExpressionLocation!=null) {
				// The left and right expressions result was stored in a temporal location
				return null;
			} else {
				// Only the left expression result was stored in a temporal location
				return null;
			}
		} else {
			if (rightExpressionLocation!=null) {
				// Only the right expression result was stored in a temporal location
				return null;
			} else {
				// The left and right expressions does not need a temporal location
				exprICStmt = new ThreeAddressStatement(instruction,expr.getLeftOperand(),expr.getRightOperand(),temporalLocation);
				intermediateCodeStatements.add(exprICStmt);
				return temporalLocation;
			}
		}
		

	}

	/**
	 * Get the instruction for the given binary operator and a tpye
	 */
	private IntermediateCodeInstruction getInstruction(BinOpType op,Type t) {
		switch(op) {
			case PLUS:
				if (t.equals(Type.INT)) {
					return IntermediateCodeInstruction.ADDI;
				} else{ 
					return IntermediateCodeInstruction.ADDF;
				}
			case MINUS:
				if (t.equals(Type.INT)) {
					return IntermediateCodeInstruction.SUBI;
				} else{ 
					return IntermediateCodeInstruction.SUBF;
				}
			case MULTIPLY:
				if (t.equals(Type.INT)) {
					return IntermediateCodeInstruction.MULTI;
				} else{ 
					return IntermediateCodeInstruction.MULTF;
				}
			case DIVIDE:
				return null;
			case MOD:
				return null;			
			case LE:
				return null;		
			case LEQ:
				return null;
			case GE:
				return null;		
			case GEQ:
				return null;
			case CEQ:
				return null;
			case NEQ:
				return null;
			case AND:
				return null;
			case OR:
				return null;
		}
		return null;
	}

	/**
	 * Visit a unary expression 
	 */
	public Location visit(UnaryOpExpr expr) {
		return null;
	}

	/**
	 * Visit a nullary expression
	 */
	public Location visit(NullaryExpr expr) {
		return null;
	}

	/**
	 * Visit int literal
	 */	
	public Location visit(IntLiteral lit) {
		return null;
	}

	/**
	 * Visit float literal
	 */
	public Location visit(FloatLiteral lit) {
		return null;
	}

	/**
	 * Visit boolean literal
	 */
	public Location visit(BooleanLiteral lit) {
		return null;
	}

	/**
	 * Visit a var location accepting it if it has been initialized
	 */
	public Location visit(VarLocation loc) {
		return null;
	}

	/**
	 * Visit a var array location
	 */
	public Location visit(VarArrayLocation loc) {
		return null;
	}

	/**
	 * Visit method call calculating the values for each arguments
	 * and accepting of the declaration
	 */
	public Location visit(MethodCall call) {
		return null;
	}

	/**
	 * Visit a block accepting only each statement 
	 */
	public Location visit(Block block) {
		for (Statement statement : block.getStatements()) {
			statement.accept(this);
		}
		return null;
	}

	/**
	 * Visit a semicolon statement
	 */
	public Location visit(SemicolonStatement s) {
		return null;
	}
	
	/**
	 * Visit a print statement
	 */
	public Location visit(PrintStatement p) {
		return null;
	}
	
}