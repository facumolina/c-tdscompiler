/**
 * This class represents a visitor that check the amount of main methods.
 */
public class CheckMainVisitor implements ASTVisitor<Integer> {

	public CheckMainVisitor() {

	}

	/* Visit program */
	public Integer visit(Program p) {
		Integer amountOfMains = 0;
		for (ClassDeclaration classDeclaration : p.getClassDeclarations()) {
			amountOfMains += classDeclaration.accept(this);
		}
		return amountOfMains;
	}

	/* Visit declarations */
	public Integer visit(ClassDeclaration decl) {
		Integer amountOfMains = 0;
		for (MethodDeclaration methodDeclaration : decl.getMethodDeclarations()) {
			amountOfMains += methodDeclaration.accept(this);
		}
		return amountOfMains;
	}


	public Integer visit(FieldDeclaration decl) {
		return 0;
	}

	public Integer visit(MethodDeclaration decl) {
		if (decl.getId().equals("main")) {
			return 1;
		} else {
			return 0;
		}
	}

	public Integer visit(Argument arg) {
		return 0;
	}

	public Integer visit(DeclarationIdentifier ident) {
		return 0;
	}
 	
	/* Visit statements */
	public Integer visit(AssignStatement stmt) {
		return 0;
	}

	public Integer visit(MethodCallStatement stmt) {
		return 0;
	}

	public Integer visit(ReturnStatement stmt) {
		return 0;
	}

	public Integer visit(IfStatement stmt) {
		return 0;
	}	

	public Integer visit(ForStatement stmt) {
		return 0;
	}

	public Integer visit(WhileStatement stmt){
		return 0;
	}

	public Integer visit(BreakStatement stmt) {
		return 0;
	}

	public Integer visit(ContinueStatement stmt) {
		return 0;
	}
	
	/* Visit expressions */
	public Integer visit(BinOpExpr expr) {
		return 0;
	}

	public Integer visit(UnaryOpExpr expr) {
		return 0;
	}

	public Integer visit(NullaryExpr expr) {
		return 0;
	}

	/* Visit literals */	
	public Integer visit(IntLiteral lit) {
		return 0;
	}

	public Integer visit(FloatLiteral lit) {
		return 0;
	}

	public Integer visit(BooleanLiteral lit) {
		return 0;
	}

	/* Visit locations */
	public Integer visit(VarLocation loc) {
		return 0;
	}

	public Integer visit(VarArrayLocation loc) {
		return 0;
	}

	/* Visit method calls */
	public Integer visit(MethodCall call) {
		return 0;
	}

	/* Visit bocks */
	public Integer visit(Block block) {
		return 0;
	}

}