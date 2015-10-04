/**
 * Enumerate for the data types.
 */
public enum Type {
	INT,
	INTARRAY,
	FLOAT,
	BOOLEAN,
	VOID,
	UNDEFINED;
	
	@Override
	public String toString() {
		switch(this) {
			case INT:
				return "int";
			case VOID:
				return "void";
			case UNDEFINED:
				return "undefined";
			case INTARRAY:
				return "int[]";
			case FLOAT:
				return "float";
			case BOOLEAN:
				return "boolean";
		}
		return null;
	}
	
	public boolean equals(Type t) {
		return this.toString().equals(t.toString());
	}

	public boolean isArray() {
		if (this == Type.INTARRAY) {
			return true;
		}
		return false;
	}
}