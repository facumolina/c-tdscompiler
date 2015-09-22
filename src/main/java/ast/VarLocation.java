import java.util.List;
import java.util.LinkedList;

/**
 * This class represents the locations of the form ID or ID.ID. ... .ID .
 */
public class VarLocation extends Location {
	
	private int blockId;
	private List<String> listIds;

	public VarLocation(String id) {
		this.id = id;
		this.blockId = -1;
		listIds = new LinkedList<String>();
	}

	public VarLocation(String id,List<String> l) {
		this.id = id;
		this.blockId = -1;
		listIds = l;	
	}
	
	public int getBlockId() {
		return blockId;
	}

	public void setBlockId(int blockId) {
		this.blockId = blockId;
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
		return locationString;
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

}
