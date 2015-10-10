/**
 * This class represents the While statement: while expr block
 * @author Facundo Molina
 */
public class WhileStatement extends Statement {
	
	private Expression condition;			// Cycle condition
	private Block block;					// Block

	/**
	 * Constructor
	 */
	public WhileStatement(Expression c, Block b, int line, int column) {
		condition = c;
		block = b;
		this.setLineNumber(line);
		this.setColumnNumber(column);
	}

	/**
	 * Get condition
	 */
	public Expression getCondition() {
		return condition;
	}

	/**
	* Set condition
	*/
	public void setCondition(Expression c) {
		condition = c;
	}

	/**
	 * Get block
	 */
	public Block getBlock() {
		return block;
	}

	/**
	 * Set block
	 */
	public void setBlock(Block b) {
		block = b;
	}

	/**
	 * Get condition type error
	 */
	public String getConditionTypeError() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Type Error: the condition expression type is " + condition.getType().toString()
				+ " and must be boolean";
		return lineAndColumn + error;
	}

	@Override 
	public String toString() {
		return "while " + condition.toString() + ", \n" + block.toString();
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

}