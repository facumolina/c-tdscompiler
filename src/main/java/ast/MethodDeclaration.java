import java.util.List;

/**
 * This class represents the Methods declarations:
 *   - type id () body ;
 *   - type id (a1,a2,..,an) body ;
 */
public class MethodDeclaration extends Declaration {
	
	private String id;
	private List<Argument> arguments;
	private Block block;
	private boolean isExtern;

	public MethodDeclaration(Type t, String id, List<Argument> l,Block b) {
		this.id = id;
		type = t;
		arguments = l;
		if (b == null) {
			isExtern = true;
		} else {
			block = b;
			isExtern = false;
		}
	}

	public String getId() {
		return id;
	}

	public List<Argument> getArguments() {
		return arguments;
	}
 
	public Block getBlock() {
		return block;
	}

	public boolean isExtern() {
		return isExtern;
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