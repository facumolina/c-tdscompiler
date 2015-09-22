import java.util.List;
import java.util.LinkedList;

/**
 * This class represents the method calls as expresions: ID.ID .. .ID(e1,e2,..,en)
 */

public class MethodCall extends Expression {
	
	private String id;
	private List<String> listIds;
	private List<Expression> arguments;

	/*
	 * Constructor for method calls with simple name and no arguments: methodname()
	 */
	public MethodCall(String id) {
		this.id = id;
		listIds = new LinkedList<String>();
		arguments = new LinkedList<Expression>();
	}

	/**
	 * Constructor for method calls with extended name and no arguments: methodname.extension1.extension2()
	 */
	public MethodCall(String id, List<String> l) {
		this.id = id;
		this.listIds = l;
		arguments = new LinkedList<Expression>();
	}


	/**
	 * Constructor for method calls with extended name and arguments: methodname.extension1.extension2(e1,e2,..,en)
	 */
	public MethodCall(String id, List<String> l, List<Expression> exprList) {
		this.id = id;
		listIds = l;
		arguments = exprList;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public List<String> getListIds() {
		return listIds;
	}

	public void setListIds(List<String> l) {
		listIds = l;
	}

	public List<Expression> getArguments() {
		return arguments;
	}

	public void setArguments(List<Expression> exprList) {
		arguments = exprList;
	}

	@Override
	public String toString() {
		String methodCallString = id;
		for (String s : listIds) {
			methodCallString += "." + s;
		}
		methodCallString += "(";
		for (int i = 0 ; i < arguments.size() ; i++) {			
			methodCallString += arguments.get(i).toString();
			if (i < arguments.size()-1) {
				methodCallString += ",";
			}
		}
		methodCallString += ")";
		return methodCallString;
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

}