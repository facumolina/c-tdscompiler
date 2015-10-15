/**
 * This class represents a visitor that prints all the ast objects
 * @author Facundo Molina
 */
public class PrintVisitor implements ASTVisitor<String> {

	public PrintVisitor() {

	}

	/* Visit program */
	public String visit(Program p) {
		return (String)p.toString();
	}

	/* Visit declarations */
	public String visit(ClassDeclaration decl) {
		return (String)"";
	}


	public String visit(FieldDeclaration decl) {
		return (String)"";
	}

	public String visit(MethodDeclaration decl) {
		return (String)"";
	}

	public String visit(Argument arg) {
		return (String)"";
	}

	public String visit(DeclarationIdentifier ident) {
		return (String)"";
	}
 	
	/* Visit statements */
	public String visit(AssignStatement stmt) {
		return (String)"";
	}

	public String visit(MethodCallStatement stmt) {
		return (String)"";
	}

	public String visit(ReturnStatement stmt) {
		return (String)"";
	}

	public String visit(IfStatement stmt) {
		return (String)"";
	}	

	public String visit(ForStatement stmt) {
		return (String)"";
	}

	public String visit(WhileStatement stmt){
		return (String)"";
	}

	public String visit(BreakStatement stmt) {
		return (String)"";
	}

	public String visit(ContinueStatement stmt) {
		return (String)"";
	}
	
	/* Visit expressions */
	public String visit(BinOpExpr expr) {
		return (String)"";
	}

	public String visit(UnaryOpExpr expr) {
		return (String)"";
	}

	public String visit(NullaryExpr expr) {
		return (String)"";
	}

	/* Visit literals */	
	public String visit(IntLiteral lit) {
		return (String)"";
	}

	public String visit(FloatLiteral lit) {
		return (String)"";
	}

	public String visit(BooleanLiteral lit) {
		return (String)"";
	}

	/* Visit locations */
	public String visit(VarLocation loc) {
		return (String)"";
	}

	public String visit(VarArrayLocation loc) {
		return (String)"";
	}

	/* Visit method calls */
	public String visit(MethodCall call) {
		return (String)"";
	}

	/* Visit bocks */
	public String visit(Block block) {
		return (String)"";
	}

	/**
	 * Visit a semicolon statement
	 */
	public String visit(SemicolonStatement s) {
		return (String)"";
	}
	
	/**
	 * Visit a print statement
	 */
	public String visit(PrintStatement p) {
		return (String)"";
	}
	
}