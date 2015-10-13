/**
 * This class represents the expressions
 * @author Facundo Molina
 */
public abstract class Expression extends AST {
		 			
	protected Type type;					// Expression type
	protected Literal value;				// Expression value
	
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

	/**
	 * Get the value
	 */
	public Literal getValue() {
		return value;
	}

	/**
	 * Set the value
	 */
	public void setValue(Literal value) {
		this.value = value;
	}

}