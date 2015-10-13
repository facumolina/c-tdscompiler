/**
 * This class represents the int literals
 * @author Facundo Molina
 */
public class IntLiteral extends Literal {
	
	private String rawValue;
	private Integer intValue;
	
	/*
	 * Constructor for int literal that takes a string as an input
	 * @param: String integer
	 */
	public IntLiteral(String val,int line,int column){
		rawValue = val; 
		intValue = Integer.parseInt(val);
		setLineNumber(line);
		setColumnNumber(column);
		value = this;
	}

	/*
	 * Constructor for int literal that takes a integer
	 */
	public IntLiteral(Integer n){
		rawValue = n.toString(); 
		intValue = n;
		value = this;
	}

	@Override
	public Type getType() {
		return Type.INT;
	}

	/**
	 * Get the string value
	 */
	public String getStringValue() {
		return rawValue;
	}

	/**
	 * Set the string value
	 */
	public void setStringValue(String stringValue) {
		this.rawValue = stringValue;
		intValue = Integer.parseInt(stringValue);
	}

	/**
	 * Get the value
	 */
	public Integer getIntegerValue() {
		return intValue;
	}

	/**
	 * Set the value
	 */
	public void setIntegerValue(Integer value) {
		this.intValue = value;
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