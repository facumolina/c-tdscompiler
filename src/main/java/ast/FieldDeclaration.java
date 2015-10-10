import java.util.List;

/**
 * This class represents the Field declarations: 
 *   - type id ;
 *   - type id [ intliteral ] ;
 * @author Facundo Molina
 */
public class FieldDeclaration extends AST {

	private List<DeclarationIdentifier> identifiersList;	// Identifiers list
	private Type type; 										// Type

	/**
	 * Constructor with a given type and list of identifiers.
	 */
	public FieldDeclaration(Type t,List<DeclarationIdentifier> l) {
		type = t;
		identifiersList = l;
		for (DeclarationIdentifier d : identifiersList) {
			d.setType(t);
		}
	}

	/**
	 * Get the list of identifiers.
	 */
	public List<DeclarationIdentifier> getListIds() {
		return identifiersList;
	}

	/**
	 * Get type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Set type
	 */
	public void setType(Type t) {
		type = t;
	}

	@Override
	public String toString() {
		String fieldString = type.toString() + " ";
		for (DeclarationIdentifier decl : identifiersList) {
			fieldString += decl.toString() + " ";
		}
		fieldString += ";";
		return fieldString;
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

}