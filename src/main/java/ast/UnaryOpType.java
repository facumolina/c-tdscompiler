/**
 * Enumerate for the type of the unary operators.
 */
public enum UnaryOpType {
	
	NOT, 
	MINUS;

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
