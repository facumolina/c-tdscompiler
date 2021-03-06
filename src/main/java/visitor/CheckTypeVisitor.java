import java.util.List;
import java.util.LinkedList;

/**
 * This class represents a visitor that evalutes the types
 * @author Facundo Molina
 */
public class CheckTypeVisitor implements ASTVisitor<List<String>> {

	private SymbolsTable table; 					// Symbols table
	private LinkedList<String> errorsList;			// Errors

	private static final int class_level = 0;
	private static final int field_level = 1;
	private static final int method_level = 2;
	private static final int method_field_level = 3;

	/**
	 * Constructor
	 */
	public CheckTypeVisitor() {
		table = new SymbolsTable();
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
	 * Visit a class declaration accepting each field declaration and each 
	 * method declaration
	 */
	public List<String> visit(ClassDeclaration decl) {
		table.addSymbol(decl);
		table.incrementLevel();
		for (FieldDeclaration fieldDeclaration : decl.getFieldDeclarations()) {
			fieldDeclaration.accept(this);
		}
		LinkedList<String> classErrors = new LinkedList<String>();
		table.incrementLevel();
		for (MethodDeclaration methodDeclaration : decl.getMethodDeclarations()) {
			classErrors.addAll(methodDeclaration.accept(this));
		}
		table.decrementLevel();
		table.decrementLevel();

		return classErrors;
	}

	/**
	 * Visit a field declaration adding each declaration identifier
	 * to the symbols table
	 */
	public List<String> visit(FieldDeclaration decl) {
		for (DeclarationIdentifier d : decl.getListIds()) {
			table.addSymbol(d);
		}
		return new LinkedList<String>();
	}

	/**
	 * Visit a method declaration accepting the block
	 */
	public List<String> visit(MethodDeclaration decl) {
		table.addSymbol(decl);
		LinkedList<String> methodErrors = new LinkedList<String>();
		if (!decl.isExtern()) {
			table.incrementLevel();
		 	methodErrors = (LinkedList<String>)decl.getBlock().accept(this);
			table.decrementLevel();
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
	 * Visit an assign statement accepting it if the type of the location
	 * is the same than the type of the expression
	 */
	public List<String> visit(AssignStatement stmt) {
		LinkedList<String> assignError = new LinkedList<String>();
		Location location = stmt.getLocation();
		assignError.addAll(location.accept(this));
		Expression expression = stmt.getExpression();
		assignError.addAll(expression.accept(this));
		if (assignError.size()==0) {
			// The expression and the location are correct
			if (!location.getDeclaration().getType().equals(expression.getType())) {
				// The type is not the same, so add the error.
				assignError.add(location.getTypeErrorMessage(expression));
			} else {
				// The types are the same
				if (!stmt.compatibleOperator()) {
					// The assignment operator is not compatible for the type of
					// the expression
					assignError.add(stmt.getIncompatibleTypeOperatorError());
				}
			}
		} 
		
		return assignError;
	}

	/**
	 * Visit a method call statement
	 */
	public List<String> visit(MethodCallStatement stmt) {
		return stmt.getMethodCall().accept(this);
	}

	/**
	 * Visit a return statement checking if the type of the returned
	 * expression is the same type declared in the method.
	 */
	public List<String> visit(ReturnStatement stmt) {
		LinkedList<String> returnErrors = new LinkedList<String>();
		MethodDeclaration currentMethod = getCurrentMethod();
		stmt.setMethodDeclaration(currentMethod);
		if (stmt.hasExpression()) {
			if (!currentMethod.getType().equals(Type.VOID)) {
				// The method tpye is not void
				returnErrors.addAll(stmt.getExpression().accept(this));
				if (returnErrors.size()==0) {
					// There are no errors in the return expression
					Expression returnExpression = stmt.getExpression();
					if (!currentMethod.getType().equals(returnExpression.getType())) {
						// The type is not the same, so add the error.
						returnErrors.add(stmt.getTypeErrorMessage(currentMethod));
					}
				}
			} else {
				// The method type is void
				returnErrors.add(currentMethod.getIncorrectReturnStatementError());
			}
		} else {
			if (!currentMethod.getType().equals(Type.VOID)) {
				// The type is not void.
				returnErrors.add(stmt.getTypeNotVoidErrorMessage(currentMethod));
			}
		}
		return returnErrors;
	}

	/**
	 * Get the current method being analized. It is assumed that the current method
	 * allways stays at the end of the list that contains the method declarations
	 */
	private MethodDeclaration getCurrentMethod() {
		int amountOfMethods = table.getLevelSymbols(method_level).size();
		return (MethodDeclaration)table.getLevelSymbols(method_level).get(amountOfMethods-1);
	}

	/**
	 * Visit an if statement accepting the condition expression, and the 
	 * if and else block
	 */
	public List<String> visit(IfStatement stmt) {
		LinkedList<String> ifStmtErrors =  new LinkedList<String>();
		ifStmtErrors.addAll(stmt.getCondition().accept(this));
		if (ifStmtErrors.size()==0) {
			// There are no previous errors in the expression
			if (stmt.getCondition().getType().equals(Type.BOOLEAN)) {
				// The condition expression type is boolean
				ifStmtErrors.addAll(stmt.getIfBlock().accept(this));
				if (stmt.hasElseBlock()) {
					ifStmtErrors.addAll(stmt.getElseBlock().accept(this));
				}
			} else {
				// The condition expression type is not boolean
				ifStmtErrors.add(stmt.getExpressionTypeErrorMessage());
			}
		}
		return ifStmtErrors;
		
	}	

	/**
	 * Visit a for statement accepting the initial assign, the condition 
	 * and the block
	 */
	public List<String> visit(ForStatement stmt) {
		LinkedList<String> forStmtErrors = new LinkedList<String>();
		forStmtErrors.addAll(stmt.getInitialAssign().accept(this));
		if (forStmtErrors.size()==0) {
			// There are no previous erros in the initial assign
			if (stmt.getInitialAssign().getExpression().getType().equals(Type.INT)) {
				forStmtErrors.addAll(stmt.getConditionExpression().accept(this));
				if (forStmtErrors.size()==0) {
					// There are no previous errors
					if (stmt.getConditionExpression().getType().equals(Type.INT)) {
						// Correct condition expression
						forStmtErrors.addAll(stmt.getBlock().accept(this));
					} else {
						// The type of the condition expression is not boolean
						forStmtErrors.add(stmt.getConditionTypeError());
					}
				} 
				
			} else {
				// Incorrect initial assign type error
				forStmtErrors.add(stmt.getAssignTypeError());
			}
		}
		return forStmtErrors;
	}

	/**
	 * Visit a while statement accepting the condition expression and the block
	 */
	public List<String> visit(WhileStatement stmt){
		LinkedList<String> whileStmtErrors = new LinkedList<String>();
		whileStmtErrors.addAll(stmt.getCondition().accept(this));
		if (whileStmtErrors.size()==0) {
			// There are no previous errors in the condition expression
			if (stmt.getCondition().getType().equals(Type.BOOLEAN)) {
				// Correct condition expression
				whileStmtErrors.addAll(stmt.getBlock().accept(this));
			} else {
				// Incorrect condition expression
				whileStmtErrors.add(stmt.getConditionTypeError());
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
	 * Visit a binary expression accepting the left and right expressions
	 * and cheking the types 
	 */
	public List<String> visit(BinOpExpr expr) {
		LinkedList<String> errorBinExpr = new LinkedList<String>();
		errorBinExpr.addAll(expr.getLeftOperand().accept(this));
		errorBinExpr.addAll(expr.getRightOperand().accept(this));
		if (errorBinExpr.size()==0) {
			// There are no errors in the left and right operands
			if (expr.getLeftOperand().getType().equals(expr.getRightOperand().getType())) {
				// The type of the left and right expressions is the same.
				if (expr.compatibleOperator()) {
					// The tpye of the expressions is compatible with the operator
					if (expr.getOperator().isArithmetical()) {
						// The operator is arithmetical
						expr.setType(expr.getLeftOperand().getType());	
					} else {
						// The operator is relational or logical
						expr.setType(Type.BOOLEAN);
					}
				} else {
					// The type of the expressions is incomptble with the operator
					errorBinExpr.add(expr.getIncompatibleTypeOperatorError());
				}
			} else {
				// The type of the left and right operands is different
				errorBinExpr.add(expr.getDifferentTypesErrorMessage());
			}
		}
		return errorBinExpr;
	}

	/**
	 * Visit a unary expression accepting the expression and the compatability with 
	 * the operator
	 */
	public List<String> visit(UnaryOpExpr expr) {
		LinkedList<String> errorUnaryExpr = new LinkedList<String>();
		errorUnaryExpr.addAll(expr.getOperand().accept(this));
		if (errorUnaryExpr.size()==0) {
			// There are no errors in the operand
			if (expr.compatibleOperator()) {
				// The type of the operand is compatible with the operator
				if (expr.getOperator().isArithmetical()) {
					// The operator is arithmetical
					expr.setType(expr.getOperand().getType());
				} else {
					// The operator is logical
					expr.setType(Type.BOOLEAN);
				}
			} else {
				errorUnaryExpr.add(expr.getIncompatibleTypeOperatorError());
			}
		}
		return errorUnaryExpr;
	}

	/**
	 * Visit a nullary expression
	 */
	public List<String> visit(NullaryExpr expr) {
		LinkedList<String> errorNullExpr = new LinkedList<String>(); 
		errorNullExpr.addAll(expr.getExpression().accept(this));
		if (errorNullExpr.size()==0) {
			// There are no previous errors
			expr.setType(expr.getExpression().getType());
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
	 * Visit var location
	 */
	public List<String> visit(VarLocation loc) {
		return new LinkedList<String>();
	}

	/**
	 * Visit var array location accepting the index expression
	 */
	public List<String> visit(VarArrayLocation loc) {
		LinkedList<String> varArrayLocErrors = new LinkedList<String>();
		Expression expression = loc.getExpression();
		varArrayLocErrors.addAll(expression.accept(this));
		if (varArrayLocErrors.size()==0) {
			// There are no errors in the expression
			if (!loc.getExpression().getType().equals(Type.INT)) {
				// The expression representing the index is not int
				varArrayLocErrors.add(loc.getTypeIndexError());
			}
		}
		return varArrayLocErrors;
	}

	/**
	 * Visit method call checking the arguments 
	 */
	public List<String> visit(MethodCall call) {
		LinkedList<String> methodCallErrors = new LinkedList<String>();
		if (call.getArguments().size()==call.getDeclaration().getArguments().size()) {
			// The method declaration and the method call has the same arity, so
			// check the types
			int i=0;
			while (i < call.getArguments().size()) {
				Expression expression = call.getArguments().get(i);
				methodCallErrors.addAll(expression.accept(this));
				if (methodCallErrors.size()==0) {
					// There are no errors in the expression
					Argument arg = call.getDeclaration().getArguments().get(i);
					if (!expression.getType().equals(arg.getType())) {
						// Type error in argument
						methodCallErrors.add(arg.getArgumentTypeError(expression,i));
					}
				}
				i++;
			}
		} else {
			// The method declaration and the method call has different arity
			methodCallErrors.add(call.getArityErrorMessage());	
		}
		return methodCallErrors;
	}

	/**
	 * Visit block accepting each field declaration and each statement
	 */
	public List<String> visit(Block block) {
		LinkedList<String> blockErrors = new LinkedList<String>();
		for (FieldDeclaration fieldDeclaration : block.getFieldDeclarations()) {
			fieldDeclaration.accept(this);
		}
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
	 * Visit a print statement accepting the expression
	 */
	public List<String> visit(PrintStatement p) {
		return p.getExpression().accept(this);
	}
}