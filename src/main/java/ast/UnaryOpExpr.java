
public class UnaryOpExpr extends Expression {
	
	private UnaryOpType operator; //operator in the expr = operator expr
	private Expression operand; //expression
	
	public UnaryOpExpr(UnaryOpType op, Expression e){
		operator = op;
		operand = e;
	}
	
	/*public BinOpExpr(Expression e, TempExpression t) {
		lOperand = e;
		operator = t.getOperator();
		rOperand = t.getRightOperand();
	}*/
	
	public UnaryOpType getOperator() {
		return operator;
	}

	public void setOperator(UnaryOpType operator) {
		this.operator = operator;
	}

	public Expression getOperand() {
		return operand;
	}

	public void setOperand(Expression operand) {
		this.operand = operand;
	}

	@Override
	public String toString() {
		return operator.toString() + operand.toString();
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}
}
