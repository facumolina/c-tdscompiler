import java.util.List;
import java.util.LinkedList;

/**
 * This class represents the interpreter visitor for evaluate the CTds program
 * @author Facundo Molina
 */
public class InterpreterVisitor implements ASTVisitor<List<String>> {

	private LinkedList<String> errorsList;			// Errors

	/**
	 * Constructor
	 */
	public InterpreterVisitor() {
		errorsList = new LinkedList<String>();
	}

	/**
	 * Visit a program accepting each class
	 */
	public List<String> visit(Program p) {
		for (ClassDeclaration classDeclaration : p.getClassDeclarations()) {
			errorsList.addAll(classDeclaration.accept(this));
		}
		return errorsList;
	}

	/**
	 * Visit a class declaration accepting each method
	 */
	public List<String> visit(ClassDeclaration decl) {
	 	LinkedList<String> classErrors = new LinkedList<String>();
		for (MethodDeclaration methodDeclaration : decl.getMethodDeclarations()) {
			classErrors.addAll(methodDeclaration.accept(this));
		}
		return classErrors;
	}

	/**
	 * Visit a field declaration 
	 */
	public List<String> visit(FieldDeclaration decl) {
		return new LinkedList<String>();
	}

	/**
	 * Visit a method declaration, accepting the block only if the method
	 * is the main method 
	 */
	public List<String> visit(MethodDeclaration decl) {
		LinkedList<String> methodErrors = new LinkedList<String>();
		if (decl.getId().equals("main")) {
		 	methodErrors.addAll(decl.getBlock().accept(this));
		}
		return methodErrors;
 	}

	/**
	 * Visit an argument
	 */
	public List<String> visit(Argument arg) {
		return new LinkedList<String>();
	}

	/**
	 * Visit a declaration identifier
	 */
	public List<String> visit(DeclarationIdentifier ident) {
		return new LinkedList<String>();
	}
 	
	/**
	 * Visit an assign statement accepting the location and the expression
	 */
	public List<String> visit(AssignStatement stmt) {
		LinkedList<String> assignErrors = new LinkedList<String>();
		Location location = stmt.getLocation();
		Expression expression = stmt.getExpression();
		assignErrors.addAll(expression.accept(this));
		if (assignErrors.size()==0) {
			// The expression is correct
			if (location.getDeclaration().isArrayDeclarationId()) {
				// The location is an array
				assignErrors.addAll(location.accept(this));
				if (assignErrors.size()==0) {
					// There are no errors in the array location
					location.getDeclaration().setValue(expression.getValue());
				}
			} else {
				// The location is a var
				if (stmt.getOperator().equals(AssignOpType.INCREMENT) || stmt.getOperator().equals(AssignOpType.DECREMENT)) {
					// The location must have initialized
					assignErrors.addAll(location.accept(this));
				}
				if (assignErrors.size()==0) {
					location.getDeclaration().setValue(evaluateAssign(location,stmt.getOperator(),expression));
				}
			}
				
		}
		return assignErrors;
	}

	/**
	 * Evaluate assign 
	 */
	private Literal evaluateAssign(Location l,AssignOpType op,Expression e) {
		Literal locationValue = l.getDeclaration().getValue();
		switch (op) {
			case ASSIGN: return e.getValue();
			case INCREMENT: 
				if (locationValue instanceof IntLiteral) {
					Integer n = ((IntLiteral)locationValue).getIntegerValue()+((IntLiteral)e.getValue()).getIntegerValue();
					return new IntLiteral(n);
				}
				if (locationValue instanceof FloatLiteral) {
					Float f = ((FloatLiteral)locationValue).getFloatValue()+((FloatLiteral)e.getValue()).getFloatValue();
					return new FloatLiteral(f);
				}
			case DECREMENT:
				if (locationValue instanceof IntLiteral) {
					Integer n = ((IntLiteral)locationValue).getIntegerValue()-((IntLiteral)e.getValue()).getIntegerValue();
					return new IntLiteral(n);
				}
				if (locationValue instanceof FloatLiteral) {
					Float f = ((FloatLiteral)locationValue).getFloatValue()-((FloatLiteral)e.getValue()).getFloatValue();
					return new FloatLiteral(f);
				}
			default: return null;
		}
	}
	/**
	 * Visit a method call statement
	 */
	public List<String> visit(MethodCallStatement stmt) {
		return stmt.getMethodCall().accept(this);
	}

	/**
	 * Visit a return statement accepting the return expression and
	 * setting the value to the associated method 
	 */
	public List<String> visit(ReturnStatement stmt) {
		LinkedList<String> returnErrors = new LinkedList<String>();
		if (stmt.hasExpression()) {
			returnErrors.addAll(stmt.getExpression().accept(this));
			if (returnErrors.size()==0) {
				// There are no errors in the return expression
				stmt.getMethodDeclaration().setValue(stmt.getExpression().getValue());
			}
		}
		return returnErrors;
	}

