import java.util.List;
import java.util.LinkedList;

/**
 * This class implements a visitor that check the declaration names and
 * fill the information needed for type evaluation. 
 */
public class CheckDeclarationVisitor implements ASTVisitor<List<String>> {

	private SymbolsTable table; 				// Symbols table

	private static final int class_level = 0;
	private static final int field_level = 1;
	private static final int method_level = 2;
	private static final int method_field_level = 3;

	/**
	 * Constructor
	 */
	public CheckDeclarationVisitor() {
		table = new SymbolsTable();
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
			if (table.addSymbol(d)) {
				// The symbol was added successfully. Set the type for type check purposes.
				d.setType(decl.getType()); 
			} else {
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
		System.out.println("Accepting method decl " + decl.getId() + " with level " + table.getLevel());
		LinkedList<String> methodErrors = new LinkedList<String>();
		if (table.addSymbol(decl)) {
			// The symbol was added successfully. 
			if (!decl.getType().equals(Type.VOID)) {
				// Method type is not void
				if (!decl.hasReturnStatement()) {
					// The method does not have a return statement
					methodErrors.add(decl.getMissingReturnStatementError());
				} 
			} else {
				// Method type is void
				if (decl.hasReturnStatement()) {
					methodErrors.add(decl.getIncorrectReturnStatementError());				
				}
			}
			table.incrementLevel();
			methodErrors.addAll(decl.getBlock().accept(this));
			table.decrementLevel();
		} else {
			// The symbol already exists
			methodErrors.add(decl.getDeclarationErrorMessage());
		}
		return methodErrors;
	}

	/**
	 * Visit argument
	 */
	public List<String> visit(Argument arg) {
		return new LinkedList<String>();
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
		LinkedList<String> forStmtErrors = new LinkedList<String>();
		forStmtErrors.addAll(stmt.getInitialAssign().accept(this));
		forStmtErrors.addAll(stmt.getConditionExpression().accept(this));
		forStmtErrors.addAll(stmt.getBlock().accept(this));
		return forStmtErrors;
	}

	/**
 	 * Visit a while statement and accepts the condition and the block.
	 */
	public List<String> visit(WhileStatement stmt){
		LinkedList<String> whileStmtErrors = new LinkedList<String>();
		whileStmtErrors.addAll(stmt.getCondition().accept(this));
		whileStmtErrors.addAll(stmt.getBlock().accept(this));
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
			// as field in the current method or as field declaration of the 
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
				// Search the id in the method field declarations or in the class field declarations
				decl = table.searchSymbol(id,method_field_level);
				if (decl == null) {
					decl = table.searchSymbol(id,field_level);
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
		for (FieldDeclaration fieldDeclaration : block.getFieldDeclarations()) {
			// Accept each field declaration
			blockErrors.addAll(fieldDeclaration.accept(this));
		}
		for (Statement statement : block.getStatements()) {
			// Accept each statement
			blockErrors.addAll(statement.accept(this));
		}
		return blockErrors;
	}

}