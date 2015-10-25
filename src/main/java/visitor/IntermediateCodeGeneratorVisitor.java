import java.util.List;
import java.util.LinkedList;

/**
 * This class represents the intermediate code generator visitor for generate 
 * the intermediate code of a CTds program
 * @author Facundo Molina
 */
public class IntermediateCodeGeneratorVisitor implements ASTVisitor<Location> {

	private LinkedList<IntermediateCodeStatement> intermediateCodeStatements;			// Errors
	private int temporalVarsCounter;
	private int statementsCounter;

	/**
	 * Constructor
	 */
	public IntermediateCodeGeneratorVisitor() {
		intermediateCodeStatements = new LinkedList<IntermediateCodeStatement>();
		temporalVarsCounter = 0;
		statementsCounter = 0;
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
	 * Get the amount of statements
	 */
	public int amountOfStatements() {
		return statementsCounter;
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
						addICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.ADDI,new Label(statementsCounter),stmt.getLocation(),expressionLocation,temporalLocation);
					} else {
						// The expression alone does not need a temporal location
						addICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.ADDI,new Label(statementsCounter),stmt.getLocation(),stmt.getExpression(),temporalLocation);
					}
				} else {
					// The location type is float
					temporalLocation.getDeclaration().setType(Type.FLOAT);
					if (expressionLocation!=null) {
						// The expression result was stored in a temporal location
						addICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.ADDF,new Label(statementsCounter),stmt.getLocation(),expressionLocation,temporalLocation);
					} else {
						// The expression alone does not need a temporal location
						addICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.ADDF,new Label(statementsCounter),stmt.getLocation(),stmt.getExpression(),temporalLocation);
					}
				}
				intermediateCodeStatements.add(addICStmt);
				statementsCounter++;
				assignICStmt = new TwoAddressStatement(IntermediateCodeInstruction.ASSIGN,new Label(statementsCounter),temporalLocation,stmt.getLocation());
				intermediateCodeStatements.add(assignICStmt);
				statementsCounter++;
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
						subICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.SUBI,new Label(statementsCounter),stmt.getLocation(),expressionLocation,temporalLocation);
					} else {
						// The expression alone does not need a temporal location
						subICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.SUBI,new Label(statementsCounter),stmt.getLocation(),stmt.getExpression(),temporalLocation);
					}
				} else {
					// The location type is float
					temporalLocation.getDeclaration().setType(Type.FLOAT);
					if (expressionLocation!=null) {
						// The expression result was stored in a temporal location
						subICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.SUBF,new Label(statementsCounter),stmt.getLocation(),expressionLocation,temporalLocation);
					} else {
						// The expression alone does not need a temporal location
						subICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.SUBF,new Label(statementsCounter),stmt.getLocation(),stmt.getExpression(),temporalLocation);
					}
				}
				intermediateCodeStatements.add(subICStmt);
				statementsCounter++;
				assignICStmt = new TwoAddressStatement(IntermediateCodeInstruction.ASSIGN,new Label(statementsCounter),temporalLocation,stmt.getLocation());
				intermediateCodeStatements.add(assignICStmt);
				statementsCounter++;
				break;
			case ASSIGN:
				expressionLocation = (VarLocation)stmt.getExpression().accept(this);
				if (expressionLocation!=null) {
					// The expression result was stored in a temporal location
					assignICStmt = new TwoAddressStatement(IntermediateCodeInstruction.ASSIGN,new Label(statementsCounter),expressionLocation,stmt.getLocation());
				} else {
					// The expression alone does not need a temporal location
					assignICStmt = new TwoAddressStatement(IntermediateCodeInstruction.ASSIGN,new Label(statementsCounter),stmt.getExpression(),stmt.getLocation());
				}
				intermediateCodeStatements.add(assignICStmt);
				statementsCounter++;
				break;
			default: break;
		}
		return stmt.getLocation();
	}

	/**
	 * Visit a method call statement accepting the method call
	 */
	public Location visit(MethodCallStatement stmt) {
		return stmt.getMethodCall().accept(this);
	}

	/**
	 * Visit a return statement accepting the return expression 
	 */
	public Location visit(ReturnStatement stmt) {
		if (stmt.hasExpression()) {
			return stmt.getExpression().accept(this);
		} else {
			return null;
		}
	}

	/**
	 * Visit an if statement accepting the expression and the blocks
	 */
	public Location visit(IfStatement stmt) {

		VarLocation temporalLocation = (VarLocation)stmt.getCondition().accept(this);
		
		int beforeBlockAmountOfStatements = amountOfStatements();
		
		// Add the jump instruction. Later will be modificated with the correct 
		// label to jump
		Label label = new Label(beforeBlockAmountOfStatements);
		Label labelToJump = new Label(0);
		IntermediateCodeStatement jumpFICStmt = new OneAddressStatement(IntermediateCodeInstruction.JUMPF,label,temporalLocation,labelToJump);
		intermediateCodeStatements.add(jumpFICStmt);
		statementsCounter++; 

		// Accept the if block
		stmt.getIfBlock().accept(this);
		
		if (stmt.hasElseBlock()) {
			// Set the correct label number to jump
			labelToJump.setNumber(amountOfStatements()+1);
			// Add the jump instruction at the end of the if block
			Label afterIfLabel = new Label(amountOfStatements());
			Label toJumpAfterIf = new Label(0); 
			IntermediateCodeStatement jumpICStmt = new OneAddressStatement(IntermediateCodeInstruction.JUMP,afterIfLabel,toJumpAfterIf);
			intermediateCodeStatements.add(jumpICStmt);
			statementsCounter++; 

			stmt.getElseBlock().accept(this);
			// Set the correct label number to jump after if
			toJumpAfterIf.setNumber(amountOfStatements());
		
		} else {
			// Set the correct label number to jump
			labelToJump.setNumber(amountOfStatements());
		} 
		return null;
	}	

	/**
	 * Visit a for statement accepting the block 
	 */
	public Location visit(ForStatement stmt) {
		return null;
	}

	/**
	 * Visit a while statement accepting the expression and the block
	 */
	public Location visit(WhileStatement stmt){
		int amountOfStatementsBeforeConditionEval = amountOfStatements();
		VarLocation temporalLocation = (VarLocation)stmt.getCondition().accept(this);
		int amountOfStatementsAfterConditionEval = amountOfStatements();

		// Add the jump instruction in the case that the expression
		// result was false. Later will be modificated with the correct 
		// label to jump
		Label label = new Label(amountOfStatements());
		Label labelToJump = new Label(0);
		IntermediateCodeStatement jumpFICStmt = new OneAddressStatement(IntermediateCodeInstruction.JUMPF,label,temporalLocation,labelToJump);
		intermediateCodeStatements.add(jumpFICStmt);
		statementsCounter++;

		// Accept the block
		stmt.getBlock().accept(this);
		labelToJump.setNumber(amountOfStatements()+1);

		// After the block, jump to the condition evaluation label
		Label afterBlockLabel = new Label(amountOfStatements());
		Label conditionEvalLabel = new Label(label.getNumber()-(amountOfStatementsAfterConditionEval-amountOfStatementsBeforeConditionEval));
		IntermediateCodeStatement jumpICStmt = new OneAddressStatement(IntermediateCodeInstruction.JUMP,afterBlockLabel,conditionEvalLabel);
		intermediateCodeStatements.add(jumpICStmt);
		statementsCounter++;

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
		Expression leftExpression;
		if (leftExpressionLocation==null) {
			// The left expression does not need a temporal location
			leftExpression = expr.getLeftOperand();
		} else {
			// The left expression result was stored in a temporal location
			leftExpression = leftExpressionLocation;
		}
		VarLocation rightExpressionLocation = (VarLocation)expr.getRightOperand().accept(this);
		Expression rightExpression;
		if (rightExpressionLocation==null) {
			// The right expression does not need a temporal location
			rightExpression = expr.getRightOperand();
		} else {
			// The right expression result was stored in a temporal location
			rightExpression = rightExpressionLocation;
		}
		String tempVarName = getTempVarName();
		VarLocation temporalLocation = new VarLocation(tempVarName,expr.getLineNumber(),expr.getColumnNumber());
		temporalLocation.setDeclaration(new DeclarationIdentifier(tempVarName,expr.getLineNumber(),expr.getColumnNumber()));
		temporalLocation.getDeclaration().setType(expr.getType());
		
		exprICStmt = new ThreeAddressStatement(instruction,new Label(statementsCounter),leftExpression,rightExpression,temporalLocation);
		intermediateCodeStatements.add(exprICStmt);
		statementsCounter++;

		// Consider the cases in which is needed more than one intermediate
		// code statement

		if (expr.getOperator()==BinOpType.NEQ) {
			// The binary operator is !=. So add an instruction for negate, because the exprICStmt
			// was setted with the EQ instruction.

			tempVarName = getTempVarName();
			VarLocation temporalLocation2 = new VarLocation(tempVarName,expr.getLineNumber(),expr.getColumnNumber());
			temporalLocation2.setDeclaration(new DeclarationIdentifier(tempVarName,expr.getLineNumber(),expr.getColumnNumber()));
			temporalLocation2.getDeclaration().setType(expr.getType());

			IntermediateCodeStatement negICStmt = new TwoAddressStatement(IntermediateCodeInstruction.NOT,new Label(statementsCounter),temporalLocation,temporalLocation2);
			intermediateCodeStatements.add(negICStmt);
			statementsCounter++;
			return temporalLocation2;
		
		} else if ((expr.getOperator()==BinOpType.LEQ) || (expr.getOperator()==BinOpType.GEQ)) {
			// The binary operator is <= or >=. So add one instruction for equality and another
			// instruction for make an or, between the equality result and the result of
			// exprICStmt

			tempVarName = getTempVarName();
			VarLocation temporalLocation2 = new VarLocation(tempVarName,expr.getLineNumber(),expr.getColumnNumber());
			temporalLocation2.setDeclaration(new DeclarationIdentifier(tempVarName,expr.getLineNumber(),expr.getColumnNumber()));
			temporalLocation2.getDeclaration().setType(expr.getType());

			IntermediateCodeStatement eqICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.EQ,new Label(statementsCounter),leftExpression,rightExpression,temporalLocation2); 
			
			tempVarName = getTempVarName();
			VarLocation temporalLocation3 = new VarLocation(tempVarName,expr.getLineNumber(),expr.getColumnNumber());
			temporalLocation3.setDeclaration(new DeclarationIdentifier(tempVarName,expr.getLineNumber(),expr.getColumnNumber()));
			temporalLocation3.getDeclaration().setType(expr.getType());

			IntermediateCodeStatement orICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.OR,new Label(statementsCounter),temporalLocation,temporalLocation2,temporalLocation3); 

			intermediateCodeStatements.add(eqICStmt);
			intermediateCodeStatements.add(orICStmt);
			statementsCounter+=2;

			return temporalLocation3;
		
		} else {
			// Not need more instructions
			return temporalLocation;
		}
		
	}

	/**
	 * Get the instruction for the given binary operator and tpye
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
				if (t.equals(Type.INT)) {
					return IntermediateCodeInstruction.DIVI;
				} else {
					return IntermediateCodeInstruction.DIVF;
				}
			case MOD:
				return IntermediateCodeInstruction.MOD;			
			case LE:
				return IntermediateCodeInstruction.LESS;		
			case LEQ:
				return IntermediateCodeInstruction.LESS;
			case GE:
				return IntermediateCodeInstruction.GREAT;		
			case GEQ:
				return IntermediateCodeInstruction.GREAT;
			case CEQ:
				return IntermediateCodeInstruction.EQ;
			case NEQ:
				return IntermediateCodeInstruction.EQ;
			case AND:
				return IntermediateCodeInstruction.AND;
			case OR:
				return IntermediateCodeInstruction.OR;
		}
		return null;
	}

	/**
	 * Visit a unary expression 
	 */
	public Location visit(UnaryOpExpr expr) {
		IntermediateCodeStatement exprICStmt;
		IntermediateCodeInstruction instruction = getInstruction(expr.getOperator(),expr.getType());
		VarLocation expressionLocation = (VarLocation)expr.getOperand().accept(this);
		String tempVarName = getTempVarName();
		VarLocation temporalLocation = new VarLocation(tempVarName,expr.getLineNumber(),expr.getColumnNumber());
		temporalLocation.setDeclaration(new DeclarationIdentifier(tempVarName,expr.getLineNumber(),expr.getColumnNumber()));
		temporalLocation.getDeclaration().setType(expr.getType());
		if (expressionLocation!=null) {
			// The expression result was stored in a temporal location
			exprICStmt = new TwoAddressStatement(instruction,new Label(statementsCounter),expressionLocation,temporalLocation);
		} else {
			// The expression does not need a temporal location
			exprICStmt = new TwoAddressStatement(instruction,new Label(statementsCounter),expr.getOperand(),temporalLocation);
		}
		intermediateCodeStatements.add(exprICStmt);
		statementsCounter++;
		return temporalLocation;
	}

	/**
	 * Get the instruction for the given unary operator and tpye
	 */
	private IntermediateCodeInstruction getInstruction(UnaryOpType op,Type t) {
		switch(op) {
			case NOT:
				return IntermediateCodeInstruction.NOT;
			case MINUS:
				if (t.equals(Type.INT)) {
					return IntermediateCodeInstruction.SUBI;
				} else{ 
					return IntermediateCodeInstruction.SUBF;
				}
		}
		return null;
	}

	/**
	 * Visit a nullary expression
	 */
	public Location visit(NullaryExpr expr) {
		return expr.getExpression().accept(this);
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
	 * Visit a var location 
	 */
	public Location visit(VarLocation loc) {
		return loc;
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