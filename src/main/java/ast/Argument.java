/**
 * This class represents the arguments of a method in the declaration
 * @author Facundo Molina
 */
public class Argument extends Identifiable {
	
	private Type type; 					// Argument type
	private DeclarationIdentifier decl; // Declaration associated in the symbols table
	
	/**
	 * Contructor with a given type and id
	 */
	public Argument(Type t, String id,int line,int column) {
		type = t;
		this.id = id;
		this.setLineNumber(line);
		this.setColumnNumber(column);
	}
	
	/**
	 * Get the type 
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Set the type 
	 */
	public void setType(Type t) {
		type = t;
	}

	/**
	* Get declaration identifier
	*/
	public DeclarationIdentifier getDeclaration() {
		return decl;
	}

	/**
	 * Set declaration identifier
	 */
	public void setDeclaration(DeclarationIdentifier decl) {
		this.decl = decl;
	}

	/**
	 * Get declaration error message
	 */
	public String getDeclarationErrorMessage() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Declaration Error: already exists an argument with id " + id;
		return lineAndColumn + error;
	}

	/**
	 * Get argument type error 
	 */
	public String getArgumentTypeError(Expression e,int position) {
		String lineAndColumn = e.getLineNumber() + ":" + e.getColumnNumber() + ": " ;
		String error = "Type Error: the expression at position " + position + " has type "
				+ e.getType().toString() + " but the argument in the method declaration has type " +  
				type.toString();
		return lineAndColumn + error;
	}

	@Override
	public String toString() {
		return type.toString() + " " + id;
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

}