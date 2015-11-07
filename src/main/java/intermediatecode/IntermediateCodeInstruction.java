/**
 * This enumarate represents all the intermediate code instructions
 * @author Facundo Molina
 */
public enum IntermediateCodeInstruction {

	ADDI,			// Integer addition 
	ADDF,			// Float addition
	SUBI,			// Integer substraction
	SUBF,			// Float substraction
	MULTI,			// Integer multiplication
	MULTF,			// Float multiplication
	DIVI, 			// Integer division
	DIVF,			// Float division
	MOD,			// Mod
	LESS,			// Less
	LESSEQ,			// Less or equal 
	GREAT,			// Great
	GREATEQ,		// Great or equal
	EQ,				// Equality
	NEQ,			// Non equality
	AND,			// Conjunction
	OR,				// Disyunction
	NOT,			// Negator
	JUMP,			// Jump
	RESERVE,		// Reserve space
	JUMPF,			// Jump for false
	INITML,			// Init method label
	LABEL,			// Label
	RET,			// Return
	PUSH,			// Push arguments
	CALL,			// Call
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
			case DIVI:
				return "DIVI";
			case DIVF:
				return "DIVF";
			case MOD:
				return "MOD";
			case LESS:
				return "LESS";
			case LESSEQ:
				return "LESSEQ";
			case GREAT:
				return "GREAT";
			case GREATEQ:
				return "GREATEQ";
			case EQ:
				return "EQ";
			case AND:
				return "AND";
			case OR:
				return "OR";
			case NOT:
				return "NOT";
			case JUMP:
				return "JUMP";
			case JUMPF:
				return "JUMPF";
			case RESERVE:
				return "RESERVE";
			case INITML:
				return "INITML";
			case LABEL:
				return "LABEL";
			case RET:
				return "RET";
			case PUSH:
				return "PUSH";
			case CALL:
				return "CALL";
			case ASSIGN:
				return "ASSIGN";
		}
		return null;		
	}

}