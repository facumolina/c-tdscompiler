/**
 * This class represents the identifiable ast nodes, that is those that have
 * an id.
 */
public abstract class Identifiable extends AST {

	protected String id;			// Identifier

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
	
}