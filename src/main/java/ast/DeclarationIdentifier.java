import java.util.ArrayList;

/**
 * This class represents the Declaration identifiers id or id[int_literal]
 * @author Facundo Molina
 */
public class DeclarationIdentifier extends Identifiable {
	
	private Integer capacity; 					// Capacity used if the declaration identifier is the identifier of an array
	private Type type;							// Type stored 
	private ArrayList<Literal> listOfValues;	// List of values. 
	
	/**
	 * Constructor for declarations identifiers: id
	 */
	public DeclarationIdentifier(String id,int line, int column) {
		this.id = id;
		capacity = null;
		this.setLineNumber(line);
		this.setColumnNumber(column);
		listOfValues = new ArrayList<Literal>(1);
	}

	/**
	 * Constructor for declarations identifiers: id[n]
	 */
	public DeclarationIdentifier(String id,Integer n,int line, int column) {
		this.id = id;
		capacity = n;
		this.setLineNumber(line);
		this.setColumnNumber(column);
		listOfValues = new ArrayList<Literal>(n);
	}

	/**
	 * Get the capacity
	 */
	public Integer getCapacity() {
		return capacity;
	}

	/**
	 * Set capacity
	 */
	public void setCapacity(Integer n) {
		capacity = n;
	}

	/**
	 * Get the type
	 */
	public Type getType() {
		return type;
	}

	/**
 	 * Set the type
 	 */
	public void setType(Type t) {
		type = t;
		if (isArrayDeclarationId()) {
			// Fill the array with the default values
			for (int i=0; i<capacity ; i++) {
				listOfValues.add(i,t.getDefaultValue());
			}
		} else {
			listOfValues.add(0,t.getDefaultValue());
		}
	}
	
	/**
	 * Return true if the declarations represents an array declaration
	 */
	public boolean isArrayDeclarationId() {
		return capacity != null;
	}

	/**
	 * Get value for declaration identifier representing: id
	 */
	public Literal getValue() {
		if (listOfValues.size()>0) {
			return listOfValues.get(0);	
		}
		return null;
	}

	/**
	 * Set value for declaration identifier representing: id
	 */
	public void setValue(Literal value) {
		listOfValues.set(0,value);
	}

	/**
	 * Get value for declaration identifier representing: id[x]
	 */
	public Literal getValue(Integer n) {
		return listOfValues.get(n);
	}

	/**
	 * Set value for declaration identifier representing: id[x]
	 */
	public void setValue(Integer n,Literal value) {
		listOfValues.set(n,value);
	}

	/**
	 * Get declaration error message
	 */
	public String getDeclarationErrorMessage() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Declaration Error: already exists a field with id " + id;
		return lineAndColumn + error;
	}

	/**
	 * Get declaration array error message
	 */
	public String getArrayDeclarationErrorMessage() {
		String lineAndColumn = getLineNumber() + ":" + getColumnNumber() + ": " ;
		String error = "Declaration Error: the array initial size must be greater than 0 ";
		return lineAndColumn + error;
	}
	
	@Override
	public String toString() {
		if (isArrayDeclarationId()) {
			return id + "[" + capacity + "]" ;
		} else {
			return id;
		}
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}

}