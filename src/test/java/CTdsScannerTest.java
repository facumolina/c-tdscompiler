//package de.jflex.example.standalone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import junit.framework.TestCase;



public class CTdsScannerTest extends TestCase {
	

	public void testSample() throws IOException {
		
		String[] argv = new String[1];
		argv[0] = "../src/test/resource/sample.in";
		File actual = new File("sample.out");
		actual.delete();
		FileOutputStream fos = new FileOutputStream("sample.out", true);
		System.setOut(new PrintStream(fos));

		CTdsScanner.main(argv);

		fos.close();

		BufferedReader actualContent = new BufferedReader(new FileReader(
				actual));

		// the expected result is in a file
		Reader expected = new FileReader("../src/test/resource/sample.expected");
		BufferedReader expectedContent = new BufferedReader(expected);

		String expectedLine, actualLine;
		do {
			expectedLine = expectedContent.readLine();
			actualLine = actualContent.readLine();

			assertEquals(expectedLine, actualLine);
		} while (expectedLine != null);
		
	}

}
