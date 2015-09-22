import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a block.
 */
public class Block extends Statement {
	
	private int blockId;
	private List<FieldDeclaration> fieldDeclarations;
	private List<Statement> statements;
	
	public Block() {
		blockId = -1;
		fieldDeclarations = new LinkedList<FieldDeclaration>();
		statements = new LinkedList<Statement>();
	}
	
	public Block(List<FieldDeclaration> fl, List<Statement> sl) {
		blockId = -1;
		fieldDeclarations = fl;		
		statements = sl;
	}
	
	public void addStatement(Statement s) {
		this.statements.add(s);
	}
		
	public List<Statement> getStatements() {
		return this.statements;
	} 
		
	public int getBlockId() {
		return blockId;
	}

	public void setBlockId(int blockId) {
		this.blockId = blockId;
	}

	@Override
	public String toString() {
		String rtn = "";
		
	    for (Statement s: statements) {
			rtn += s.toString() + '\n';
		}
		
		if (rtn.length() > 0) return rtn.substring(0, rtn.length() - 1); // remove last new line char
		
		return rtn; 
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}
	
}
