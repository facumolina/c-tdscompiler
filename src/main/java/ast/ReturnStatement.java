/**
 * This class represents the Return statement: return expr;
 */
public class ReturnStatement extends Statement {
	
	private Expression expression; 	// Return expression
	
	/**
	 * Constructor
	 */
	public ReturnStatement() {
		this.expression = null;
	}

	/**
	 * Constructor with an expression
	 */
	public ReturnStatement(Expression e,int line,int column) {
		this.expression = e;
		this.setLineNumber(line);
		this.setColumnNumber(column);
	}

	/**
	 * Get the expression
	 */
	public Expression getExpression() {
		return expression;
	}

	/**
	 * Set the expression
	 */
	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	
	/**
	 * Get type error message
	 */
	public String getTypeErrorMessage(MethodDeclaration m) {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Type error: the return has type " + expression.getType().toString() 
				+ ", but the declared type in the method is " + m.getType().toString();
		return lineAndColumn + error ;
	}

	/**
	 * Returns true if has an expression 
	 */
	public boolean hasExpression() {
		return expression!=null;
	}
	
	@Override
	public String toString() {
		if (expression == null) {
			return "return";
		}
		else {
			return "return " + expression;
		}
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

}