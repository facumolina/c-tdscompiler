/**
 * This class represents the print statement
 * @author Facundo Molina
 */
public class PrintStatement extends Statement {
 
 	private Expression expression; 		// Expression to be printed

 	/**
 	 * Constructor
 	 */
	public PrintStatement(Expression e,int line, int column) {
		this.expression = e;
		setLineNumber(line);
		setColumnNumber(column);
	}

	/**
	 * Get the expression
	 */
	public Expression getExpression() {
		return expression;
	}

	/**
	 * Set the expression
	 */
	public void setExpression(Expression e) {
		this.expression = e;
	}

	@Override 
	public String toString() {
		return "print";
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}
}