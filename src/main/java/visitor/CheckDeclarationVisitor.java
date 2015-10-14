import java.util.List;
import java.util.LinkedList;

/**
 * This class implements a visitor that check the declaration names and
 * fill the information needed for type evaluation. 
 * @author Facundo Molina
 */
public class CheckDeclarationVisitor implements ASTVisitor<List<String>> {

	private SymbolsTable table; 				// Symbols table

	private static final int class_level = 0;
	private static final int field_level = 1;
	private static final int method_level = 2;
	private static final int method_field_level = 3;

	private static int inCycle;				// Increment every time that a cycle begins
											// and decrement every time that a cycle ends
	/**
	 * Constructor
	 */
	public CheckDeclarationVisitor() {
		table = new SymbolsTable();
		inCycle = 0;
	}

	/**
	 * Visit a program accepting each class
	 */
	public List<String> visit(Program p) {
	 	LinkedList<String> errorsList = new LinkedList<String>();
		for (ClassDeclaration classDeclaration : p.getClassDeclarations()) {
			errorsList.addAll(classDeclaration.accept(this));
		}
		return errorsList;
	}

	/**
	 * Visit a class accepting it if it not exists in the symbols table, and accepting 
	 * the field declarations and the method declarations
	 */
	public List<String> visit(ClassDeclaration decl) {
		LinkedList<String> classErrors = new LinkedList<String>();
		if (table.addSymbol(decl)) {
			// The symbol was added successfully
			table.incrementLevel();
			for (FieldDeclaration fieldDeclaration : decl.getFieldDeclarations()) {
				// Accept each field of the class
				classErrors.addAll(fieldDeclaration.accept(this));
			}
			table.incrementLevel();
			for (MethodDeclaration methodDeclaration : decl.getMethodDeclarations()) {
				// Accept each method of the class
				classErrors.addAll(methodDeclaration.accept(this));
			}
			table.decrementLevel();
			table.decrementLevel();
		} else {
			// The symbol already exists
			classErrors.add(decl.getDeclarationErrorMessage());
		}
		return classErrors;
	}

	/**
	 * Visit a field accepting it if it not exists in the symbols table.
	 */
	public List<String> visit(FieldDeclaration decl) {
		LinkedList<String> fieldErrors = new LinkedList<String>();
		for (DeclarationIdentifier d : decl.getListIds()) {
			if (d.isArrayDeclarationId()) {
				// Is an array declaration. 
				if (d.getCapacity()<=0) {
					// The array initial size must be greater than 0
					fieldErrors.add(d.getArrayDeclarationErrorMessage());
				}
			}
			if (!table.addSymbol(d)) {
				// The symbol already exists.
				fieldErrors.add(d.getDeclarationErrorMessage());
			}
		}
		return fieldErrors;
	}

	/**
	 * Visit a method accepting it if it not exists in the symbols table, 
	 * checking that have a return statement if the type is not void 
	 * and accepting the block.
	 */
	public List<String> visit(MethodDeclaration decl) {
		LinkedList<String> methodErrors = new LinkedList<String>();
		if (table.addSymbol(decl)) {
			// The symbol was added successfully. 
			if (!decl.isExtern()) {
				if (!decl.hasReturnStatement()) {
						// The method does not have a return statement
						methodErrors.add(decl.getMissingReturnStatementError());
				} 
				table.incrementLevel();
				for (Argument arg : decl.getArguments()) {
					methodErrors.addAll(arg.accept(this));
				}
				methodErrors.addAll(decl.getBlock().accept(this));
				table.decrementLevel();
			}
		} else {
			// The symbol already exists
			methodErrors.add(decl.getDeclarationErrorMessage());
		}
		return methodErrors;
	}

	/**
	 * Visit argument accepting it if it not exists in the symbols table
	 */
	public List<String> visit(Argument arg) {
		LinkedList<String> argErrors = new LinkedList<String>();
		DeclarationIdentifier argDeclaration = new DeclarationIdentifier(arg.getId(),arg.getLineNumber(),arg.getColumnNumber());
		argDeclaration.setType(arg.getType());
		arg.setDeclaration(argDeclaration);
		if (!table.addSymbol(argDeclaration)) {
			// Already exists an argument with the same id
			argErrors.add(arg.getDeclarationErrorMessage());
		}
		return argErrors;
	}

	/**
	 * Visit declaration identifier
	 */
	public List<String> visit(DeclarationIdentifier ident) {
		return new LinkedList<String>();
	}
 	
