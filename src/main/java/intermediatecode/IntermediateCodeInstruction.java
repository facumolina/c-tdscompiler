/**
 * This enumarate represents all the intermediate code instructions
 * @author Facundo Molina
 */
public enum IntermediateCodeInstruction {

	ADDI,			// Integer addition 
	ADDF,			// Float addition
	SUBI,			// Integer substraction
	SUBF,			// Float substraction
	MULTI,			// Interger multiplication
	MULTF,			// Float multiplication
	ASSIGN;			// Assignment

	@Override
	public String toString() {
		switch(this) {
			case ADDI:
				return "ADDI";
			case ADDF:
				return "ADDF";
			case SUBI:
				return "SUBI";
			case SUBF:
				return "SUBF";
			case MULTI:
				return "MULTI";
			case MULTF:
				return "MULTF";
			case ASSIGN:
				return "ASSIGN";
		}
		return null;		
	}

}