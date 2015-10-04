/**
 * This class represents the continue statements: continue;
 */

public class ContinueStatement extends Statement {
	

	public ContinueStatement() {
		
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