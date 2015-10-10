/**
 * This class represents the Declaration identifiers id or id[int_literal]
 * @author Facundo Molina
 */
public class DeclarationIdentifier extends Identifiable {
	
	private Integer capacity; 		// Capacity used if the declaration identifier is the identifier of an array
	private Type type;				// Type stored 

	/**
	 * Constructor for declarations identifiers: id
	 */
	public DeclarationIdentifier(String id,int line, int column) {
		this.id = id;
		capacity = null;
		this.setLineNumber(line);
		this.setColumnNumber(column);
	}

	/**
	 * Constructor for declarations identifiers: id[n]
	 */
	public DeclarationIdentifier(String id,Integer n,int line, int column) {
		this.id = id;
		capacity = n;
		this.setLineNumber(line);
		this.setColumnNumber(column);
	}

	/**
	 * Get the capacity
	 */
	public Integer getCapacity() {
		return capacity;
	}

	/**
	 * Set capacity
	 */
	public void setCapacity(Integer n) {
		capacity = n;
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
	 * Return true if the declarations represents an array declaration
	 */
	public boolean isArrayDeclarationId() {
		return capacity != null;
	}

	/**
	 * Get declaration error message
	 */
	public String getDeclarationErrorMessage() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Declaration Error: already exists a field with id " + id;
		return lineAndColumn + error;
	}

	@Override
	public String toString() {
		if (isArrayDeclarationId()) {
			return id + "[" + capacity + "]" ;
		} else {
			return id;
		}
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

}