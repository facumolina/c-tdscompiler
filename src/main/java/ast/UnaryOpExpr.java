/**
 * This class represents the unary expressions: op expr
 * @author Facundo Molina
 */
public class UnaryOpExpr extends Expression {
	
	private UnaryOpType operator; 	//operator in the expr = operator expr
	private Expression operand; 	//expression
	
	/**
	 * Constructor
	 */
	public UnaryOpExpr(UnaryOpType op, Expression e,int line,int column){
		operator = op;
		operand = e;
		this.setLineNumber(line);
		this.setColumnNumber(column);
	}
	
	/**
	 * Get operator
	 */
	public UnaryOpType getOperator() {
		return operator;
	}

	/**
	 * Set operator
	 */
	public void setOperator(UnaryOpType operator) {
		this.operator = operator;
	}

	/**
	 * Get operand
	 */
	public Expression getOperand() {
		return operand;
	}

	/**
	 * Set operand
	 */
	public void setOperand(Expression operand) {
		this.operand = operand;
	}

	/**
	 * Returns true if the operand type is compatible with the operator
	 */
	public boolean compatibleOperator() {
		return operator.isValidForType(operand.getType());
	}
	
	/**
	 * Get incomptabile type operator error message
	 */
	public String getIncompatibleTypeOperatorError() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Type error: incompatible operator " + operator.toString() 
				+ " with type " + operand.getType().toString();
		return lineAndColumn + error ;
	}

	@Override
	public String toString() {
		return operator.toString() + operand.toString();
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

}