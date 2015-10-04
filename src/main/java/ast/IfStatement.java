/**
 * This class represents the If statement: if condition ifblock else elseblock 
 */

public class IfStatement extends Statement {
	
	private Expression condition; 				// Condition expression
	private Block ifBlock;						// If block
	private Block elseBlock;					// Else block
	
	/**
	 * Constructor without else block
	 */
	public IfStatement(Expression cond, Block ifBl, int line, int column) {
		this.condition = cond;
		this.ifBlock = ifBl;
		this.elseBlock = null;
		this.setLineNumber(line);
		this.setColumnNumber(column);
	}
	
	/**
	 * Constructor with else block
	 */
	public IfStatement(Expression cond, Block ifBl, Block elseBl, int line, int column) {
		this.condition = cond;
		this.ifBlock = ifBl;
		this.elseBlock = elseBl;
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
	public void setCondition(Expression condition) {
		this.condition = condition;
	}

	/**
	 * Get if block
	 */
	public Block getIfBlock() {
		return ifBlock;
	}

	/**
	 * Set if block
	 */
	public void setIfBlock(Block ifBlock) {
		this.ifBlock = ifBlock;
	}

	/**
	 * Get else block
	 */
	public Block getElseBlock() {
		return elseBlock;
	}

	/**
	 * Set else block
	 */
	public void setElseBlock(Block elseBlock) {
		this.elseBlock = elseBlock;
	}

	/**
	 * Returns true if the statement has else block
	 */
	public boolean hasElseBlock() {
		return elseBlock != null;
	}
	
	/**
	 * Get expression type error message
	 */
	public String getExpressionTypeErrorMessage() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Type error: the condition expression type is " + condition.getType().toString()
				+ " and must be boolean";
		return lineAndColumn + error ;
	}

	@Override
	public String toString() {
		String rtn = "if " + condition + '\n' + ifBlock.toString();
		
		if (elseBlock != null) {
			rtn += "else \n" + elseBlock;
		}
		
		return rtn;
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}
}
