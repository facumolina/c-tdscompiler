
// Abstract visitor
public interface ASTVisitor<T> {

// visit declarations
	T visit(ClassDeclaration decl);
	T visit(FieldDeclaration decl);
	T visit(MethodDeclaration decl);
	T visit(Argument arg);
	
// visit statements
	T visit(AssignStatement stmt);
	T visit(ReturnStmt stmt);
	T visit(IfStatement stmt);
	
// visit expressions
	T visit(BinOpExpr expr);
	T visit(UnaryOpExpr expr);
	T visit(NullaryExpr expr);

// visit literals	
	T visit(IntLiteral lit);
	T visit(FloatLiteral lit);
	T visit(BooleanLiteral lit);

// visit locations	
	T visit(VarLocation loc);
	T visit(VarArrayLocation loc);

// visit method calls
	T visit(MethodCall call);

// visit bocks
	T visit(Block block);
}