	/**
	 * Visit an if statement accepting the expression and taking the ifblock or
	 * the else block according to the expression value
	 */
	public List<String> visit(IfStatement stmt) {
		LinkedList<String> ifStmtErrors = new LinkedList<String>();
		Expression expression = stmt.getCondition();
		ifStmtErrors.addAll(expression.accept(this));
		if (ifStmtErrors.size()==0) {
			// There are no errors in the expression
			BooleanLiteral value = (BooleanLiteral)expression.getValue();
			if (value.getBooleanValue()) {
				// The expression value is true so accept the if block
				ifStmtErrors.addAll(stmt.getIfBlock().accept(this));
			} else {
				// The expression value is false, so if the statement has else block 
				// accept the else block
				if (stmt.hasElseBlock()) {
					ifStmtErrors.addAll(stmt.getElseBlock().accept(this));
				}
			}
		}
		return ifStmtErrors;
	}	

	/**
	 * Visit a for statement accepting the block 
	 */
	public List<String> visit(ForStatement stmt) {
		LinkedList<String> forStmtErrors = new LinkedList<String>();
		AssignStatement initialAssign = stmt.getInitialAssign();
		Expression condition = stmt.getConditionExpression();
		forStmtErrors.addAll(initialAssign.accept(this));
		forStmtErrors.addAll(condition.accept(this));
		if (forStmtErrors.size()==0) {
			// There are no errors in the expression
			DeclarationIdentifier index = (DeclarationIdentifier)initialAssign.getLocation().getDeclaration();
			IntLiteral indexValue = (IntLiteral)index.getValue();
			IntLiteral conditionValue = (IntLiteral)condition.getValue();
			while ( (indexValue.getIntegerValue()<conditionValue.getIntegerValue()) 
					&& (forStmtErrors.size()==0)) {
				// Accept the block while index value < condition 
				forStmtErrors.addAll(stmt.getBlock().accept(this));
				indexValue.setIntegerValue(indexValue.getIntegerValue()+1); 
			}
		}
		return forStmtErrors;
	}

	/**
	 * Visit a while statement 
	 */
	public List<String> visit(WhileStatement stmt){
		LinkedList<String> whileStmtErrors = new LinkedList<String>();
		Expression condition = stmt.getCondition();
		whileStmtErrors.addAll(condition.accept(this));
		if (whileStmtErrors.size()==0) {
			// There are no errors in the condition expression
			while (((BooleanLiteral)condition.getValue()).getBooleanValue()
					&& (whileStmtErrors.size()==0)) {
				// Accept the block while the condition be true
				whileStmtErrors.addAll(stmt.getBlock().accept(this));
				whileStmtErrors.addAll(condition.accept(this));
			}
		}
		return whileStmtErrors;
	}

	/**
	 * Visit break statement
	 */
	public List<String> visit(BreakStatement stmt) {
		return new LinkedList<String>();
	}

	/**
	 * Visit continue statement
	 */
	public List<String> visit(ContinueStatement stmt) {
		return new LinkedList<String>();
	}
	
	/**
	 * Visit a binary expression accepting the left and right operands and calculating
	 * the value
	 */
	public List<String> visit(BinOpExpr expr) {
		LinkedList<String> errorBinExpr = new LinkedList<String>();
		errorBinExpr.addAll(expr.getLeftOperand().accept(this));
		errorBinExpr.addAll(expr.getRightOperand().accept(this));
		if (errorBinExpr.size()==0) {
			// There are no errors in the expressions
			expr.setValue(evaluateBinExpr(expr.getType(),expr.getOperator(),expr.getLeftOperand(),expr.getRightOperand()));
		}
		return errorBinExpr;
	}