	/**
	 * Visit an assign statement accepting the location and the expression.
	 */
	public List<String> visit(AssignStatement stmt) {
		LinkedList<String> assignError = new LinkedList<String>();
		assignError.addAll(stmt.getLocation().accept(this));
		assignError.addAll(stmt.getExpression().accept(this));
		return assignError;
	}

	/**
	 * Visit a method call statement accepting the method call.
	 */
	public List<String> visit(MethodCallStatement stmt) {
		return stmt.getMethodCall().accept(this);
	}

	/**
	 * Visit a return statement accepting the expression if it has one.
	 */
	public List<String> visit(ReturnStatement stmt) {
		if (stmt.hasExpression()) {
			return stmt.getExpression().accept(this);
		} else {
			return new LinkedList<String>();
		}
	}

	/**
	 * Visit an if statement and accepting the condition and the blocks.
	 */
	public List<String> visit(IfStatement stmt) {
		LinkedList<String> ifStmtErrors = new LinkedList<String>();
		ifStmtErrors.addAll(stmt.getCondition().accept(this));
		ifStmtErrors.addAll(stmt.getIfBlock().accept(this));
		if (stmt.getElseBlock()!=null) {
			ifStmtErrors.addAll(stmt.getElseBlock().accept(this));
		}
 		return ifStmtErrors;
	}	

	/**
	 * Visit a for statement and accepts the initial expression, the condition 
	 * expression and the block
	 */
	public List<String> visit(ForStatement stmt) {
		inCycle++;
		LinkedList<String> forStmtErrors = new LinkedList<String>();
		forStmtErrors.addAll(stmt.getInitialAssign().accept(this));
		forStmtErrors.addAll(stmt.getConditionExpression().accept(this));
		forStmtErrors.addAll(stmt.getBlock().accept(this));
		inCycle--;
		return forStmtErrors;
	}

	/**
 	 * Visit a while statement and accepts the condition and the block.
	 */
	public List<String> visit(WhileStatement stmt){
		inCycle++;
		LinkedList<String> whileStmtErrors = new LinkedList<String>();
		whileStmtErrors.addAll(stmt.getCondition().accept(this));
		whileStmtErrors.addAll(stmt.getBlock().accept(this));
		inCycle--;
		return whileStmtErrors;
	}

	/**
	 * Visit break statement accepting it only if it appears in a cycle
	 */
	public List<String> visit(BreakStatement stmt) {
		LinkedList<String> breakError = new LinkedList<String>();
		if (inCycle==0) {
			// The statement is outside of a cycle
			breakError.add(stmt.getOutsideOfCycleError());
		}
		return breakError;
	}

	/**
	 * Visit continue statement accepting it only if it appears in a cycle
	 */
	public List<String> visit(ContinueStatement stmt) {
		LinkedList<String> continueError = new LinkedList<String>();
		if (inCycle==0) {
			// The statement is outside of a cycle
			continueError.add(stmt.getOutsideOfCycleError());
		}
		return continueError;
	}
	
	/**
	 * Visit binary expression accepting the left and right expressions
	 */
	public List<String> visit(BinOpExpr expr) {
		LinkedList<String> errorBinExpr = new LinkedList<String>();
		errorBinExpr.addAll(expr.getLeftOperand().accept(this));
		errorBinExpr.addAll(expr.getRightOperand().accept(this));
		return errorBinExpr;
	}

	/**
	 * Visit unary expression accepting the expression.
	 */
	public List<String> visit(UnaryOpExpr expr) {
		return expr.getOperand().accept(this);
	}

