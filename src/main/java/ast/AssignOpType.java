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

	/**
	 * Returns true if the operator is valid for the given type
	 */
	public boolean isValidForType(Type t) {
		switch (t) {
			case BOOLEAN: if ((this==INCREMENT)||(this==DECREMENT)) return false;
			default: return true;
		}
	}

	/**
	 * Returns true if the current operator is equal to the given operator
	 */
	public boolean equals(AssignOpType t) {
		return this.toString().equals(t.toString());
	}
	
}
