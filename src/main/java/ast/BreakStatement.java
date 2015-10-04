/**
 * This class represents the break statements: break;
 */

public class BreakStatement extends Statement {
	
	/**
	 * Constructor
	 */
	public BreakStatement() {
		
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