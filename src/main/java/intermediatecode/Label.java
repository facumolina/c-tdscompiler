/**
* This class represents a label (line number) 
* @author Facundo Molina 
*/
public class Label {

	private int number;

	/**
	 * Constructor 
	 */
	public Label(int n) {
		if (n<0) throw new IllegalArgumentException();
		number = n;
	}
	
	/**
	 * Get number 
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Set number 
	 */
	public void setNumber(int n) {
		if (n<0) throw new IllegalArgumentException();
		number = n;
	}

	@Override
	public String toString() {
		return "L"+number;
	}

}