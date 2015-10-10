import java.util.List;
import java.util.LinkedList;

/**
 * This class represents the locations of the form ID or ID.ID. ... .ID .
 * @author Facundo Molina
 */
public class VarLocation extends Location {
	
	private int blockId; 					// Block
	private List<String> listIds;			// List of identifiers
	
	/**
	 * Constructor for simple var location (ID).
	 */
	public VarLocation(String id,int line,int column) { 		
		this.id = id;
		this.blockId = -1;
		listIds = new LinkedList<String>();
		this.setLineNumber(line);
		this.setColumnNumber(column);
	}

	/**
	 * Constructor for extended var location (ID.ID. .. .ID)
	 */
	public VarLocation(String id,List<String> l,int line,int column) {
		this.id = id;
		this.blockId = -1;
		listIds = l;
		this.setLineNumber(line);
		this.setColumnNumber(column);	
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
	 * Get the list of identifiers
	 */
	public List<String> getListIds() {
		return listIds;
	}

	/**
	 * Set the list of identifiers
	 */
	public void setListIds(List<String> l) {
		listIds = l;
	}

	/**
	 * Returns true if the location is simple, that is has the form ID
	 * and returns false if is extended
	 */
	public boolean isSimpleLocation() {
		return listIds.size()==0;
	}

	/**
	 * Get no declaration error message
	 */
	public String getNoDeclarationErrorMessage() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Location Error: No exists a var declaration with name " + id;
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
