/**
 * Abstract visitor
 * @author Facundo Molina
 */
public interface ASTVisitor<T> {

	/* Visit program */
	T visit(Program p);

	/* Visit declarations */
	T visit(ClassDeclaration decl);
	T visit(FieldDeclaration decl);
	T visit(MethodDeclaration decl);
	T visit(Argument arg);
	T visit(DeclarationIdentifier ident);
 	
	/* Visit statements */
	T visit(AssignStatement stmt);
	T visit(MethodCallStatement stmt);
	T visit(ReturnStatement stmt);
	T visit(IfStatement stmt);
	T visit(ForStatement stmt);
	T visit(WhileStatement stmt);
	T visit(BreakStatement stmt);
	T visit(ContinueStatement stmt);
	T visit(SemicolonStatement stmt);
	T visit(PrintStatement stmt);
	
	/* Visit expressions */
	T visit(BinOpExpr expr);
	T visit(UnaryOpExpr expr);
	T visit(NullaryExpr expr);

	/* Visit literals */	
	T visit(IntLiteral lit);
	T visit(FloatLiteral lit);
	T visit(BooleanLiteral lit);

	/* Visit locations */
	T visit(VarLocation loc);
	T visit(VarArrayLocation loc);

	/* Visit method calls */
	T visit(MethodCall call);

	/* Visit bocks */
	T visit(Block block);

}