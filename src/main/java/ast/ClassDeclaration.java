import java.util.List;
import java.util.LinkedList;

/**
 * This class represents the Class declarations: class ID { ... }
 */
public class ClassDeclaration extends Identifiable {
	
	private List<FieldDeclaration> fieldDeclarations; 		// List of field declarations
	private List<MethodDeclaration> methodDeclarations;		// List of method declarations

	/**
	 * Constructor with a given field declaration list.
	 */
	public ClassDeclaration(String id,List<FieldDeclaration> fl,int line,int column) {
		this.id = id;
		fieldDeclarations = fl;
		methodDeclarations = new LinkedList<MethodDeclaration>();
		this.setLineNumber(line);
		this.setColumnNumber(column);
		hasId = true;
	}
	
	/**
	 * Constructor with a given field declaration list and method declaration list. 
	 */
	public ClassDeclaration(String id,List<FieldDeclaration> fl, List<MethodDeclaration> ml,int line,int column) {
		this.id = id;
		fieldDeclarations = fl;
		methodDeclarations = ml;
		this.setLineNumber(line);
		this.setColumnNumber(column);
	}

	/**
	 * Get the field declarations
	 */
	public List<FieldDeclaration> getFieldDeclarations() {
		return fieldDeclarations;
	}

	/**
	 * Set the field declarations
	 */
	public void setFieldDeclarations(List<FieldDeclaration> fl) {
		fieldDeclarations = fl;
	}

	/**
	 * Get the method declarations
	 */
	public List<MethodDeclaration> getMethodDeclarations() {
		return methodDeclarations;
	}

	/**
	 * Set the method declarations
	 */
	public void setMethodDeclarations(List<MethodDeclaration> ml) {
		methodDeclarations = ml;
	}
	
	/**
	 * Get declaration error message
	 */
	public String getDeclarationErrorMessage() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Declaration Error: already exists a class with name " + id;
		return lineAndColumn + error;
	}

	@Override
	public String toString() {
		String classString = "class " + id + " { \n\n";
		for (FieldDeclaration fieldDeclaration : fieldDeclarations) {
			classString += "\t" + fieldDeclaration.toString() + "\n";
		}
		classString += "\n";
		for (MethodDeclaration methodDeclaration : methodDeclarations) {
			classString += "\t" + methodDeclaration.toString() + "\n";
		}
		classString += "\n";
		classString += "}";
		return classString;
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

}