/**
 * This class represents the Declarations:
 *  - Field declarations
 *  - Methods declarations
 *
 */
public abstract class Declaration extends AST {

	protected Type type;

	public Type getType() {
		return type;
	}

	public void setType(Type t) {
		type = t;
	}
	
}