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

/* This class provides a set of tests for the CTds Parser */
public class CTdsParserTest {

	/* 
	 * Test the grammar rules that involves class declarations. 
	 */
	@Test
	public void ClassDeclarationOkTest() throws IOException {
		assertEquals(genericTest("ClassDeclarationOk.in"),true);
	}

	@Test
	public void ClassDeclarationErrorTest() throws IOException {
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
	public void FieldDeclarationErrorTest() throws IOException {
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
	public void MethodDeclarationErrorTest() throws IOException {
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
	public void MethodBodyFieldDeclErrorTest() throws IOException {
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
	public void StatementAssignmentErrorTest() throws IOException {
		assertEquals(genericTest("StatementAssignmentError.in"),false);
	}

	@Test 
	public void StatementMethodCallOkTest() throws IOException {
		assertEquals(genericTest("StatementMethodCallOk.in"),true);
	}

	@Test 
	public void StatementMethodCallErrorTest() throws IOException {
		assertEquals(genericTest("StatementMethodCallError.in"),false);
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