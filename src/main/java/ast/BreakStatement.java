/**
 * This class represents the break statements: break;
 * @author Facundo Molina
 */
public class BreakStatement extends Statement {
	
	/**
	 * Constructor
	 */
	public BreakStatement(int line,int column) {
		this.setLineNumber(line);
		this.setColumnNumber(column);
	}

	/**
	 * Get outside of cycle error message
	 */
	public String getOutsideOfCycleError() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Declaration Error: the break statement is outside of a cycle ";
		return lineAndColumn + error;
	}

	@Override
	public String toString() {
		return "break;";
		
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

}