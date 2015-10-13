import java.util.List;
import java.util.LinkedList;

/**
 * This class represents the interpreter visitor for evaluate the CTds program
 * @author Facundo Molina
 */
public class InterpreterVisitor implements ASTVisitor<List<String>> {

	private SymbolsTable table; 					// Symbols table
	private LinkedList<String> errorsList;			// Errors

	private static final int class_level = 0;
	private static final int field_level = 1;
	private static final int method_level = 2;
	private static final int method_field_level = 3;

	/**
	 * Constructor
	 */
	public InterpreterVisitor() {
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
	 * Visit a class declaration accepting only the main method, if its founded.
	 */
	public List<String> visit(ClassDeclaration decl) {
	 	LinkedList<String> classErrors = new LinkedList<String>();
	 	/*table.incrementLevel();
		for (FieldDeclaration fieldDeclaration : decl.getFieldDeclarations()) {
			fieldDeclaration.accept(this);
		}
		table.incrementLevel();*/
		for (MethodDeclaration methodDeclaration : decl.getMethodDeclarations()) {
			classErrors.addAll(methodDeclaration.accept(this));
		}
		/*table.decrementLevel();
		table.decrementLevel();*/
		return classErrors;
	}

	/**
	 * Visit a field declaration 
	 */
	public List<String> visit(FieldDeclaration decl) {
		/*for (DeclarationIdentifier d : decl.getListIds()) {
			table.addSymbol(d);
		}*/
		return new LinkedList<String>();
	}

	/**
	 * Visit a method declaration 
	 */
	public List<String> visit(MethodDeclaration decl) {
		LinkedList<String> methodErrors = new LinkedList<String>();
		if (decl.getId().equals("main")) {
			//table.incrementLevel();
		 	methodErrors.addAll(decl.getBlock().accept(this));
			//table.decrementLevel();
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
	 * Visit an assign statement 
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
			} else {
				// The location is a var
				location.getDeclaration().setValue(expression.getValue());
			}
				
		}
		return assignErrors;
	}

	/**
	 * Visit a method call statement
	 */
	public List<String> visit(MethodCallStatement stmt) {
		return new LinkedList<String>();
	}

	/**
	 * Visit a return statement 
	 */
	public List<String> visit(ReturnStatement stmt) {
		return new LinkedList<String>();
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
	 * Visit an if statement 
	 */
	public List<String> visit(IfStatement stmt) {
		return new LinkedList<String>();
	}	

	/**
	 * Visit a for statement 
	 */
	public List<String> visit(ForStatement stmt) {
		return new LinkedList<String>();
	}

	/**
	 * Visit a while statement 
	 */
	public List<String> visit(WhileStatement stmt){
		return new LinkedList<String>();
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
		System.out.println("Expr: "+ expr.toString());
		System.out.println(expr.getLeftOperand().toString());
		System.out.println(expr.getRightOperand().toString());
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
		if (loc.isSimpleLocation()) {
			// The location is of the form ID. 
			if (loc.getDeclaration().getValue()==null) {
				varLocationError.add(loc.getInitializationError());
			}	
		} else {

		}
		return varLocationError;
	}

	/**
	 * Visit var array location
	 */
	public List<String> visit(VarArrayLocation loc) {
		return new LinkedList<String>();
	}

	/**
	 * Visit method call 
	 */
	public List<String> visit(MethodCall call) {
		return new LinkedList<String>();
	}

	/**
	 * Visit a block accepting only each statement 
	 */
	public List<String> visit(Block block) {
		LinkedList<String> blockErrors = new LinkedList<String>();
		/*table.incrementLevel();
		for (FieldDeclaration fieldDeclaration : block.getFieldDeclarations()) {
			fieldDeclaration.accept(this);
		}*/
		for (Statement statement : block.getStatements()) {
			System.out.println("Stmt: " + statement.toString());
			blockErrors.addAll(statement.accept(this));
		}
		/*table.decrementLevel();*/
		return blockErrors;
	}

	/**
	 * Visit a semicolon statement
	 */
	public List<String> visit(SemicolonStatement s) {
		return new LinkedList<String>();
	}
	
	/**
	 * Search a declaration in the symbols table for a given location type (simple
	 * or extended). Return null if it not exists.
	 */
	private Identifiable searchDeclaration(String id,boolean isSimple,String secondId, boolean isField) {
		Identifiable decl = null;
		if (isSimple) {
			// The declaration searched only has one id.
			if (isField) {
				if(table.getLevel()>method_field_level) {
					// There are some blocks
					int levelToSearch = table.getLevel();
					while ((decl==null) && levelToSearch>method_field_level) {
						decl = table.searchSymbol(id,levelToSearch);
						levelToSearch--;
					}
				}
				if (decl==null) {
					// Search the id in the method field declarations or in the class field declarations
					decl = table.searchSymbol(id,method_field_level);
					if (decl == null) {
						decl = table.searchSymbol(id,field_level);
					}
				}
				
			} else {
				// Search the method
				decl = table.searchSymbol(id,method_level);
			}
		} else {
			// The declaration searched has more than one id.
			ClassDeclaration classDecl = (ClassDeclaration)table.searchSymbol(id,class_level);
			if (classDecl != null) {
				if (isField) {
					// Is a field
					for (FieldDeclaration field : classDecl.getFieldDeclarations() ) {
						for (DeclarationIdentifier declaration : field.getListIds()) {
							if (declaration.getId().equals(secondId)) {
								decl = declaration;
								break;
							}
						}	
					}
				} else {
					// Is a method
					for (MethodDeclaration method : classDecl.getMethodDeclarations() ) {
						if (method.getId().equals(secondId)) {
							decl = method;
						}
					}
				}
			}	
		}
		return decl;
	}
}