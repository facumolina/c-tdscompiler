/**
 * Enumerate for the type of the assignment operators.
 * @author Facundo Molina
 */
public enum AssignOpType {
	
	INCREMENT,				// Increment assignment +=
	DECREMENT,				// Decrement assignment -=
	ASSIGN;					// Common assignment =
	
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
