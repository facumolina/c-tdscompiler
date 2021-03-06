import java.util.LinkedList;
import java.util.ArrayList;

/**
 * This class represents the symbols table, which is as an array of linked lists.
 * @author Facundo Molina
 */
public class SymbolsTable {
	
	private ArrayList<LinkedList<AST>> table;      // Symbols table represented as an array of linked lists
	private int currentLevel; 					   // Current level, for simulate the stack behaviour

	/**
	 * Constructor
	 */
	public SymbolsTable() {
		table = new ArrayList<LinkedList<AST>>();
		currentLevel = 0;
		table.add(currentLevel,new LinkedList<AST>());
	}

	/**
	 * Get the level
	 */
	public int getLevel() {
		return currentLevel;
	}
	
	/**
	 * Increment the level
	 */
	public void incrementLevel() {
		currentLevel++;
		table.add(currentLevel,new LinkedList<AST>());
	}

	/**
	 * Decrement the level
	 */
	public void decrementLevel() {
		table.get(currentLevel).clear();
		currentLevel--;
	}

	/**
	 * Adds a symbol in the given level.
	 */
	public boolean addSymbol(Identifiable symbol) {
		if (!exists(symbol,currentLevel)) {
			table.get(currentLevel).add(symbol);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Get the symbols list at a given livel.
	 */
	public LinkedList<AST> getLevelSymbols(int level) {
		return table.get(level);
	}

	/**
	 * Empty the table
	 */
	public void makeEmpty() {
		table.clear();
	}

	/**
	 * Empty level
	 */
	public void emptyCurrentLevel() {
		table.get(currentLevel).clear();
	}

	/**
	 * Returns true if an ast element with the same id already exists at the given level. 
	 */
	private boolean exists(Identifiable i,int level) {
		for (AST a : table.get(level)) {
			Identifiable j = (Identifiable)a;
			if (i.getId().equals(j.getId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Search a symbol by id and returns it if it exists. Otherwise returns null
	 */
	public Identifiable searchSymbol(String id,int level) {
		for (AST a : table.get(level)) {
			Identifiable i = (Identifiable)a;
			if (i.getId().equals(id)) {
				return i;
			} 
		}
		return null;
	}
	
}