	/**
	 * Visit nullary expression accepting the expression
	 */
	public List<String> visit(NullaryExpr expr) {
		return expr.getExpression().accept(this);
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
	 * Visit a var location and accepts it if it is already declared
	 * and fill the type for type check purposes.
	 */
	public List<String> visit(VarLocation loc) {
		LinkedList<String> varLocationError = new LinkedList<String>();
		if (loc.isSimpleLocation()) {
			// The location is of the form ID. So the ID must be declared 
			// int the current or previous blocks, or as field declaration of the 
			// current class.
			DeclarationIdentifier decl = (DeclarationIdentifier)searchDeclaration(loc.getId(),true,"",true);
			boolean error = false;
			if (decl != null) {
				// The field was founded. So add it to the location if is not an array declaration
				if (!decl.isArrayDeclarationId()) {
					loc.setDeclaration(decl);	
				} else {
					error = true;
				}
			} else {
				// The field declaration of the current location does not exist.
				error = true;
			}
			if (error) {
				varLocationError.add(loc.getNoDeclarationErrorMessage());
			}
		} else {
			// The location is of the form ID.ID . So the first ID
			// must be the name of an already defined class and the second ID
			// a field declaration of that class.	
			DeclarationIdentifier decl = (DeclarationIdentifier)searchDeclaration(loc.getId(),false,loc.getListIds().get(0),true);
			boolean error = false;
			if (decl!=null) {
				// The field declaration was founded. So add it to the location if is not an array declaration
				if (!decl.isArrayDeclarationId()) {
					loc.setDeclaration(decl);	
				} else {
					error = true;
				}
			} else {
				// The field declaration of the current location does not exist.
				error = true;
			}
			if (error) {
				varLocationError.add(loc.getNoDeclarationOtherClassErrorMessage());
			}
		}
		return varLocationError;
	}

	/**
	 * Visit a var array location and accepts it if it is already declared.
	 */
	public List<String> visit(VarArrayLocation loc) {
		LinkedList<String> varArrayLocationError = new LinkedList<String>();
		if (loc.isSimpleLocation()) {
			// The location is of the form ID[expr]. So the ID must be declared 
			// as field in the current method or as field declaration of the 
			// current class.
			DeclarationIdentifier decl = (DeclarationIdentifier)searchDeclaration(loc.getId(),true,"",true);
			boolean error = false;
			if (decl != null) {
				// The field was founded. So add it to the location if it is an array declaration
				if (decl.isArrayDeclarationId()) {
					loc.setDeclaration(decl);
					varArrayLocationError.addAll(loc.getExpression().accept(this));	
				} else {
					error = true;
				}
			} else {
				// The field declaration of the current location does not exist.
				error = true;
			}
			if (error) {
				varArrayLocationError.add(loc.getNoDeclarationErrorMessage());
			}
		} else {
			// The location is of the form ID.ID[expr] So the first ID
			// must be the name of an already defined class and the second ID
			// a field declaration of that class.	
			DeclarationIdentifier decl = (DeclarationIdentifier)searchDeclaration(loc.getId(),false,loc.getListIds().get(0),true);
			boolean error = false;
			if (decl!=null) {
				// The field declaration was founded. So add it to the location if is an array declaration
				if (decl.isArrayDeclarationId()) {
					loc.setDeclaration(decl);
					varArrayLocationError.addAll(loc.getExpression().accept(this));	
				} else {
					error = true;
				}
			} else {
				// The field declaration of the current location does not exist.
				error = true;
			}
			if (error) {
				varArrayLocationError.add(loc.getNoDeclarationOtherClassErrorMessage());
			}
		}
		return varArrayLocationError;
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

	/**
	 * Visit a method call and accepts it if it is already declared.
	 */
	public List<String> visit(MethodCall call) {
		LinkedList<String> methodCallError = new LinkedList<String>();
		MethodDeclaration decl = null;
		if (call.isLocalMethodCall()) {
			// Is a local method call, so the id must be a method declaration in the 
			// current class
			decl = (MethodDeclaration)searchDeclaration(call.getId(),true,"",false);
		} else {
			// Is a method of other class, so that class must exist and have such method.
			decl = (MethodDeclaration)searchDeclaration(call.getId(),false,call.getListIds().get(0),false);
		}
		if (decl!=null) {
			// The method declaration was founded.
			call.setDeclaration(decl);
			for (Expression e : call.getArguments()) {
				methodCallError.addAll(e.accept(this));
			}
		} else {
			methodCallError.add(call.getDeclarationErrorMessage());
		}
		return methodCallError;
	}

	/**
	 * Visit a block accepting the field declarations and statement declarations
	 */
	public List<String> visit(Block block) {
		LinkedList<String> blockErrors = new LinkedList<String>();
		table.incrementLevel();
		for (FieldDeclaration fieldDeclaration : block.getFieldDeclarations()) {
			// Accept each field declaration
			blockErrors.addAll(fieldDeclaration.accept(this));
		}
		for (Statement statement : block.getStatements()) {
			// Accept each statement
			blockErrors.addAll(statement.accept(this));
		}
		table.decrementLevel();
		return blockErrors;
	}

	/**
	 * Visit a semicolon statement
	 */
	public List<String> visit(SemicolonStatement s) {
		return new LinkedList<String>();
	}

}