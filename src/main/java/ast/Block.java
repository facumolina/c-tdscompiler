import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a block.
 */
public class Block extends Statement {
	
	private int blockId; 								// Block id
	private List<FieldDeclaration> fieldDeclarations; 	// List of field declarations
	private List<Statement> statements;					// List of statements
	
	/**
	 * Constructor 
	 */
	public Block() {
		blockId = -1;
		fieldDeclarations = new LinkedList<FieldDeclaration>();
		statements = new LinkedList<Statement>();
	}
	
	/**
	 * Constructor with a fiven list of field declarations and a list of statements.
	 */
	public Block(List<FieldDeclaration> fl, List<Statement> sl) {
		blockId = -1;
		fieldDeclarations = fl;		
		statements = sl;
	}
	
	/**
	 * Get the block id
	 */
	public int getBlockId() {
		return blockId;
	}

	/**
	 * Set the block id
	 */
	public void setBlockId(int blockId) {
		this.blockId = blockId;
	}

	/**
	 * Get the statements
	 */
	public List<Statement> getStatements() {
		return statements;
	} 

	/**
	 * Set the statements
	 */
	public void setStatements(List<Statement> sl) {
		statements = sl;
	}
	
	/**
	 * Add a new statement to the list of statements
	 */
	public void addStatement(Statement s) {
		statements.add(s);
	}

	/**
	 * Get the field declarations
	 */
	public List<FieldDeclaration> getFieldDeclarations() {
		return fieldDeclarations;
	}

	/**
	 * Set the field declarations
	 */
	public void setFieldDeclarations(List<FieldDeclaration> fl) {
		fieldDeclarations = fl;
	}

	@Override
	public String toString() {
		String blockString = "{\n";
		for (FieldDeclaration fieldDeclaration : fieldDeclarations) {
			blockString += "\t" + fieldDeclaration.toString() + "\n";
		}
	    for (Statement statement: statements) {
			blockString += "\t" + statement.toString() + "\n";
		}
		
		//if (blockString.length() > 0) return blockString.substring(0, blockString.length() - 1); // remove last new line char
		blockString += "}";
		return blockString; 
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}
	
}
