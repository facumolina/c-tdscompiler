/**
 * This class represents a boolean literal: true or false
 */
public class BooleanLiteral extends Literal {
	
	private String rawValue;
	private Boolean value;
	
	/*
	 * Constructor with a boolean value
	 */
	public BooleanLiteral(Boolean b){
		rawValue = b.toString(); 
		value = b;
	}

	@Override
	public Type getType() {
		return Type.BOOLEAN;
	}

	/**
	 * Get the value as string	
	 */
	public String getStringValue() {
		return rawValue;
	}

	/**
	 * Set the string value
	 */
	public void setStringValue(String stringValue) {
		rawValue = stringValue;
	}

	/**
	 * Get the value
	 */
	public Boolean getValue() {
		return value;
	}

	/**
	 * Set the value
	 */ 
	public void setValue(boolean value) {
		this.value = value;
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