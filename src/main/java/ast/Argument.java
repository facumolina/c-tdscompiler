/**
 * This class represents the arguments of a method.
 */

public class Argument extends AST {
	
	private Type type;
	private String id;

	public Argument(Type t, String id) {
		type = t;
		this.id = id;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type t) {
		type = t;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return type.toString() + " " + id;
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

}