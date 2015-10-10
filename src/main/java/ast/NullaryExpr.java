/**
 * This class represents the nullary expressions. (expr)
 * @author Facundo Molina
 */
public class NullaryExpr extends Expression {
	
	private Expression expr; 			// Expression

	/**
	 * Constructor
	 */
	public NullaryExpr(Expression e){
		expr = e;
	}

	/**
	 * Get the expression
	 */
	public Expression getExpression() {
		return expr;
	}

	/**
	 * Set the expression
	 */
	public void setExpression(Expression e) {
		this.expr = e;
	}

	@Override
	public String toString() {
		return expr.toString();
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}
	
}