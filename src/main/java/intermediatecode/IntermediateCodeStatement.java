/**
 * This class represents an intermediate code statement
 * @author Facundo Molina
 */
public class IntermediateCodeStatement {

	protected Label label;								// Label - line number
	protected IntermediateCodeInstruction instruction;	// Instruction
	
	/**
	 * Constructor
	 */
	public IntermediateCodeStatement(IntermediateCodeInstruction instruction,Label label) {
		this.instruction = instruction;
		this.label = label;
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

	/**
	 * Get the label 
	 */
	public Label getLabel() {
		return label;
	}

	/**
	 * Set the label 
	 */
	public void setLabel(Label label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label.toString() + ": " + instruction.toString();
	}
	
}