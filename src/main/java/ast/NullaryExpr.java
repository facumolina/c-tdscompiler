
public class NullaryExpr extends Expression {
	
	public NullaryExpr(Expression e){
		expr = e;
	}
	
	/*public BinOpExpr(Expression e, TempExpression t) {
		lOperand = e;
		operator = t.getOperator();
		rOperand = t.getRightOperand();
	}*/

	public Expression getExpression() {
		return expr;
	}

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