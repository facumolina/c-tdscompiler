/**
 * This class represents the binary operations: expr op expr
 * @author Facundo Molina
 */
public class BinOpExpr extends Expression {
	
	private BinOpType operator; 	// Operator in the expr = expr operator expr
	private Expression lOperand; 	// Left expression
	private Expression rOperand; 	// Right expression
	
	/**
	 * Constructor with given left and right expressions and the operator.
	 */
	public BinOpExpr(Expression l, BinOpType op, Expression r, int line, int column){
		operator = op;
		lOperand = l;
		rOperand = r;
		this.setLineNumber(line);
		this.setColumnNumber(column);
	}
	
	/**
	 * Get the operator
	 */
	public BinOpType getOperator() {
		return operator;
	}

	/**
	 * Set the operator
	 */
	public void setOperator(BinOpType operator) {
		this.operator = operator;
	}

	/**
	 * Get the left expression
	 */
	public Expression getLeftOperand() {
		return lOperand;
	}

	/**
	 * Set the left expression 
	 */
	public void setLeftOperand(Expression lOperand) {
		this.lOperand = lOperand;
	}

	/**
	 * Get the right expression
	 */
	public Expression getRightOperand() {
		return rOperand;
	}

	/**
	 * Set the right expression
	 */
	public void setRightOperand(Expression rOperand) {
		this.rOperand = rOperand;
	}
	
	/**
	 * Assuming that the left and right operartors has the same type,
	 * returns true if that type is compatible with the operator
	 */
	public boolean compatibleOperator() {
		return operator.isValidForType(lOperand.getType());
	}
	
	/**
	 * Get incomptabile type operator error message
	 */
	public String getIncompatibleTypeOperatorError() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Type error: incomptabile operator " + operator.toString() 
				+ " with type " + lOperand.getType().toString();
		return lineAndColumn + error ;
	}

	/**
	 * Get different types error message
	 */
	public String getDifferentTypesErrorMessage() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Type error: incomptabile operands types. The left operands " 
			+ "type is " + lOperand.getType().toString() + ", but the right operand " 
			+ "type is " + rOperand.getType().toString();
		return lineAndColumn + error ;	
	}

	@Override
	public String toString() {
		return lOperand.toString() + " " + operator.toString() + " " + rOperand.toString();
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}
}
