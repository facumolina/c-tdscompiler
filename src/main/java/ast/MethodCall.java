import java.util.List;
import java.util.LinkedList;

/**
 * This class represents the method calls as expresions: ID.ID .. .ID(e1,e2,..,en)
 */

public class MethodCall extends Expression {
	
	private String id; 							// First id
	private List<String> listIds;				// List of ids after the first dot
	private List<Expression> arguments; 		// Method arguments
	private MethodDeclaration decl;				// Declaration associated
	/*
	 * Constructor with simple name and no arguments: methodname()
	 */
	public MethodCall(String id,int line,int column) {
		this.id = id;
		listIds = new LinkedList<String>();
		arguments = new LinkedList<Expression>();
		decl = null;
		this.setLineNumber(line);
		this.setColumnNumber(column);
	}

	/**
	 * Constructor with extended name and no arguments: id.extension1.methodname()
	 */
	public MethodCall(String id, List<String> l,int line,int column) {
		this.id = id;
		this.listIds = l;
		arguments = new LinkedList<Expression>();
		this.setLineNumber(line);
		this.setColumnNumber(column);
	}


	/**
	 * Constructor with extended name and arguments: id.extension1.methodname(e1,e2,..,en)
	 */
	public MethodCall(String id, List<String> l, List<Expression> exprList,int line,int column) {
		this.id = id;
		listIds = l;
		arguments = exprList;
		this.setLineNumber(line);
		this.setColumnNumber(column);
	}

	/**
	 * Get the id.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Set new id.
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Get the list of ids after the first dot.
	 */
	public List<String> getListIds() {
		return listIds;
	}

	/**
	 * Set the list fo ids after the first dot.
	 */
	public void setListIds(List<String> l) {
		listIds = l;
	}

	/**
	 * Get the arguments of the method.
	 */
	public List<Expression> getArguments() {
		return arguments;
	}

	/**
	 * Set the arguments of the method.
	 */
	public void setArguments(List<Expression> exprList) {
		arguments = exprList;
	}

	/**
	 * Return true if is a local method call, otherwise return false 
	 */
	public boolean isLocalMethodCall() {
		return listIds.size()==0;
	}

	/**
	 * Get declaration 
	 */
	public MethodDeclaration getDeclaration() {
		return decl;
	}

	/**
	 * Set declaration
	 */
	public void setDeclaration(MethodDeclaration decl) {
		this.decl = decl;
		this.setType(decl.getType());
	}

	/**
	 * Get declaration error message
	 */
	public String getDeclarationErrorMessage() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Method Call Error: No exists a method declaration with name ";
		if (isLocalMethodCall()) {
			error += id;
		} else {
			error += listIds.get(0) + " in class " + id;
		}
		return lineAndColumn + error; 
	}

	/**
	 * Get arity error message
	 */
	public String getArityErrorMessage() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Method Call Error: the method was declared with " + decl.getArguments().size() 
				+ " arguments, but was invoked with " + arguments.size() + " arguments";
		return lineAndColumn + error; 
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