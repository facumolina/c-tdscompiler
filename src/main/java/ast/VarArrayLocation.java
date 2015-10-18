import java.util.List;
import java.util.LinkedList;

/**
 * This class represents the locations of the form ID[expr] or ID.ID ... .ID[expr] .
 * @author Facundo Molina
 */
public class VarArrayLocation extends Location {
	
	private int blockId; 				// Block id
	private List<String> listIds;		// List of identifiers
	private Expression expr;			// Expression

	/**
	 * Constructor for a simple var array location (ID [expr])
	 */
	public VarArrayLocation(String id,Expression expr,int line,int column) {
		this.id = id;
		this.expr = expr;
		this.blockId = -1;
		listIds = new LinkedList<String>();
		this.setLineNumber(line);
		this.setColumnNumber(column);
	}
	
	/**
	 * Constructor for a extended var array location (ID.ID ... .ID[expr])
	 */
	public VarArrayLocation(String id,Expression expr,List<String> l,int line,int column) {
		this.id = id;
		this.expr = expr;
		this.blockId = -1;
		listIds = l;
		this.setLineNumber(line);
		this.setColumnNumber(column);
	}

	/**
	 * Get block id
	 */
	public int getBlockId() {
		return blockId;
	}

	/**
	 * Set block id
	 */
	public void setBlockId(int blockId) {
		this.blockId = blockId;
	}
	
	/**
	 * Get the expression
	 */
	public Expression getExpression() {
		return expr;
	}

	/**
	 * Set the expression
	 */
	public void setExpression(Expression e) {
		expr = e;
	}

	/**
	 * Get the list of identifiers
	 */
	public List<String> getListIds() {
		return listIds;
	}

	/**
	 * Set the lisf of identifiers
	 */
	public void setListIds(List<String> l) {
		listIds = l;
	}

	@Override
	public Literal getValue() {
		IntLiteral position = (IntLiteral)expr.getValue();
		return decl.getValue(position.getIntegerValue());
	}

	@Override
	public void setValue(Literal value) {
		decl.setValue(value);
	}

	/**
	 * Returns true if the location is simple, that is it has the form ID[expr]
	 * and returns false if is extended.
	 */
	public boolean isSimpleLocation() {
		return listIds.size()==0;
	}

	/**
	 * Get no declaration error message
	 */
	public String getNoDeclarationErrorMessage() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Location Error: No exists an array declaration with name " + id;
		return lineAndColumn + error;
	}

	/**
	 * Get no declaration other class error message
	 */
	public String getNoDeclarationOtherClassErrorMessage() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Location Error: cannot resolve attribute " + listIds.get(0)
			+ " of class " + id;
		return lineAndColumn + error;
	}

	/**
	 * Get incorrect index (expr) type error message
	 */
	public String getTypeIndexError() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Type Error: the index expression has type " + expr.getType().toString()
				+  ", and must be of type int";
		return lineAndColumn + error;
	}

	/**
	 * Get invalid index error message
	 */
	public String getInvalidIndexError() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		IntLiteral value = (IntLiteral)expr.getValue();
		String error = "Error: the index expression value is " + value.getIntegerValue()
			+ " and must be greater or equal than 0 and less than " + decl.getCapacity();
		return lineAndColumn + error;
	}

	@Override
	public String toString() {
		String locationString = id;
		for (String s : listIds) {
			locationString += "." + s;
		}
		locationString += "[" + expr.toString() + "]";
		return locationString;
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

}