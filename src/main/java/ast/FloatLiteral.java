/**
* This class represents the float literals
*/
public class FloatLiteral extends Literal {

	private String rawValue;
	private Float value;
	
	/*
	 * Constructor for float literal that takes a string as an input
	 * @param: String float
	 */
	public FloatLiteral(String val){
		rawValue = val; // Will convert to float value in semantic check
		value = Float.parseFloat(val);
	}

	@Override
	public Type getType() {
		return Type.FLOAT;
	}

	/**
	 * Get string value
	 */
	public String getStringValue() {
		return rawValue;
	}

	/**
	 * Set string value
	 */
	public void setStringValue(String stringValue) {
		this.rawValue = stringValue;
	}

	/**
	 * Get the value
	 */
	public Float getValue() {
		return value;
	}

	/**
	 * Set the value
	 */
	public void setValue(float value) {
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