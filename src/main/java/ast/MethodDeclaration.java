import java.util.List;

/**
 * This class represents the Method declarations:
 *   - type id () body ;
 *   - type id (a1,a2,..,an) body ;
 */
public class MethodDeclaration extends Identifiable {
	
	private List<Argument> arguments; 			// Arguments
	private Block block;						// Block
	private boolean isExtern;					// IsExtern
	private Type type;							// Return type

	/**
	 * Constructor
	 */
	public MethodDeclaration(Type t, String id, List<Argument> l,Block b, int line, int column) {
		this.id = id;
		type = t;
		arguments = l;
		if (b == null) {
			isExtern = true;
		} else {
			block = b;
			isExtern = false;
		}
		this.setLineNumber(line);
		this.setColumnNumber(column);
	}

	/**
	 * Get type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Set type
	 */
	public void setType(Type t) {
		type = t;
	}

	/**
	 * Get the arguments
 	 */
	public List<Argument> getArguments() {
		return arguments;
	}
 
 	/**
 	 * Set the arguments
 	 */
 	public void setArguments(List<Argument> l) {
 		arguments = l;
 	}

 	/**
 	 * Get the block 
 	 */
	public Block getBlock() {
		return block;
	}

	/**
	 * Set the block
	 */
	public void setBlock(Block b) {
		block = b;
	}

	/**
	 * Returns true if the method is extern
	 */
	public boolean isExtern() {
		return isExtern;
	}

	/**
	 * Returns true if has a return statement
	 */
	public boolean hasReturnStatement() {
		if (!isExtern) {
			/*for (Statement statement : block.getStatements()) {
				if (statement instanceof ReturnStatement) {
					return true;
				}
			}
			return false;*/
			return hasReturnStatementRecursive(block);
		} 
		return false;
	}

	private boolean blockWithReturn(Block block) {
		for (Statement statement : block.getStatements()) {
			if (statement instanceof ReturnStatement) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Returns true if the given Block has a return statement in the list of statements
	 */
	private boolean hasReturnStatementRecursive(Block block) {
		if (blockWithReturn(block)) {
			return true;
		} else {
			List<Statement> stmtList = block.getStatements();
			boolean found = false;
			for (int i = 0; i < stmtList.size() && !found; i++) {
				Statement statement = stmtList.get(i);
				if (statement instanceof IfStatement) {
					IfStatement ifStatement = (IfStatement) statement;
					if (ifStatement.hasElseBlock()) {
						found = hasReturnStatementRecursive(ifStatement.getIfBlock()) && hasReturnStatementRecursive(ifStatement.getElseBlock());	
					}
				} else if (statement instanceof Block) {
					found = hasReturnStatementRecursive((Block)statement);
				}
			}
			return found;
		}
		
	}

	/**
	 * Get declaration error message
	 */
	public String getDeclarationErrorMessage() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Declaration Error: already exists a method with name " + id;
		return lineAndColumn + error;
	}

	/**
	 * Get missing return statement error message
	 */
	public String getMissingReturnStatementError() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Declaration Error: missing return statement";
		return lineAndColumn + error;
	}

	/**
	 * Get incorrect return statement error message
	 */
	public String getIncorrectReturnStatementError() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Declaration Error: the void methods does not return expressions";
		return lineAndColumn + error;
	}

	@Override
	public String toString() {
		String methodString = type.toString() + " " + id + "(";
		for (Argument arg : arguments) {
			methodString += arg.toString() + " "; 
		}
		methodString += ") ";
		methodString += block.toString();
		return methodString;
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

} 