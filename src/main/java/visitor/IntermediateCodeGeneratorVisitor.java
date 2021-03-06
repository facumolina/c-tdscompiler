import java.util.List;
import java.util.LinkedList;
import java.util.Stack;

/**
 * This class represents the intermediate code generator visitor for generate 
 * the intermediate code of a CTds program
 * @author Facundo Molina
 */
public class IntermediateCodeGeneratorVisitor implements ASTVisitor<Location> {

	private LinkedList<IntermediateCodeStatement> intermediateCodeStatements;			// Errors
	private int temporalVarsCounter;
	private int statementsCounter;
	private int offset;
	private Stack<Label> inLabels;
	private Stack<Label> outLabels;

	/**
	 * Constructor
	 */
	public IntermediateCodeGeneratorVisitor() {
		intermediateCodeStatements = new LinkedList<IntermediateCodeStatement>();
		temporalVarsCounter = 0;
		statementsCounter = 0;
		offset = 0;
		inLabels = new Stack<Label>();
		outLabels = new Stack<Label>();
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
	 * Get next offset
	 */
	private int getNextOffset() {
		offset -= 4;
		return offset;
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
		for (FieldDeclaration fieldDeclaration: decl.getFieldDeclarations()) {
			fieldDeclaration.setIsGlobal(true);
			fieldDeclaration.accept(this);
		}
		for (MethodDeclaration methodDeclaration : decl.getMethodDeclarations()) {
			methodDeclaration.accept(this);
		}
		return null;
	}

	/**
	 * Visit a field declaration 
	 */
	public Location visit(FieldDeclaration decl) {
		for (DeclarationIdentifier d : decl.getListIds()) {
				d.setIsGlobal(decl.isGlobal());
				d.accept(this);                                                              
		} 
		return null;
	}

	/**
	 * Visit a method declaration creating the label and accepting the block 
	 */
	public Location visit(MethodDeclaration decl) {
		
		if (!decl.isExtern()) {
	
			// The method is not extern
			// Create the instruction for init the label
			VarLocation methodLocation = new VarLocation(decl.getId(),decl.getLineNumber(),decl.getColumnNumber());
			IntermediateCodeStatement initMLICStmt = new OneAddressStatement(IntermediateCodeInstruction.INITML,new Label(statementsCounter),methodLocation);
			intermediateCodeStatements.add(initMLICStmt);
			statementsCounter++;

			// Set offset for each argument
			int argumentOffset = 8;
			for (Argument arg : decl.getArguments()) {
				arg.getDeclaration().setOffset(argumentOffset);
				argumentOffset += 4;
			}

			// Create the instruction for reserve space for local and temporal variables
			VarLocation temporalLocation = new VarLocation("amount",decl.getLineNumber(),decl.getColumnNumber());
			temporalLocation.setType(Type.INT); 
			IntermediateCodeStatement reserveICStmt = new OneAddressStatement(IntermediateCodeInstruction.RESERVE,new Label(statementsCounter),temporalLocation);
			intermediateCodeStatements.add(reserveICStmt);
			statementsCounter++;

			int currentTemporalAmount = temporalVarsCounter;

			// Accept the block
			decl.getBlock().accept(this);

			// Set the correct number of local and temporal for reserve space
			IntLiteral amount = new IntLiteral(decl.getAmountOfFieldDeclarations()
				+ (temporalVarsCounter - currentTemporalAmount));
			DeclarationIdentifier declIdentifier = new DeclarationIdentifier("amount",decl.getLineNumber(),decl.getColumnNumber());
			declIdentifier.setType(Type.INT);
			temporalLocation.setDeclaration(declIdentifier);
			temporalLocation.setValue(amount);

			// Restore the offset for the next method declaration
			offset = 0;
		
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
		if (ident.isGlobal()) {
			// The declaration is global
			VarLocation declLocation = new VarLocation(ident.getId(),ident.getLineNumber(),ident.getColumnNumber());
			declLocation.setDeclaration(ident);
			IntermediateCodeStatement stmt = new OneAddressStatement(IntermediateCodeInstruction.GLOBAL,new Label(statementsCounter),declLocation);
			intermediateCodeStatements.add(stmt);
			statementsCounter++;
			return null;
		} else {
			// The declaration is local, so set the offset
			if (ident.isArrayDeclarationId()) {
				// The declaration is an array, so reserve space for every position
				ident.setOffset(getNextOffset());
				for (int i=0; i < ident.getCapacity()-1 ; i++) {
					getNextOffset();
				}
			} else {
				// The declaration is a variable
				ident.setOffset(getNextOffset());
			}
			return null;
		}
		
	}
 	
	/**
	 * Visit an assign statement
	 */
	public Location visit(AssignStatement stmt) {
		IntermediateCodeStatement assignICStmt;
		String tempVarName;
		VarLocation temporalLocation;
		DeclarationIdentifier declIdentifier;
		Location expressionLocation;
		Location locationToBeAssigned = stmt.getLocation().accept(this);
		switch (stmt.getOperator()) {
			case INCREMENT:
				// The assignment operator is +=. So create one instruction for solve
				// the addition storing the result in a temporal location 
				// and create other instruction for solve the assignment
				IntermediateCodeStatement addICStmt;
				tempVarName = getTempVarName();
				temporalLocation = new VarLocation(tempVarName,stmt.getLineNumber(),stmt.getColumnNumber());
				declIdentifier = new DeclarationIdentifier(tempVarName,stmt.getLineNumber(),stmt.getColumnNumber());
				declIdentifier.setOffset(getNextOffset());
				temporalLocation.setDeclaration(declIdentifier);
				expressionLocation = stmt.getExpression().accept(this);
				if (stmt.getLocation().getType().equals(Type.INT)) {
					// The location type is int
					temporalLocation.getDeclaration().setType(Type.INT);
					if (expressionLocation!=null) {
						// The expression result was stored in a temporal location
						addICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.ADDI,new Label(statementsCounter),locationToBeAssigned,expressionLocation,temporalLocation);
					} else {
						// The expression alone does not need a temporal location
						addICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.ADDI,new Label(statementsCounter),locationToBeAssigned,stmt.getExpression(),temporalLocation);
					}
				} else {
					// The location type is float
					temporalLocation.getDeclaration().setType(Type.FLOAT);
					if (expressionLocation!=null) {
						// The expression result was stored in a temporal location
						addICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.ADDF,new Label(statementsCounter),locationToBeAssigned,expressionLocation,temporalLocation);
					} else {
						// The expression alone does not need a temporal location
						addICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.ADDF,new Label(statementsCounter),locationToBeAssigned,stmt.getExpression(),temporalLocation);
					}
				}
				intermediateCodeStatements.add(addICStmt);
				statementsCounter++;
				assignICStmt = new TwoAddressStatement(IntermediateCodeInstruction.ASSIGN,new Label(statementsCounter),temporalLocation,locationToBeAssigned);
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
				declIdentifier = new DeclarationIdentifier(tempVarName,stmt.getLineNumber(),stmt.getColumnNumber());
				declIdentifier.setOffset(getNextOffset());
				temporalLocation.setDeclaration(declIdentifier);
				expressionLocation = stmt.getExpression().accept(this);
				if (stmt.getLocation().getType().equals(Type.INT)) {
					// The location type is int
					temporalLocation.getDeclaration().setType(Type.INT);
					if (expressionLocation!=null) {
						// The expression result was stored in a temporal location
						subICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.SUBI,new Label(statementsCounter),locationToBeAssigned,expressionLocation,temporalLocation);
					} else {
						// The expression alone does not need a temporal location
						subICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.SUBI,new Label(statementsCounter),locationToBeAssigned,stmt.getExpression(),temporalLocation);
					}
				} else {
					// The location type is float
					temporalLocation.getDeclaration().setType(Type.FLOAT);
					if (expressionLocation!=null) {
						// The expression result was stored in a temporal location
						subICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.SUBF,new Label(statementsCounter),locationToBeAssigned,expressionLocation,temporalLocation);
					} else {
						// The expression alone does not need a temporal location
						subICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.SUBF,new Label(statementsCounter),locationToBeAssigned,stmt.getExpression(),temporalLocation);
					}
				}
				intermediateCodeStatements.add(subICStmt);
				statementsCounter++;
				assignICStmt = new TwoAddressStatement(IntermediateCodeInstruction.ASSIGN,new Label(statementsCounter),temporalLocation,locationToBeAssigned);
				intermediateCodeStatements.add(assignICStmt);
				statementsCounter++;
				break;
			case ASSIGN:
				expressionLocation = stmt.getExpression().accept(this);
				if (expressionLocation!=null) {
					// The expression result was stored in a temporal location
					assignICStmt = new TwoAddressStatement(IntermediateCodeInstruction.ASSIGN,new Label(statementsCounter),expressionLocation,locationToBeAssigned);
				} else {
					// The expression alone does not need a temporal location
					assignICStmt = new TwoAddressStatement(IntermediateCodeInstruction.ASSIGN,new Label(statementsCounter),stmt.getExpression(),locationToBeAssigned);
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
		IntermediateCodeStatement returnICStmt;
		Location temporalLocation;
		if (stmt.hasExpression()) {
			// The return statement has an expression
			Location temporalLocationExpr =  stmt.getExpression().accept(this);
			Expression exprLocation;
			if (temporalLocationExpr == null) {
				exprLocation = stmt.getExpression();
				temporalLocation = null;
			} else {
				exprLocation = temporalLocationExpr;
				temporalLocation = temporalLocationExpr;
			}
			returnICStmt = new OneAddressStatement(IntermediateCodeInstruction.RET,new Label(statementsCounter),exprLocation);
		} else {
			// The return statement has not an expression
			returnICStmt = new IntermediateCodeStatement(IntermediateCodeInstruction.RET,new Label(statementsCounter));
			temporalLocation = null;
		} 
		intermediateCodeStatements.add(returnICStmt);
		statementsCounter++;
		return temporalLocation;
	}

	/**
	 * Visit an if statement accepting the expression and the blocks
	 */
	public Location visit(IfStatement stmt) {

		Location temporalLocation = stmt.getCondition().accept(this);
		Expression locationExpression;
		if (temporalLocation == null ) {
			locationExpression = stmt.getCondition();
		} else {
			locationExpression = temporalLocation;
		}

		int beforeBlockAmountOfStatements = amountOfStatements();
		
		// Add the jump instruction. Later will be modificated with the correct 
		// label to jump
		Label label = new Label(beforeBlockAmountOfStatements);
		Label labelToJump = new Label(0);
		IntermediateCodeStatement jumpFICStmt = new OneAddressStatement(IntermediateCodeInstruction.JUMPF,label,locationExpression,labelToJump);
		intermediateCodeStatements.add(jumpFICStmt);
		statementsCounter++; 

		// Accept the if block
		stmt.getIfBlock().accept(this);
		
		if (stmt.hasElseBlock()) {
			
			// Add the jump instruction at the end of the if block
			Label afterIfLabel = new Label(amountOfStatements());
			Label toJumpAfterIf = new Label(0); 
			IntermediateCodeStatement jumpICStmt = new OneAddressStatement(IntermediateCodeInstruction.JUMP,afterIfLabel,toJumpAfterIf);
			intermediateCodeStatements.add(jumpICStmt);
			statementsCounter++; 

			// Set the correct label number to jump
			labelToJump.setNumber(amountOfStatements()+1);

			// Add a label instruction
			IntermediateCodeStatement labelICStmt = new OneAddressStatement(IntermediateCodeInstruction.LABEL,new Label(amountOfStatements()),new Label(amountOfStatements()+1));
			intermediateCodeStatements.add(labelICStmt);
			statementsCounter++;

			stmt.getElseBlock().accept(this);
			// Set the correct label number to jump after if
			toJumpAfterIf.setNumber(amountOfStatements());

			// Add a label instruction
			IntermediateCodeStatement labelICStmt2 = new OneAddressStatement(IntermediateCodeInstruction.LABEL,new Label(amountOfStatements()),new Label(amountOfStatements()));
			intermediateCodeStatements.add(labelICStmt2);
			statementsCounter++;

		} else {

			// Add a label instruction and set the correct label number to jump
			IntermediateCodeStatement labelICStmt = new OneAddressStatement(IntermediateCodeInstruction.LABEL,new Label(amountOfStatements()),new Label(amountOfStatements()+1));
			intermediateCodeStatements.add(labelICStmt);
			statementsCounter++; 

			labelToJump.setNumber(amountOfStatements());
		} 
		return null;
	}	

	/**
	 * Visit a for statement accepting the initial assign and the block
	 */
	public Location visit(ForStatement stmt) {
		
		VarLocation temporalLocation = (VarLocation)stmt.getInitialAssign().accept(this);

		// Add a label instruction 
		IntermediateCodeStatement labelICStmt = new OneAddressStatement(IntermediateCodeInstruction.LABEL,new Label(amountOfStatements()),new Label(amountOfStatements()+1));
		intermediateCodeStatements.add(labelICStmt);
		statementsCounter++;

		int beforeEvaluateExpression = amountOfStatements();
		Label toJumpAfterBlock = new Label(beforeEvaluateExpression); 
		inLabels.push(toJumpAfterBlock);

		// Create an expression for compare if the location of
		// initial variable is less or equal than the expression
		Expression comparationExpression = new BinOpExpr(temporalLocation,BinOpType.LEQ,stmt.getConditionExpression(),0,0);
		comparationExpression.setType(Type.BOOLEAN);
		VarLocation temporalLocation2 = (VarLocation)comparationExpression.accept(this);

		// Add the jump instruction. Later will be modificated with the correct 
		// label to jump
		Label label = new Label(amountOfStatements());
		Label labelToJump = new Label(0);
		outLabels.push(labelToJump);
		IntermediateCodeStatement jumpFICStmt = new OneAddressStatement(IntermediateCodeInstruction.JUMPF,label,temporalLocation2,labelToJump);
		intermediateCodeStatements.add(jumpFICStmt);
		statementsCounter++; 

		//int beforeBlockAmountOfStatements = amountOfStatements();
		stmt.getBlock().accept(this);

		// Increment the cycle variable
		Label l = new Label(amountOfStatements());
		IntermediateCodeStatement addICStmt = new ThreeAddressStatement(IntermediateCodeInstruction.ADDI,l,temporalLocation,new IntLiteral(1),temporalLocation);
		intermediateCodeStatements.add(addICStmt);
		statementsCounter++;

		// Jump to the comparation for cycle again
		Label afterBlockLabel = new Label(amountOfStatements());
		IntermediateCodeStatement jumpICStmt = new OneAddressStatement(IntermediateCodeInstruction.JUMP,afterBlockLabel,toJumpAfterBlock);
		intermediateCodeStatements.add(jumpICStmt);
		statementsCounter++; 

		// Add a label instruction and set the correct label number to jump
		IntermediateCodeStatement labelICStmt2 = new OneAddressStatement(IntermediateCodeInstruction.LABEL,new Label(amountOfStatements()),new Label(amountOfStatements()+1));
		intermediateCodeStatements.add(labelICStmt2);
		statementsCounter++;

		labelToJump.setNumber(amountOfStatements()); 
		/*if (!outLabels.isEmpty()) {
			outLabels.pop();
		}*/
		inLabels.pop();
		outLabels.pop();
		return null;
	}

	/**
	 * Visit a while statement accepting the expression and the block
	 */
	public Location visit(WhileStatement stmt){
		// Add a label instruction 
		int amountOfStatementsBeforeConditionEval = amountOfStatements();
		IntermediateCodeStatement labelICStmt = new OneAddressStatement(IntermediateCodeInstruction.LABEL,new Label(amountOfStatements()),new Label(amountOfStatements()));
		intermediateCodeStatements.add(labelICStmt);
		statementsCounter++;

		Label conditionEvalLabel = new Label(0);
		inLabels.push(conditionEvalLabel);

		Location temporalLocation = stmt.getCondition().accept(this);
		Expression locationExpression;
		if (temporalLocation == null ) {
			locationExpression = stmt.getCondition();
		} else {
			locationExpression = temporalLocation;
		}

		int amountOfStatementsAfterConditionEval = amountOfStatements();

		// Add the jump instruction in the case that the expression
		// result was false. Later will be modificated with the correct 
		// label to jump
		Label label = new Label(amountOfStatements());
		Label labelToJump = new Label(0);
		outLabels.push(labelToJump);
		IntermediateCodeStatement jumpFICStmt = new OneAddressStatement(IntermediateCodeInstruction.JUMPF,label,locationExpression,labelToJump);
		intermediateCodeStatements.add(jumpFICStmt);
		statementsCounter++;

		// Accept the block
		stmt.getBlock().accept(this);
		labelToJump.setNumber(amountOfStatements()+1);

		// After the block, jump to the condition evaluation label
		Label afterBlockLabel = new Label(amountOfStatements());
		//Label conditionEvalLabel = new Label(label.getNumber()-(amountOfStatementsAfterConditionEval-amountOfStatementsBeforeConditionEval));
		conditionEvalLabel.setNumber(label.getNumber()-(amountOfStatementsAfterConditionEval-amountOfStatementsBeforeConditionEval));
		IntermediateCodeStatement jumpICStmt = new OneAddressStatement(IntermediateCodeInstruction.JUMP,afterBlockLabel,conditionEvalLabel);
		intermediateCodeStatements.add(jumpICStmt);
		statementsCounter++;

		// Add a label instruction and set the correct label number to jump
		IntermediateCodeStatement labelICStmt2 = new OneAddressStatement(IntermediateCodeInstruction.LABEL,new Label(amountOfStatements()),new Label(amountOfStatements()));
		intermediateCodeStatements.add(labelICStmt2);
		statementsCounter++;

		inLabels.pop();
		outLabels.pop();
		return null;
	}

	/**
	 * Visit break statement
	 */
	public Location visit(BreakStatement stmt) {
		Label label = new Label(amountOfStatements());
		Label labelToJump = outLabels.peek();
		IntermediateCodeStatement jumpICStmt = new OneAddressStatement(IntermediateCodeInstruction.JUMP,label,labelToJump);
		intermediateCodeStatements.add(jumpICStmt);
		statementsCounter++;
		return null;
	}

	/**
	 * Visit continue statement
	 */
	public Location visit(ContinueStatement stmt) {
		Label label = new Label(amountOfStatements());
		Label labelToJump = inLabels.peek();
		IntermediateCodeStatement jumpICStmt = new OneAddressStatement(IntermediateCodeInstruction.JUMP,label,labelToJump);
		intermediateCodeStatements.add(jumpICStmt);
		statementsCounter++;
		return null;
	}
	
	/**
	 * Visit a binary expression accepting the left and right operands and calculating
	 * the value
	 */
	public Location visit(BinOpExpr expr) {
		IntermediateCodeStatement exprICStmt;
		IntermediateCodeInstruction instruction = getInstruction(expr.getOperator(),expr.getType());
		Location leftExpressionLocation = expr.getLeftOperand().accept(this);
		Expression leftExpression;
		if (leftExpressionLocation==null) {
			// The left expression does not need a temporal location
			leftExpression = expr.getLeftOperand();
		} else {
			// The left expression result was stored in a temporal location
			leftExpression = leftExpressionLocation;
		}
		Location rightExpressionLocation = expr.getRightOperand().accept(this);
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
		DeclarationIdentifier declIdentifier = new DeclarationIdentifier(tempVarName,expr.getLineNumber(),expr.getColumnNumber());
		declIdentifier.setOffset(getNextOffset());
		temporalLocation.setDeclaration(declIdentifier);
		temporalLocation.getDeclaration().setType(expr.getType());
		
		exprICStmt = new ThreeAddressStatement(instruction,new Label(statementsCounter),leftExpression,rightExpression,temporalLocation);
		intermediateCodeStatements.add(exprICStmt);
		statementsCounter++;

		return temporalLocation;
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
				return IntermediateCodeInstruction.LESSEQ;
			case GE:
				return IntermediateCodeInstruction.GREAT;		
			case GEQ:
				return IntermediateCodeInstruction.GREATEQ;
			case CEQ:
				return IntermediateCodeInstruction.EQ;
			case NEQ:
				return IntermediateCodeInstruction.NEQ;
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
		DeclarationIdentifier declIdentifier = new DeclarationIdentifier(tempVarName,expr.getLineNumber(),expr.getColumnNumber());
		declIdentifier.setOffset(getNextOffset());
		declIdentifier.setType(expr.getType());
		temporalLocation.setDeclaration(declIdentifier);
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
		Location temporalLocation = expr.getExpression().accept(this);
		if (temporalLocation == null) {
			String tempVarName = getTempVarName();
			temporalLocation = new VarLocation(tempVarName,expr.getLineNumber(),expr.getColumnNumber());
			DeclarationIdentifier declIdentifier = new DeclarationIdentifier(tempVarName,expr.getLineNumber(),expr.getColumnNumber());
			declIdentifier.setOffset(getNextOffset());
			temporalLocation.setDeclaration(declIdentifier);
			
			IntermediateCodeStatement assignICStmt = new TwoAddressStatement(IntermediateCodeInstruction.ASSIGN,new Label(statementsCounter),expr.getExpression(),temporalLocation);
			intermediateCodeStatements.add(assignICStmt);
			statementsCounter++;
		} 
		return temporalLocation;
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
		VarLocation temporalLocation = (VarLocation) loc.getExpression().accept(this);
		if (temporalLocation!=null) {
			// The expression result was stored in a temporal location
			loc.setExpression(temporalLocation);	
		}
		return loc;
	}

	/**
	 * Visit method call accepting each argument
	 */
	public Location visit(MethodCall call) {
		Location temporalLocation;
		IntermediateCodeStatement pushICStmt;
		LinkedList<Expression> argumentsToPush = new LinkedList<Expression>();
		for (Expression argument : call.getArguments()) {
			if (argument instanceof Literal) {
				// The argument is a literal, so not need a location
				argumentsToPush.add(0,argument);
				//pushICStmt = new OneAddressStatement(IntermediateCodeInstruction.PUSH,new Label(statementsCounter),argument);
			} else {
				// The argument is not a literal, so need a location
				temporalLocation = argument.accept(this);
				argumentsToPush.add(0,temporalLocation);
				//pushICStmt = new OneAddressStatement(IntermediateCodeInstruction.PUSH,new Label(statementsCounter),temporalLocation);
			}
			//intermediateCodeStatements.add(pushICStmt);
			//statementsCounter++;
		}
		for (Expression expr: argumentsToPush) {
			pushICStmt = new OneAddressStatement(IntermediateCodeInstruction.PUSH,new Label(statementsCounter),expr);
			intermediateCodeStatements.add(pushICStmt);
			statementsCounter++;
		}

		VarLocation methodLocation = new VarLocation(call.getDeclaration().getId(),call.getLineNumber(),call.getColumnNumber());

		if (call.getDeclaration().getType().equals(Type.VOID)) {
			// The method type is void
			IntermediateCodeStatement callICStmt = new OneAddressStatement(IntermediateCodeInstruction.CALL,new Label(statementsCounter),methodLocation);
			intermediateCodeStatements.add(callICStmt);
			statementsCounter++;
			return null;

		} else {
			
			// The method type is not void, so need a temporal location for
			// store the result
			String tempVarName = getTempVarName();
			temporalLocation = new VarLocation(tempVarName,call.getLineNumber(),call.getColumnNumber());
			DeclarationIdentifier declIdentifier = new DeclarationIdentifier(tempVarName,call.getLineNumber(),call.getColumnNumber());
			declIdentifier.setOffset(getNextOffset());
			temporalLocation.setDeclaration(declIdentifier);
			IntermediateCodeStatement callICStmt = new TwoAddressStatement(IntermediateCodeInstruction.CALL,new Label(statementsCounter),methodLocation,temporalLocation);
			intermediateCodeStatements.add(callICStmt);
			statementsCounter++;
			return temporalLocation;
		
		}

	}

	/**
	 * Visit a block accepting each field declaration and each statement
	 */
	public Location visit(Block block) {
		for (FieldDeclaration fieldDeclaration : block.getFieldDeclarations()) {
			fieldDeclaration.setIsGlobal(false);
			fieldDeclaration.accept(this);
		}
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