/**
 * This class represents a three address statement: instruction expression1 expression2 result
 */
public class ThreeAddressStatement extends IntermediateCodeStatement {
	
	private Expression expressionOne;
	private Expression expressionTwo;
	private Expression result;

	/**
	 * Constructor
	 */
	public ThreeAddressStatement(IntermediateCodeInstruction instruction, Label label,Expression expressionOne,Expression expressionTwo, Expression result) {
		super(instruction,label);
		this.expressionOne = expressionOne;
		this.expressionTwo = expressionTwo;		
		this.result = result;
	}

	/**
	 * Get expression one
	 */
	public Expression getExpressionOne() {
		return expressionOne;
	}

	/**
	 * Set expression one
	 */
	public void setExpressionOne(Expression expressionOne) {
		this.expressionOne = expressionOne;
	}

	/**
	 * Get expression two
	 */
	public Expression getExpressionTwo() {
		return expressionTwo;
	}

	/**
	 * Set expression two
	 */
	public void setExpressionTwo(Expression expressionTwo) {
		this.expressionTwo = expressionTwo;
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
		return label.toString() + ": " + instruction.toString() 
			+ " " + expressionOne.toString() + " "+ expressionTwo.toString() 
			+ " " + result.toString();
	}

}