/**
 * This class represents the int literals
 * @author Facundo Molina
 */
public class IntLiteral extends Literal {
	
	private String rawValue;
	private Integer value;
	
	/*
	 * Constructor for int literal that takes a string as an input
	 * @param: String integer
	 */
	public IntLiteral(String val){
		rawValue = val; 
		value = Integer.parseInt(val);
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
	}

	/**
	 * Get the value
	 */
	public Integer getValue() {
		return value;
	}

	/**
	 * Set the value
	 */
	public void setValue(int value) {
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