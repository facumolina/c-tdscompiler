import java.util.List;

/**
 * This class represents the Field declarations: 
 *   - type id ;
 *   - type id [ intliteral ] ;
 */
public class FieldDeclaration extends Declaration {

	private List<String> listIds;

	public FieldDeclaration(Type t,List<String> l) {
		type = t;
		listIds = l;
	}

	public List<String> getListIds() {
		return listIds;
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

}