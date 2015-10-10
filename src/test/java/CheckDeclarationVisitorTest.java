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
 * This class provides a set of tests for the CheckDeclarationVisitor 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CheckDeclarationVisitorTest {

	@BeforeClass
    public static void initTest() {
        System.out.println("----------------- Testing CheckDeclarationVisitor -----------------");
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
	 * Test that an error is founded when a class is already defined 
	 */
	@Test
	public void AlreadyDefinedClassTest() throws IOException {
		assertEquals(genericTest("AlreadyDefinedClass.ctds"),true);
	}

	/**
	 * Test that an error is founded when a field is already defined
	 */
	@Test
	public void AlreadyDefinedFieldTest() throws IOException {
		assertEquals(genericTest("AlreadyDefinedField.ctds"),true);
	}

	/**
	 * Test that an error is founded when an array is already defined
	 */
	@Test
	public void AlreadyDefinedFieldArrayTest() throws IOException {
		assertEquals(genericTest("AlreadyDefinedFieldArray.ctds"),true);
	}

	/**
	 * Test that an error is founded when a field is not declared
	 */
	@Test
	public void FieldNotDeclaredTest() throws IOException {
		assertEquals(genericTest("FieldNotDeclared.ctds"),true);
	}

	/**
	 * Test that an error is founded when a field array is not declared
	 */
	@Test
	public void FieldArrayNotDeclaredTest() throws IOException {
		assertEquals(genericTest("FieldArrayNotDeclared.ctds"),true);
	}

	/**
	 * Test that there are no errors when a field is declared with the same name
	 * but in different blocks
	 */
	@Test
	public void FieldDeclaredSameNameDifferentBlockTest() throws IOException {
		assertEquals(genericTest("FieldDeclaredSameNameDifferentBlock.ctds"),false);
	}

	/**
	 * Test that an error is founded when a method is already declared
	 */
	@Test
	public void AlreadyDefinedMethodTest() throws IOException {
		assertEquals(genericTest("AlreadyDefinedMethod.ctds"),true);
	}

	/**
	 * Test that an error is founded when a method is not declared
	 */
	@Test
	public void MethodNotDeclaredTest() throws IOException {
		assertEquals(genericTest("MethodNotDeclared.ctds"),true);
	}

	/**
	 * Test that an error is founded when a method not have a return statement
	 */
	@Test
	public void MissingReturnStatementTest() throws IOException {
		assertEquals(genericTest("MissingReturn.ctds"),true);
	}

	/**
	 * Test that an error is founded when a method have to arguments with the same name
	 */
	@Test
	public void MethodWithIncorrectArgumentsTest() throws IOException {
		assertEquals(genericTest("MethodArgumentsSameName.ctds"),true);
	}

	/**
	 * Test that an error is founded when a field is not defined in other class
	 */
	@Test
	public void FieldNotDefinedOtherClassTest() throws IOException {
		assertEquals(genericTest("FieldNotDefinedOtherClass.ctds"),true);
	}

	/**
	 * Test that there are no errors when a field is defined in other class
	 */
	@Test
	public void FieldDefinedOtherClassTest() throws IOException {
		assertEquals(genericTest("FieldDefinedOtherClass.ctds"),false);
	}

	/**
	 * Test that an error is founded when a method is not defined in other class
	 */
	@Test
	public void MethodNotDefinedOtherClassTest() throws IOException {
		assertEquals(genericTest("MethodNotDefinedOtherClass.ctds"),true);
	}

	/**
	 * Test that there are no errors when a method is defined in other class
	 */
	@Test
	public void MethodDefinedOtherClassTest() throws IOException {
		assertEquals(genericTest("MethodDefinedOtherClass.ctds"),false);
	}

	/**
	 * Test that an error is founded when a break statement is outside of a cycle
	 */
	@Test
	public void BreakOutsideOfCycleTest() throws IOException {
		assertEquals(genericTest("BreakOutsideOfCycle.ctds"),true);
	}

	/**
	 * Test that an error is founded when a continue statement is outside of a cycle
	 */
	@Test
	public void ContinueOutsideOfCycleTest() throws IOException  {
		assertEquals(genericTest("ContinueOutsideOfCycle.ctds"),true);
	}

	/* 
	* Execute the check declaration visitor for the given input file and returns 
	* true if an error is founded.
	*/
	public static boolean genericTest(String inputFile) throws IOException {
		String[] argv = new String[1];
		argv[0] = "../src/test/resource/visitor/declaration/"+inputFile;
    	LinkedList<String> errors = new LinkedList<String>();
		try {
			
			CTdsParser parser = new CTdsParser(new CTdsScanner(new FileReader(argv[0])));
			Program program = (Program)parser.parse().value;

			CheckDeclarationVisitor declarationVisitor = new CheckDeclarationVisitor();
 			errors.addAll(declarationVisitor.visit(program));

 			
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