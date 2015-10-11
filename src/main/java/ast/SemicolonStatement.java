/**
* This class represents the semicolon statement: ;
* @author Facundo Molina
*/
public class SemicolonStatement extends Statement {

	/**
	 * Constructor
	 */
	public SemicolonStatement(int line,int column) {
		setLineNumber(line);
		setColumnNumber(column);
	}

	@Override
	public String toString() {
		return ";";
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

}