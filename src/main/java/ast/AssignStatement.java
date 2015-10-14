/**
 * This class represents the assignment statements: location assignment_op expr
 * @author Facundo Molina
 */
public class AssignStatement extends Statement {
	
	private Location location; 			// Location 
	private Expression expression; 		// Expression assigned to location
	private AssignOpType operator; 		// Assignment operator

	/**
	 * Constructor with a given location, assignment operator and an expression.
	 */
	public AssignStatement(Location loc, AssignOpType op, Expression e,int line, int column) {
		location = loc;
		expression = e;
		operator = op;
		setLineNumber(line);
		setColumnNumber(column);
	}
	
	/**
	 * Constructor
	 */
	public AssignStatement(Location loc, AssignOpType op, Expression e) {
		location = loc;
		expression = e;
		operator = op;
	}

	/**
	 * Get the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Set the location
	 */
	public void setLocation(Location loc) {
		location = loc;
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
	public void setExpression(Expression e) {
		expression = e;
	}
	
	/**
	 * Get the operator
	 */
	public AssignOpType getOperator() {
		return operator;
	}

	/**
	 * Set the operator
	 */
	public void setOperator(AssignOpType op) {
		operator = op;
	}
	
	/**
	 * Assuming that the left and right operartors has the same type,
	 * returns true if that type is compatible with the operator
	 */
	public boolean compatibleOperator() {
		return operator.isValidForType(expression.getType());
	}

	/**
	 * Get incompatible type error message
	 */
	public String getIncompatibleTypeOperatorError() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Type error: incompatible operator " + operator.toString() 
				+ " with type " + expression.getType().toString();
		return lineAndColumn + error ;
	}
	
	@Override
	public String toString() {
		return location.toString() + " " + operator.toString() + " " + expression.toString();
		
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

}