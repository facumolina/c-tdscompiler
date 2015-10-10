/**
 * This class represents the Locations
 * @author Facundo Molina
 */
public abstract class Location extends Expression {
	
	protected String id; 					// Location id
	protected DeclarationIdentifier decl; 	// Declaration associated 
	
	/**
	 * Get the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Set the id
 	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get the field declaration
	 */
	public DeclarationIdentifier getDeclaration() {
		return decl;
	}

	/**
	 * Set the field declaration
	 */
	public void setDeclaration(DeclarationIdentifier decl) {
		this.decl = decl;
		setType(decl.getType());
	}
	
	/**
	 * Get type error message
	 */
	public String getTypeErrorMessage(Expression e) {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Type error: the location " + id + " has type " + decl.getType().toString()
				+ ", but the expression type is " + e.getType().toString();
		return lineAndColumn + error;
	}

}