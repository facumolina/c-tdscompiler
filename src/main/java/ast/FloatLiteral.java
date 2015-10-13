/**
* This class represents the float literals
* @author Facundo Molina
*/
public class FloatLiteral extends Literal {

	private String rawValue;
	private Float floatValue;
	
	/*
	 * Constructor for float literal that takes a string as an input
	 */
	public FloatLiteral(String val,int line, int column){
		rawValue = val; 
		floatValue = Float.parseFloat(val);
		setLineNumber(line);
		setColumnNumber(column);
		value = this;
	}

	/**
	 * Constructor with a given float 
	 */
	public FloatLiteral(Float f) {
		floatValue = f;
		rawValue = f.toString();
		value = this;
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
		floatValue = Float.parseFloat(stringValue);
	}

	/**
	 * Get the value
	 */
	public Float getFloatValue() {
		return floatValue;
	}

	/**
	 * Set the value
	 */
	public void setFloatValue(Float value) {
		this.floatValue = value;
		this.rawValue = value.toString();
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