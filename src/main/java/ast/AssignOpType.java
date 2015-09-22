/**
 * Enumerate for the type of the assignment operators.
 */

public enum AssignOpType {
	INCREMENT,
	DECREMENT,
	ASSIGN;
	
	@Override
	public String toString() {
		switch(this) {
			case INCREMENT:
				return "+=";
			case DECREMENT:
				return "-=";
			case ASSIGN:
				return "=";
		}
		return null;		
	}
}