	/**
	 * Evaluate a binary expression according to the expression type, the operator
	 * and the operands
	 */
	private Literal evaluateBinExpr(Type exprType,BinOpType op,Expression e1,Expression e2) {
		if (exprType.equals(Type.INT)) {
			// The expression type is int. So the value of the left and right operands
			// is an int literal.
			IntLiteral value = new IntLiteral(0);
			IntLiteral left = (IntLiteral)e1.getValue();
			IntLiteral right = (IntLiteral)e2.getValue();
			switch (op) {
				case PLUS: value.setIntegerValue(left.getIntegerValue()+right.getIntegerValue()); break;
				case MINUS: value.setIntegerValue(left.getIntegerValue()-right.getIntegerValue()); break;
				case MULTIPLY: value.setIntegerValue(left.getIntegerValue()*right.getIntegerValue()); break;
				case DIVIDE: value.setIntegerValue(left.getIntegerValue()/right.getIntegerValue());	break;
				case MOD: value.setIntegerValue(left.getIntegerValue() % right.getIntegerValue());	break;
				default: break;
			}
			return value;
		} else if (exprType.equals(Type.FLOAT)) {
			// The expression type is float. So the value of the left and right operands
			// is a float literal.
			FloatLiteral value = new FloatLiteral(new Float(0.0));
			FloatLiteral left = (FloatLiteral)e1.getValue();
			FloatLiteral right = (FloatLiteral)e2.getValue();
			switch (op) {
				case PLUS: value.setFloatValue(left.getFloatValue()+right.getFloatValue()); break;
				case MINUS: value.setFloatValue(left.getFloatValue()-right.getFloatValue()); break;
				case MULTIPLY: value.setFloatValue(left.getFloatValue()*right.getFloatValue()); break;
				case DIVIDE: value.setFloatValue(left.getFloatValue()/right.getFloatValue());	break;
				default: break;
			}
			return value;
		} else if (exprType.equals(Type.BOOLEAN)) {
			// The expression type is boolean.
			BooleanLiteral value = new BooleanLiteral(); 
			if (e1.getType().equals(Type.INT)) {
				// The left and right expressions are of type int
				IntLiteral left = (IntLiteral)e1.getValue();
				IntLiteral right = (IntLiteral)e2.getValue();
				switch (op) {
					case LE: value.setBooleanValue(left.getIntegerValue()<right.getIntegerValue()); break;
					case LEQ: value.setBooleanValue(left.getIntegerValue()<=right.getIntegerValue()); break;
					case GE: value.setBooleanValue(left.getIntegerValue()>right.getIntegerValue()); break;
					case GEQ: value.setBooleanValue(left.getIntegerValue()>=right.getIntegerValue()); break;
					case CEQ: value.setBooleanValue(left.getIntegerValue()==right.getIntegerValue()); break;
					case NEQ: value.setBooleanValue(left.getIntegerValue()!=right.getIntegerValue()); break;
					default:break;
				} 
			} else if (e1.getType().equals(Type.FLOAT)) {
				// The left and right expressions are of type float
				FloatLiteral left = (FloatLiteral)e1.getValue();
				FloatLiteral right = (FloatLiteral)e2.getValue();
				switch (op) {
					case LE: value.setBooleanValue(left.getFloatValue()<right.getFloatValue()); break;
					case LEQ: value.setBooleanValue(left.getFloatValue()<=right.getFloatValue()); break;
					case GE: value.setBooleanValue(left.getFloatValue()>right.getFloatValue()); break;
					case GEQ: value.setBooleanValue(left.getFloatValue()>=right.getFloatValue()); break;
					case CEQ: value.setBooleanValue(left.getFloatValue()==right.getFloatValue()); break;
					case NEQ: value.setBooleanValue(left.getFloatValue()!=right.getFloatValue()); break;
					default:break;
				}
			} else if (e1.getType().equals(Type.BOOLEAN)) {
				BooleanLiteral left = (BooleanLiteral)e1.getValue();
				BooleanLiteral right = (BooleanLiteral)e2.getValue();
				switch (op) {
					case AND: value.setBooleanValue(left.getBooleanValue()&&right.getBooleanValue()); break;
					case OR:value.setBooleanValue(left.getBooleanValue()||right.getBooleanValue()); break;
					default: break;
				}
			}
			return value;
		}
		return null;
	}

	/**
	 * Visit a unary expression 
	 */
	public List<String> visit(UnaryOpExpr expr) {
		LinkedList<String> errorUnExpr = new LinkedList<String>();
		errorUnExpr.addAll(expr.getOperand().accept(this));
		if (errorUnExpr.size()==0) {
			// There are no errors in the expression
			expr.setValue(evaluateUnaryExpr(expr.getType(),expr.getOperator(),expr.getOperand()));
		} 
		return errorUnExpr;
	}

