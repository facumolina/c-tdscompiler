import java_cup.runtime.Symbol;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Rule;
import org.junit.FixMethodOrder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import java.util.LinkedList;

/* 
 * This class provides a set of tests for the CheckTypeVisitor 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CheckTypeVisitorTest {

	@BeforeClass
    public static void initTest() {
        System.out.println("----------------------------------- Testing CheckTypeVisitor ---------------------------------");
    	System.out.println();
    }

	@Rule
	public TestRule watcher = new TestWatcher() {
   		protected void starting(Description description) {
    		System.out.println("Starting test: " + description.getMethodName());
   		}
	};

	@After
	public void after() {
		System.out.println();
	}

	/* 
	 * Test that an error is founded in an assignment with a literal
	 * of different type than the location 
	 */
	@Test
	public void DifferentTypeAssignmentTest() throws IOException {
		assertEquals(genericTest("DifferentTypeAssignmentLiteral.ctds"),true);
	}

	/* 
	 * Test there are no erros in an assignment with a literal
	 * of the same type than the location 
	 */
	@Test
	public void SameTypeAssignmentTest() throws IOException {
		assertEquals(genericTest("SameTypeAssignmentLiteral.ctds"),false);
	}

	/**
	 * Test that an error is founded in an assignment with a location expression
	 * of different type than the location 
	 */
	@Test
	public void DifferentTypeAssignmentLocationExprTest() throws IOException {
		assertEquals(genericTest("DifferentTypeAssignmentLocationExpr.ctds"),true);
	}

	/**
	 * Test that there are no errors in an assignment with a location expression
	 * of the same type than the location 
	 */
	@Test
	public void SameTypeAssignmentLocationExprTest() throws IOException {
		assertEquals(genericTest("SameTypeAssignmentLocationExpr.ctds"),false);
	}

	/**
	 * Test that an error is founded in an assignment with a method call
	 * of different type than the location 
	 */
	@Test
	public void DifferentTypeAssignmentMethodCallTest() throws IOException {
		assertEquals(genericTest("DifferentTypeAssignmentMethodCall.ctds"),true);
	}

	/**
	 * Test there are no errors in an assignment with a method call
	 * of the same type than the location 
	 */
	@Test
	public void SameTypeAssignmentMethodCallTest() throws IOException {
		assertEquals(genericTest("SameTypeAssignmentMethodCall.ctds"),false);
	}

	/**
	 * Test that an error is founded when a return statement has different type
	 * than the method
	 */
	@Test
	public void DifferentTypeReturnStatementTest() throws IOException {
		assertEquals(genericTest("DifferentTypeReturnStmt.ctds"),true);		
	}

	/**
	 * Test that there are no errors when a return statement has the same type
	 * than the method
	 */
	@Test
	public void SameTypeReturnStatementTest() throws IOException {
		assertEquals(genericTest("SameTypeReturnStmt.ctds"),false);		
	}


	/**
	 * Test that an error is founded when in a if statement the condition 
	 * type is not boolean
	 */
	@Test
	public void NotBooleanConditionIfStatementTest() throws IOException {
		assertEquals(genericTest("NotBooleanConditionIfStmt.ctds"),true);		
	}

	/**
	 * Test that there are no errors when in a if statement the condition 
	 * type is boolean
	 */
	@Test
	public void BooleanConditionIfStatementTest() throws IOException {
		assertEquals(genericTest("BooleanConditionIfStmt.ctds"),false);		
	}

	/**
	 * Test that an error is founded when in a for statement the condition 
	 * type is not boolean
	 */
	@Test
	public void NotBooleanConditionForStatementTest() throws IOException {
		assertEquals(genericTest("NotBooleanConditionForStmt.ctds"),true);		
	}

	/**
	 * Test that there are no errors when in a for statement the condition 
	 * type is boolean
	 */
	@Test
	public void BooleanConditionForStatementTest() throws IOException {
		assertEquals(genericTest("BooleanConditionForStmt.ctds"),false);		
	}

	/**
	 * Test that an error is founded when in a while statement the condition 
	 * type is not boolean
	 */
	@Test
	public void NotBooleanConditionWhileStatementTest() throws IOException {
		assertEquals(genericTest("NotBooleanConditionWhileStmt.ctds"),true);		
	}

	/**
	 * Test that there are no errors when in a while statement the condition 
	 * type is boolean
	 */
	@Test
	public void BooleanConditionWhileStatementTest() throws IOException {
		assertEquals(genericTest("BooleanConditionWhileStmt.ctds"),false);		
	}

	/**
	 * Test that an error is founded when a method is invoked with
	 * different arity
	 */
	@Test
	public void DifferentArityMethodCallTest() throws IOException  {
		assertEquals(genericTest("DifferentArityMethodCall.ctds"),true);		
	}

	/**
	 * Test that an error is founded when a method is invoked with
	 * arguments with incorrect types
	 */
	@Test
	public void DifferentArgumentsTypeMethodCallTest() throws IOException  {
		assertEquals(genericTest("DifferentArgumentsTypeMethodCall.ctds"),true);		
	}

	/**
	 * Test that there are no errors when a method is invoked with
	 * arguments with correct types
	 */
	@Test
	public void SameArgumentsTypeMethodCallTest() throws IOException  {
		assertEquals(genericTest("SameArgumentsTypeMethodCall.ctds"),false);		
	}

	/**
	 * Test that an error is founded when some logical operator is used with not 
	 * boolean expressions
	 */
	@Test
	public void BooleanOperatorWithNonBooleanExprTest() throws IOException  {
		assertEquals(genericTest("BooleanOperatorWithNonBooleanExpr.ctds"),true);		
	}

	/**
	 * Test that there are no errors when some logical operator is used with boolean
	 * expressions
	 */
	@Test
	public void BooleanOperatorWithBooleanExprTest() throws IOException  {
		assertEquals(genericTest("BooleanOperatorWithBooleanExpr.ctds"),false);		
	}

	/**
	 * Test that an error is founded when some arithmetical operator is used
	 * with non numerical expressions
	 */
	@Test
	public void ArithmeticalOperatorWithNonNumericalExprTest() throws IOException  {
		assertEquals(genericTest("ArithmeticalOperatorWithNonNumericalExpr.ctds"),true);		
	}

	/**
	 * Test that there are no errors when some arithmetical operator is used
	 * with numerical expressions
	 */
	@Test
	public void ArithmeticalOperatorWithNumericalExprTest() throws IOException  {
		assertEquals(genericTest("ArithmeticalOperatorWithNumericalExpr.ctds"),false);		
	}

	/* 
	* Execute the check type visitor for the given input file and returns 
	* true if an error is founded.
	*/
	public static boolean genericTest(String inputFile) throws IOException {
		String[] argv = new String[1];
		argv[0] = "../src/test/resource/visitor/type/"+inputFile;
    	LinkedList<String> errors = new LinkedList<String>();
		try {
			
			CTdsParser parser = new CTdsParser(new CTdsScanner(new FileReader(argv[0])));
			Program program = (Program)parser.parse().value;

			CheckDeclarationVisitor declarationVisitor = new CheckDeclarationVisitor();
 			errors.addAll(declarationVisitor.visit(program));

			CheckTypeVisitor typeVisitor = new CheckTypeVisitor();
 			errors.addAll(typeVisitor.visit(program));

 			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (errors.size()==1) {
			System.out.println("Error founded, as expected: ");
			System.out.println(errors.get(0));
			return true;
		} else {
			return false;
		}
		
	}

}