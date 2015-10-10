/**
 * Enumerate for the type of the unary operators.
 * @author Facundo Molina
 */
public enum UnaryOpType {
	
	NOT, 
	MINUS;

	/**
	 * Returns true if the operator is valid for the given type
	 */
	public boolean isValidForType(Type t) {
		switch (t) {
			case INT: switch (this) {
				case MINUS: 
					return true;
				default: 
					return false;
			}
			case FLOAT: switch (this) {
				case MINUS: 
					return true;
				default: 
					return false;
			}
			case BOOLEAN: switch (this) {
				case NOT:
					return true;
				default:
					return false;
			}
			default:
				return false;
		}
	}

	/**
	 * Returns true if the operator is arithmetical
	 */
	public boolean isArithmetical() {
		return this==MINUS;
	}
	
	@Override
	public String toString() {
		switch(this) {
			case NOT:
				return "!";
			case MINUS:
				return "-";
			
		}
		return null;
	}

}
