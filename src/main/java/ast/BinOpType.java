/**
 * Enumerate for the type of the bynary operators.
 */
public enum BinOpType {
	
	PLUS, // Arithmetic
	MINUS,
	MULTIPLY,
	DIVIDE,
	MOD,
	LE, // Relational
	LEQ,
	GE,
	GEQ,
	NEQ, // Equal
	CEQ, 
	AND, // Conditional
	OR;
	
	@Override
	public String toString() {
		switch(this) {
			case PLUS:
				return "+";
			case MINUS:
				return "-";
			case MULTIPLY:
				return "*";
			case DIVIDE:
				return "/";
			case MOD:
				return "%";
			case LE:
				return "<";
			case LEQ:
				return "<=";
			case GE:
				return ">";
			case GEQ:
				return ">=";
			case CEQ:
				return "==";
			case NEQ:
				return "!=";
			case AND:
				return "&&";
			case OR:
				return "||";
		}
		
		return null;
	}

	/**
	 * Returns true if the operator is valid for the given type
	 */
	public boolean isValidForType(Type t) {
		switch (t) {
			case INT: switch (this) {
				case AND: 
					return false;
				case OR: 
					return false;
				default: 
					return true;
			}
			case FLOAT: switch (this) {
				case AND: 
					return false;
				case OR: 
					return false;
				case MOD:
					return false;
				default: 
					return true;
			}
			case BOOLEAN: switch (this) {
				case CEQ:
					return true;
				case NEQ:
					return true;
				case AND:
					return true;
				case OR:
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
		switch (this) {
			case PLUS:
				return true;
			case MINUS:
				return true;
			case MULTIPLY:
				return true;
			case DIVIDE:
				return true;
			case MOD:
				return true;
			default:
				return false;
		}
	}

}