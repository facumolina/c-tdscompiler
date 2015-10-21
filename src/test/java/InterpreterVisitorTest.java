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
 * This class provides a set of tests for the InterpreterVisitor
 * @author Facundo Molina
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InterpreterVisitorTest {

	@BeforeClass
    public static void initTest() {
        System.out.println("----------------- Testing InterpreterVisitor -----------------");
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
	 * Test that the assign statement is performed correctly
	 */
	@Test
	public void AssignStatementTest() throws IOException {
		assertEquals(genericTest("AssignStatement.ctds"),false);
	}

	/* 
	* Execute the interpreter visitor for the given input file and returns 
	* true if an error is founded.
	*/
	public static boolean genericTest(String inputFile) throws IOException {
		String[] argv = new String[1];
		argv[0] = "../src/test/resource/visitor/interpreter/"+inputFile;
    	LinkedList<String> errors = new LinkedList<String>();
		try {
			
			CTdsParser parser = new CTdsParser(new CTdsScanner(new FileReader(argv[0])));
			Program program = (Program)parser.parse().value;

			CheckDeclarationVisitor declarationVisitor = new CheckDeclarationVisitor();
 			errors.addAll(declarationVisitor.visit(program));

			CheckTypeVisitor typeVisitor = new CheckTypeVisitor();
 			errors.addAll(typeVisitor.visit(program));

 			InterpreterVisitor interpreter = new InterpreterVisitor();
 			errors.addAll(interpreter.visit(program));

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