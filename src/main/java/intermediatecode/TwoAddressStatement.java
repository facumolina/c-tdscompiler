/**
 * This class represents a two address statement: instruction expression result
 */
public class TwoAddressStatement extends IntermediateCodeStatement {
	
	private Expression expression;
	private Expression result;

	/**
	 * Constructor
	 */
	public TwoAddressStatement(IntermediateCodeInstruction instruction,Expression expression, Expression result) {
		super(instruction);
		this.expression = expression;
		this.result = result;
	}

	/**
	 * Get expression
	 */
	public Expression getExpression() {
		return expression;
	}

	/**
	 * Set expression
	 */
	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	/**
	 * Get result
	 */
	public Expression getResult() {
		return result;
	}

	/**
	 * Set result
	 */
	public void setResult(Expression result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return instruction.toString() + " " + expression.toString() + " " + result.toString();
	}

}