	/**
	 * Evaluate an unary expression according to the expression type, the operator
	 * and the operand
	 */
	private Literal evaluateUnaryExpr(Type exprType,UnaryOpType op,Expression e) {
		if (exprType.equals(Type.INT)) {
			// The expression type is int. So the value of the operand is an int literal.
			IntLiteral value = new IntLiteral(0);
			IntLiteral exprValue = (IntLiteral)e.getValue();
			switch (op) {
				case MINUS: value.setIntegerValue(-exprValue.getIntegerValue()); break;
				default: break;
			}
			return value;
		} else if (exprType.equals(Type.FLOAT)) {
			// The expression type is float. So the value of the operand is a float literal.
			FloatLiteral value = new FloatLiteral(new Float(0.0));
			FloatLiteral exprValue = (FloatLiteral)e.getValue();
			switch (op) {
				case MINUS: value.setFloatValue(-exprValue.getFloatValue()); break;
				default: break;
			}
			return value;
		} else if (exprType.equals(Type.BOOLEAN)) {
			// The expression type is boolean. So the value of the operand is a boolean literal.
			BooleanLiteral value = new BooleanLiteral(); 
			BooleanLiteral exprValue = (BooleanLiteral)e.getValue();
			switch (op) {
				case NOT: value.setBooleanValue(!exprValue.getBooleanValue()); break;
				default: break;
			}
			return value;
		}
		return null;
	}

	/**
	 * Visit a nullary expression
	 */
	public List<String> visit(NullaryExpr expr) {
		LinkedList<String> errorNullExpr = new LinkedList<String>();
		errorNullExpr.addAll(expr.getExpression().accept(this));
		if (errorNullExpr.size()==0) {
			// There are no errors in the expression
			expr.setValue(expr.getExpression().getValue());
		}
		return errorNullExpr;
	}

	/**
	 * Visit int literal
	 */	
	public List<String> visit(IntLiteral lit) {
		return new LinkedList<String>();
	}

	/**
	 * Visit float literal
	 */
	public List<String> visit(FloatLiteral lit) {
		return new LinkedList<String>();
	}

	/**
	 * Visit boolean literal
	 */
	public List<String> visit(BooleanLiteral lit) {
		return new LinkedList<String>();
	}

	/**
	 * Visit a var location accepting it if it has been initialized
	 */
	public List<String> visit(VarLocation loc) {
		LinkedList<String> varLocationError = new LinkedList<String>();
		if (loc.getDeclaration().getValue()==null) {
			varLocationError.add(loc.getInitializationError());
		}	
		return varLocationError;
	}

	/**
	 * Visit a var array location
	 */
	public List<String> visit(VarArrayLocation loc) {
		LinkedList<String> varArrayLocError = new LinkedList<String>();
		varArrayLocError.addAll(loc.getExpression().accept(this));
		if (varArrayLocError.size()==0) {
			// There are no previous errors
			IntLiteral expressionValue = (IntLiteral)loc.getExpression().getValue();
			Integer arrayCapacity = loc.getDeclaration().getCapacity();
			if ((expressionValue.getIntegerValue()<0) || (expressionValue.getIntegerValue()>arrayCapacity-1)) {
				// The expression value is less than zero or greater than the 
				// array capacity
				varArrayLocError.add(loc.getInvalidIndexError());
			}
		}
		return varArrayLocError;
	}

	/**
	 * Visit method call calculating the values for each arguments
	 * and accepting of the declaration
	 */
	public List<String> visit(MethodCall call) {
		LinkedList<String> methodCallErrors = new LinkedList<String>();
		int i=0;
		while (i < call.getArguments().size()) {
			// Iterate over all the arguments resolving the values
			Expression expression = call.getArguments().get(i);
			methodCallErrors.addAll(expression.accept(this));
			if (methodCallErrors.size()==0) {
				// There are no errors in the expression
				Argument arg = call.getDeclaration().getArguments().get(i);
				arg.getDeclaration().setValue(expression.getValue());
			}
			i++;
		}
		if (methodCallErrors.size()==0) {
			// There are no errors in arguments. Accept the block of the method declaration
			methodCallErrors.addAll(call.getDeclaration().getBlock().accept(this));
		}
		if (methodCallErrors.size()==0) {
			// There are no errors in method execution
			if (call.getDeclaration().hasReturnStatement()) {
				call.setValue(call.getDeclaration().getValue());				
			}
		}
		return methodCallErrors;
	}

	/**
	 * Visit a block accepting only each statement 
	 */
	public List<String> visit(Block block) {
		LinkedList<String> blockErrors = new LinkedList<String>();
		for (Statement statement : block.getStatements()) {
			blockErrors.addAll(statement.accept(this));
		}
		return blockErrors;
	}

	/**
	 * Visit a semicolon statement
	 */
	public List<String> visit(SemicolonStatement s) {
		return new LinkedList<String>();
	}
	
	/**
	 * Visit a print statement
	 */
	public List<String> visit(PrintStatement p) {
		LinkedList<String> printErrors = new LinkedList<String>();
		Expression expression = p.getExpression();
		printErrors.addAll(expression.accept(this));
		if (printErrors.size()==0) {
			// There are no errors in the expression. So print it
			System.out.println(expression.getValue().toString());
		}
		return printErrors;
	}
	
}