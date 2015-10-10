/**
 * This class represents the literals:
 *  - Boolean literal
 *  - Int literal
 *  - Float literal
 * @author Facundo Molina
 */
public abstract class Literal extends Expression {
	
	/*
	 * @return: returns Type of Literal instance
	 */
	public abstract Type getType();

}