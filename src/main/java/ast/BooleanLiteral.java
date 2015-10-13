/**
 * This class represents a boolean literal: true or false
 * @author Facundo Molina
 */
public class BooleanLiteral extends Literal {
	
	private String rawValue; 	
	private Boolean booleanValue;
	
	/*
	 * Constructor with a boolean value
	 */
	public BooleanLiteral(Boolean b, int line, int column){
		rawValue = b.toString(); 
		booleanValue = b;
		setLineNumber(line);
		setColumnNumber(column);
		value = this;
	}

	/**
	 * Default constructor
	 */
	public BooleanLiteral() {
		booleanValue = false;
		rawValue = booleanValue.toString();
		value = this;
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
		booleanValue = stringValue.equals("true")?true:false;
	}

	/**
	 * Get the value
	 */
	public Boolean getBooleanValue() {
		return booleanValue;
	}

	/**
	 * Set the value
	 */ 
	public void setBooleanValue(Boolean value) {
		this.booleanValue = value;
		rawValue = value.toString();
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