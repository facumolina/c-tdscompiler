/**
 * This class represents the method calls as statements.
 * @author Facundo Molina
 */

public class MethodCallStatement extends Statement {
	
	private MethodCall methodCall; 			// Method call

	/**
	 * Constructor
	 */
	public MethodCallStatement(MethodCall m, int line, int column) {
		methodCall = m;
		setLineNumber(line);
		setColumnNumber(column);
	}

	/**
	 * Get method call
	 */
	public MethodCall getMethodCall() {
		return methodCall;
	}

	/**
	 * Set method call
	 */
	public void setMethodCall(MethodCall m) {
		methodCall = m;
	}

	@Override
	public String toString() {
		return methodCall.toString() + ";";
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}
	
}