import java.util.List;
import java.util.LinkedList;

/**
 * This class represents a program: class ID { .. } class ID' { .. } ..
 * @author Facundo Molina
 */
public class Program extends AST {
	
	private List<ClassDeclaration> classDeclarations; 		// List of class declarations

	/**
	 * Constructor
	 */
	public Program() {
		classDeclarations = new LinkedList<ClassDeclaration>();
	}

	/**
	 * Constructor with a given list of class declarations
	 */
	public Program(List<ClassDeclaration> l) {
		classDeclarations = l;
	}

	/**
	 * Get the class declarations
	 */
	public List<ClassDeclaration> getClassDeclarations() {
		return classDeclarations;
	}

	/**
	 * Set the class declarations
	 */
	public void setClassDeclarations(List<ClassDeclaration> l) {
		classDeclarations = l;
	}

	/**
	 * Add a new class declaration to the program.
	 */
	public void addClassDeclaration(ClassDeclaration cd) {
		classDeclarations.add(0,cd);
	}
	
	@Override
	public String toString() {
		String programString = "";
		for (ClassDeclaration classDeclaration : classDeclarations) {
			programString += classDeclaration.toString() + "\n";
		}
		return programString;
	}

	@Override
	public <T> T accept(ASTVisitor<T> v) {
		return v.visit(this);
	}
	
}