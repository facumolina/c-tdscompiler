
public class BooleanLiteral extends Literal {
	private String rawValue;
	private Boolean value;
	
	/*
	 * Constructor for boolean literal that takes a boolean value
	 * @param: Boolean b
	 */
	public BooleanLiteral(Boolean b){
		rawValue = b.toString(); // Will convert to int value in semantic check
		value = b;
	}

	@Override
	public Type getType() {
		return Type.BOOLEAN;
	}

	public String getStringValue() {
		return rawValue;
	}

	public void setStringValue(String stringValue) {
		this.rawValue = stringValue;
	}

	public Boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}
	
	public String getRawValue() {
		return rawValue;
	}

	public void setRawValue(String rawValue) {
		this.rawValue = rawValue;
	}

	@Override
	public String toString() {
		return rawValue;
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}
}