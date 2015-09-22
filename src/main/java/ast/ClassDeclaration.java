import java.util.List;
import java.util.LinkedList;

/**
 * This class represents the Class declarations: class ID { ... }
 */

public class ClassDeclaration extends AST {
	
	private List<FieldDeclaration> fieldDeclarations;
	private List<MethodDeclaration> methodDeclarations;

	public ClassDeclaration() {
		fieldDeclarations = new LinkedList<FieldDeclaration>();
		methodDeclarations = new LinkedList<MethodDeclaration>();
	}

	public ClassDeclaration(List<FieldDeclaration> fl, List<MethodDeclaration> ml) {
		fieldDeclarations = fl;
		methodDeclarations = ml;
	}

	public List<FieldDeclaration> getFieldDeclarations() {
		return fieldDeclarations;
	}

	public List<MethodDeclaration> getMethodDeclarations() {
		return methodDeclarations;
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

}