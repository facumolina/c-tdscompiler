/**
 * This class represents a visitor that check the amount of main methods.
 * @author Facundo Molina
 */
public class CheckMainVisitor implements ASTVisitor<Integer> {

	/**
	 * Constructor
	 */
	public CheckMainVisitor() {

	}

	/**
	 * Visit a program accepting each class
 	 */
	public Integer visit(Program p) {
		Integer amountOfMains = 0;
		for (ClassDeclaration classDeclaration : p.getClassDeclarations()) {
			amountOfMains += classDeclaration.accept(this);
		}
		return amountOfMains;
	}

	/**
	 * Visit a class accepting each method declaration. 
	 */
	public Integer visit(ClassDeclaration decl) {
		Integer amountOfMains = 0;
		for (MethodDeclaration methodDeclaration : decl.getMethodDeclarations()) {
			amountOfMains += methodDeclaration.accept(this);
		}
		return amountOfMains;
	}

	/**
	 * Visit a field
	 */
	public Integer visit(FieldDeclaration decl) {
		return 0;
	}

	/**
	 * Visit a method declaration
	 */
	public Integer visit(MethodDeclaration decl) {
		if (decl.getId().equals("main")) {
			// The method name is main
			if (decl.getArguments().size()==0){
				// The method does not have arguments
				return 1;
			} else {
				// The method have arguments
				return 0;
			}
		} else {
			return 0;
		}
	}

	/**
	 * Visit an argument
	 */
	public Integer visit(Argument arg) {
		return 0;
	}
 
	/**
	 * Visit a declaration identifier
	 */
	public Integer visit(DeclarationIdentifier ident) {
		return 0;
	}
 	
	/**
	 * Visit an assignment statement
	 */
	public Integer visit(AssignStatement stmt) {
		return 0;
	}

	/**
	 * Visit a method call statement
	 */
	public Integer visit(MethodCallStatement stmt) {
		return 0;
	}

	/**
	 * Visit a return statement
	 */
	public Integer visit(ReturnStatement stmt) {
		return 0;
	}

	/**
	 * Visit an if statement
	 */
	public Integer visit(IfStatement stmt) {
		return 0;
	}	

	/**
	 * Visit a for statement
	 */
	public Integer visit(ForStatement stmt) {
		return 0;
	}

	/**
	 * Visit a while statement
	 */
	public Integer visit(WhileStatement stmt){
		return 0;
	}

	/**
	 * Visit a break statement
	 */
	public Integer visit(BreakStatement stmt) {
		return 0;
	}

	/**
	 * Visit a continue statement
	 */
	public Integer visit(ContinueStatement stmt) {
		return 0;
	}
	
	/**
	 * Visit an expression
	 */
	public Integer visit(BinOpExpr expr) {
		return 0;
	}

	/**
	 * Visit an unary expression
	 */
	public Integer visit(UnaryOpExpr expr) {
		return 0;
	}

	/**
	 * Visit a nullary expression 
	 */
	public Integer visit(NullaryExpr expr) {
		return 0;
	}

	/**
	 * Visit an int literal
	 */	
	public Integer visit(IntLiteral lit) {
		return 0;
	}

	/**
	 * Visit a float literal
	 */
	public Integer visit(FloatLiteral lit) {
		return 0;
	}

	/**
	 * Visit a boolean literal
	 */
	public Integer visit(BooleanLiteral lit) {
		return 0;
	}

	/**
	 * Visit a var location
	 */
	public Integer visit(VarLocation loc) {
		return 0;
	}

	/**
	 * Visit a var array location
	 */
	public Integer visit(VarArrayLocation loc) {
		return 0;
	}

	/**
	 * Visit a method call
	 */
	public Integer visit(MethodCall call) {
		return 0;
	}

	/**
	 * Visit a block
	 */
	public Integer visit(Block block) {
		return 0;
	}

	/**
	 * Visit a semicolon statement
	 */
	public Integer visit(SemicolonStatement s) {
		return 0;
	}

}