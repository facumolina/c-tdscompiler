/**
 * Enumerate for the data types.
 * @author Facundo Molina
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
	
	/**
	 * Returns true if the current type is equal to the given type
	 */
	public boolean equals(Type t) {
		return this.toString().equals(t.toString());
	}

	/**
	 * Returns true if the type represents an array
	 */
	public boolean isArray() {
		if (this == Type.INTARRAY) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the default value 
	 */
	public Literal getDefaultValue() {
		switch (this) {
			case INT: return new IntLiteral(0);
			case FLOAT:	return new FloatLiteral(new Float(0));
			case BOOLEAN: return new BooleanLiteral();
			default: return null;
		}
	}
}