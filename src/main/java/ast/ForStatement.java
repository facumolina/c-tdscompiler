/**
 * This class represets the For statements: for ID = expr, expr block
 * @author Facundo Molina
 */
public class ForStatement extends Statement {
	
	private AssignStatement initialAssign;  // Assign
	private Expression condition;			// Cycle condition
	private Block block;					// Block

	/**
	 * Constructor 
	 */
	public ForStatement(String id,Expression i,Expression c,Block b,int line,int column) {
		initialAssign = new AssignStatement(new VarLocation(id,line,column),AssignOpType.ASSIGN,i);
		condition = c;
		block = b;
		this.setLineNumber(line);
		this.setColumnNumber(column);
	}


	/**
	 * Get initial assignment
	 */
	public AssignStatement getInitialAssign() {
		return initialAssign;
	}

	/**
	 * Set initial assignment
	 */
	public void setInitialAssign(AssignStatement i) {
		initialAssign = i;
	}

	/**
	 * Get condition
	 */
	public Expression getConditionExpression() {
		return condition;
	}

	/**
	 * Set condition
	 */
	public void setCondition(Expression cond) {
		condition = cond;
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
				+ " and must be int";
		return lineAndColumn + error;
	}

	/**
	 * Get initial assign error
	 */
	public String getAssignTypeError() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Type Error: the expression and location type must be int";
		return lineAndColumn + error;
	}

	@Override
	public String toString() {
		return "for " + initialAssign.getLocation().getId() + " = " 
				+ initialAssign.getExpression().toString() + ", " + condition.toString() 
				+ " " + block.toString(); 
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

}