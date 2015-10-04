/**
 * This class represents a generic ast node.
 */

public abstract class AST {
	
	private int lineNumber; 			// line number
	private int colNumber;				// column number
	protected boolean hasId;			

	/**
	 * Get line number
	 */
	public int getLineNumber() {
		return lineNumber;
	}
	
	/**
	 * Set line number
	 */
	public void setLineNumber(int ln) {
		lineNumber = ln;
	}
	
	/**
	 * Get column number
	 */
	public int getColumnNumber() {
		return colNumber;
	}
	
	/**
	 * Set column number
	 */
	public void setColumnNumber(int cn) {
		colNumber = cn;
	}

	public boolean hasId() {
		return hasId;
	}
	
	/**
	 * Accept a visitor.
	 */
	public abstract <T> T accept(ASTVisitor<T> v);

}