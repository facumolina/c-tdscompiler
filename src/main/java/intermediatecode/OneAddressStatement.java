/**
 * This class represents a one address statement: instruction expression
 */
public class OneAddressStatement extends IntermediateCodeStatement {

	private Label labelToJump;						// Label to jump
	private Expression expression;					// Expression
	
	/**
	 * Constructor
	 */
	public OneAddressStatement(IntermediateCodeInstruction instruction, Label label) {
		super(instruction,label);
	}

	/**
	 * Constructor with expression
	 */
	public OneAddressStatement(IntermediateCodeInstruction instruction, Label label, Expression expression) {
		super(instruction,label);
		this.expression = expression;
		labelToJump = null;
	}

	/**
	 * Constructor with label to jump
	 */
	public OneAddressStatement(IntermediateCodeInstruction instruction, Label label,Label toJump) {
		super(instruction,label);
		this.labelToJump = toJump;
	}

	/**
	 * Constructor with expression and label to jump
	 */
	public OneAddressStatement(IntermediateCodeInstruction instruction, Label label, Expression expression,Label toJump) {
		super(instruction,label);
		this.expression = expression;
		this.labelToJump = toJump;
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

	/**
	 * Get the label to jump
	 */
	public Label getLabelToJump() {
		return labelToJump;
	}

	/**
	 * Set the expression
	 */
	public void setLabelToJump(Label toJump) {
		this.labelToJump = toJump;
	}

	@Override
	public String toString() {
		String s = label.toString() + ": " + instruction.toString();
		if (expression!=null) {
			s += " " + expression.toString();
		}
		if (labelToJump!=null) {
			s += " " + labelToJump.toString();
		}
		return s;
	}

}