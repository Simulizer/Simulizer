package simulizer.simulation.components;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.AfterClass;
import org.junit.BeforeClass;

/**class will aim to test the execute functionality of the cpu
 * since it is so dependent on large amounts of the cpu
 * it will be tested as follows: small programs will be made to isolate
 * each intruction which can be executed, since decode is successfully tested
 * and the fetch is trivial, then this will test only the execute
 * it will then verify the results by looking at points of interest in the cpu
 * and checking registers/memory have been set correctly in accordance with the operation executed
 * these tests assume that decode and the assembler work as intended; they have been tested and so 
 * we have confidence that they do indeed work
 * @author Charlie Street
 *
 */
public class ExecuteTest {

	private PrintStream oldOut;
	private ByteArrayOutputStream redirected;
	
	/**method is used to redirect the system.out stream so I can analyse it in tests
	 * particularly usedul for syscall tests
	 * carried out before start of tests
	 */
	@BeforeClass
	private void redirectOutput()
	{
		this.oldOut = System.out;
		this.redirected = new ByteArrayOutputStream();
		PrintStream newStream = new PrintStream(this.redirected);
		System.setOut(newStream);
	}
	
	/**method to set back to old print stream, i.e standard system.out
	 * this method is just for completeness and will be run at the end of the tests
	 */
	@AfterClass
	private void resetOutput()
	{
		System.out.flush();
		System.setOut(this.oldOut);
	}
	
}
