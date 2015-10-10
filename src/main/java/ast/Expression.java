/**
 * This class represents the expressions
 * @author Facundo Molina
 */
public abstract class Expression extends AST {
		 			
	protected Type type;					// Expression type

	/**
	 * Get the type
	 */
	public Type getType() {
		return this.type;
	}
	
	/**
	 * Set the type
	 */
	public void setType(Type t) {
		this.type = t;
	}

}