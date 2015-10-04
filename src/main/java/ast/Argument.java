/**
 * This class represents the arguments of a method in the declaration
 */
public class Argument extends Identifiable {
	
	private Type type; 					// Argument type

	/**
	 * Contructor with a given type and id
	 */
	public Argument(Type t, String id) {
		type = t;
		this.id = id;
		hasId = true;
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

	@Override
	public String toString() {
		return type.toString() + " " + id;
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

	@Override
	public boolean hasId() {
		return true;
	}

}