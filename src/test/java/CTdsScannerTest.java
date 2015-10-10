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

/* 
 * This class provides a set of tests for the CTds Scanner
 * @author Facundo Molina
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CTdsScannerTest {

	@BeforeClass
    public static void initTest() {
        System.out.println("----------------- Testing CTdsScanner -----------------");
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

	@Test
	public void testLiterals() throws IOException {
		assertEquals(genericTest("literals.in","literals.out","literals.expected"),true);	
	}

	@Test 
	public void testKeywords() throws IOException {
		assertEquals(genericTest("keywords.in","keywords.out","keywords.expected"),true);
	}

	@Test 
	public void testOpertors() throws IOException {
		assertEquals(genericTest("operators.in","operators.out","operators.expected"),true);
	}

	@Test
	public void testDelimiters() throws IOException {
		assertEquals(genericTest("delimiters.in","delimiters.out","delimiters.expected"),true);
	}

	@Test
	public void testIdentifiers() throws IOException {
		assertEquals(genericTest("identifiers.in","identifiers.out","identifiers.expected"),true);
	}

	/* Scan a file and returns true if the scanned file and the expected file are equals */
	private boolean genericTest(String inputFile,String outputFile,String expectedFile) throws IOException {
		String[] argv = new String[1];
		argv[0] = "../src/test/resource/scanner/"+inputFile;
		File actual = new File("../src/test/resource/scanner/"+outputFile);
		actual.delete();
		FileOutputStream fos = new FileOutputStream("../src/test/resource/scanner/"+outputFile, true);
		System.setOut(new PrintStream(fos));

		CTdsScannerStandalone.main(argv);

		fos.close();

		BufferedReader actualContent = new BufferedReader(new FileReader(
				actual));

		/* The expected result is in a file*/
		Reader expected = new FileReader("../src/test/resource/scanner/"+expectedFile);
		BufferedReader expectedContent = new BufferedReader(expected);

		String expectedLine, actualLine;

		boolean result = true;
		do {
			expectedLine = expectedContent.readLine();
			actualLine = actualContent.readLine();

			if (expectedLine != null) {
				result = expectedLine.equals(actualLine);
			}
			
		} while ((expectedLine != null) && result);

		return result;
	}

}