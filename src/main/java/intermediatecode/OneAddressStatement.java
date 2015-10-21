/**
 * This class represents a one address statement: instruction expression
 */
public class OneAddressStatement extends IntermediateCodeStatement {

	private Expression expression;

	/**
	 * Constructor
	 */
	public OneAddressStatement(IntermediateCodeInstruction instruction, Expression e) {
		super(instruction);
		this.expression = e;
	}

	/**
	 * Get expression
	 */
	public Expression getExpression() {
		return expression;
	}

	/**
	 * Set the expression
	 */
	public void setExpression(Expression e) {
		this.expression = e;
	}

	@Override
	public String toString() {
		return instruction.toString() + " " + expression.toString();
	}

}