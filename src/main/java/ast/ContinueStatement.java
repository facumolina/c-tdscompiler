/**
 * This class represents the continue statements: continue;
 */

public class ContinueStatement extends Statement {
	
	/**
	 * Constructor
	 */
	public ContinueStatement(int line,int column) {
		this.setLineNumber(line);
		this.setColumnNumber(column);
	}

	/**
	 * Get outside of cycle error message
	 */
	public String getOutsideOfCycleError() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Declaration Error: the continue statement is outside of a cycle ";
		return lineAndColumn + error;
	}

	@Override
	public String toString() {
		return "continue;";
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

}