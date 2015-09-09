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

/* This class provides a set of tests for the CTds Parser */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CTdsParserTest {

	@BeforeClass
    public static void initTest() {
        System.out.println("----------------------------------- Testing CTdsParser ---------------------------------");;
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
	 * Test the grammar rules that involves class declarations. 
	 */
	@Test
	public void ClassDeclarationOkTest() throws IOException {
		assertEquals(genericTest("ClassDeclarationOk.in"),true);
	}

	@Test
	public void ClassDeclarationWithErrorTest() throws IOException {
		assertEquals(genericTest("ClassDeclarationError.in"),false);
	}

	/* 
	 * Test the grammar rules that involves fields declarations. 
	 */
	@Test
	public void FieldDeclarationOkTest() throws IOException {
		assertEquals(genericTest("FieldDeclarationOk.in"),true);
	}

	@Test
	public void FieldDeclarationWithErrorTest() throws IOException {
		assertEquals(genericTest("FieldDeclarationError.in"),false);
	}


	/* 
	 * Test the grammar rules that involves methods declarations.
	 */
	@Test 
	public void MethodDeclarationOkTest() throws IOException {
		assertEquals(genericTest("MethodDeclarationOk.in"),true);
	}

	@Test 
	public void MethodDeclarationWithErrorTest() throws IOException {
		assertEquals(genericTest("MethodDeclarationError.in"),false);
	}

	/* 
	 * Test the grammar rules that involves declarations of methods with field
	 * declarations in the body.
	 */
	@Test 
	public void MethodBodyFieldDeclOkTest() throws IOException {
		assertEquals(genericTest("MethodBodyFieldDeclOk.in"),true);
	}

	@Test 
	public void MethodBodyFieldDeclWithErrorTest() throws IOException {
		assertEquals(genericTest("MethodBodyFieldDeclError.in"),false);
	}

	/* 
	 * Test the grammar rules that involves declarations of methods with statements
	 * declarations in the body 
	 */
	@Test 
	public void StatementAssignmentOkTest() throws IOException {
		assertEquals(genericTest("StatementAssignmentOk.in"),true);
	}

	@Test 
	public void StatementAssignmentWithErrorTest() throws IOException {
		assertEquals(genericTest("StatementAssignmentError.in"),false);
	}

	@Test 
	public void StatementMethodCallOkTest() throws IOException {
		assertEquals(genericTest("StatementMethodCallOk.in"),true);
	}

	@Test 
	public void StatementMethodCallWithErrorTest() throws IOException {
		assertEquals(genericTest("StatementMethodCallError.in"),false);
	}

	@Test 
	public void StatementIfOkTest() throws IOException {
		assertEquals(genericTest("StatementIfOk.in"),true);
	}

	@Test 
	public void StatementIfWithErrorTest() throws IOException {
		assertEquals(genericTest("StatementIfError.in"),false);
	}

	@Test 
	public void StatementLoopsOkTest() throws IOException {
		assertEquals(genericTest("StatementLoopsOk.in"),true);
	}

	@Test 
	public void StatementLoopsWithErrorTest() throws IOException {
		assertEquals(genericTest("StatementLoopsError.in"),false);
	}

	@Test 
	public void StatementExpresionOkTest() throws IOException {
		assertEquals(genericTest("StatementExpressionOk.in"),true);
	}

	@Test 
	public void StatementExpressionErrorTest() throws IOException {
		assertEquals(genericTest("StatementExpressionError.in"),false);
	}

	/* 
	* Execute the parser for the given input file and returns true if the 
	* the string of text of the input file belongs to the language or false
	* in otherwise.
	*/
	private boolean genericTest(String inputFile) throws IOException {
		String[] argv = new String[1];
		argv[0] = "../src/test/resource/parser/"+inputFile;

		boolean do_debug_parse = false;
    
		CTdsParser parser = new CTdsParser(new CTdsScanner(new FileReader(argv[0])));
		parser.errors = 0;
		Symbol parseTree = null;
		try {
			if (do_debug_parse)
				 parseTree = parser.debug_parse();
			else 
				parseTree = parser.parse();
			System.out.println("Errors: " + parser.errors);
			return true;
		} catch (Exception e) {
			System.out.println("Errors: " + parser.errors);
			return false;
		}
	}

}