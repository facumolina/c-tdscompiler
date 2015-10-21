/**
 * This class represents an intermediate code statement
 * @author Facundo Molina
 */
public class IntermediateCodeStatement {

	protected IntermediateCodeInstruction instruction;

	/**
	 * Constructor
	 */
	public IntermediateCodeStatement(IntermediateCodeInstruction instruction) {
		this.instruction = instruction;
	}
	
	/**
	 * Get the instruction
	 */
	public IntermediateCodeInstruction getInstruction() {
		return instruction; 
	}

	/**
	 * Set the instruction 
	 */
	public void setInstruction(IntermediateCodeInstruction i) {
		instruction = i;
	}

	@Override
	public String toString() {
		return instruction.toString();
	}
	
}