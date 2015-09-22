import java.util.List;
import java.util.LinkedList;
/**
 * This class represents the locations of the form ID[expr] or ID.ID ... .ID[expr] .
 */
public class VarArrayLocation extends Location {
	
	private int blockId;
	private List<String> listIds;
	private Expression expr;

	public VarArrayLocation(String id,Expression expr) {
		this.id = id;
		this.expr = expr;
		this.blockId = -1;
		listIds = new LinkedList<String>();
	}
	
	public VarArrayLocation(String id,Expression expr,List<String> l) {
		this.id = id;
		this.expr = expr;
		this.blockId = -1;
		listIds = l;
	}

	public int getBlockId() {
		return blockId;
	}

	public void setBlockId(int blockId) {
		this.blockId = blockId;
	}
	
	public Expression getExpression() {
		return expr;
	}

	public void setExpression(Expression e) {
		expr = e;
	}

	public List<String> getListIds() {
		return listIds;
	}

	public void setListIds(List<String> l) {
		listIds = l;
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
