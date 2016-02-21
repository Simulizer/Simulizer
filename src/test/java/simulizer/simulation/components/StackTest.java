package simulizer.simulation.components;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import simulizer.assembler.representation.Address;
import simulizer.simulation.cpu.components.StackSegment;
import simulizer.simulation.exceptions.StackException;

/**this class is aimed at carrying out unit tests
 * on the stack segment in the CPU
 * @author Charlie Street
 *
 */
public class StackTest {

	
	/**creates a new stack object for use in the tests
	 * 
	 * @param stackPointer the initial stack pointer
	 * @param lowestAddress the lowest address the stack can go to 
	 * @return the stack segment used for tests
	 */
	public StackSegment createStack(int stackPointer, int lowestAddress)
	{
		return new StackSegment(new Address(stackPointer), new Address(lowestAddress));
	}
	
	/**tests set up of stack is correct
	 * it also tests size works but this is trivial
	 */
	@Test
	public void testInitialStack()
	{
		StackSegment stack = createStack(10,0);
		assertEquals(4,stack.size());
	}
	
	/**this method will test the getBytes method of the stack
	 * @throws StackException 
	 * 
	 */
	@Test
	public void testGetBytes() throws StackException
	{
		{//valid read
			StackSegment stack = createStack(10,0);
			stack.setBytes(0,new byte[]{0x11,0x10,0x21,0x20});
			assertEquals(0x11,stack.getBytes(0,4)[0]);
			assertEquals(0x10,stack.getBytes(0,4)[1]);
			assertEquals(0x21,stack.getBytes(0,4)[2]);
			assertEquals(0x20,stack.getBytes(0,4)[3]);
		}
		
		{//invalid read (invalid at start of read)
			StackSegment stack = createStack(10,0);
			stack.setBytes(2, new byte[]{0x11,0x20,0x30,0x40});
			assertEquals(6,stack.size());
			try
			{
				stack.getBytes(8,4);
				fail();//fail test if no exception thrown
			} catch(StackException e) {
				assertTrue(e.getMessage().equals("Invalid read on stack."));//if exception properly thrown (checks correct exception)
			}
		}
		
		{//invalid read 2 (invalid half way through read)
			StackSegment stack = createStack(10,0);
			stack.setBytes(2, new byte[]{0x11,0x20,0x30,0x40});
			assertEquals(6,stack.size());
			try
			{
				stack.getBytes(4,4);
				fail();//fail test if no exception thrown
			} catch(StackException e) {
				assertTrue(e.getMessage().equals("Invalid read on stack."));//if exception properly thrown
			}
		}
	}
	
	/**method will test the set bytes method of the stack segment
	 * @throws StackException 
	 * 
	 */
	@Test
	public void testSetBytes() throws StackException
	{
		{//valid write (no growth of stack)
			StackSegment stack = createStack(10,0);
			assertEquals(4,stack.size());
			stack.setBytes(0, new byte[]{0x11,0x10,0x11,0x10});
			assertEquals(4,stack.size());
			assertEquals(0x11,stack.getBytes(0, 4)[0]);
			assertEquals(0x10,stack.getBytes(0, 4)[1]);
			assertEquals(0x11,stack.getBytes(0, 4)[2]);
			assertEquals(0x10,stack.getBytes(0, 4)[3]);
		}
		
		{//valid write (growth of stack)
			StackSegment stack = createStack(10,0);
			assertEquals(4,stack.size());
			stack.setBytes(2,new byte[]{0x00,0x11,0x10,0x01});
			assertEquals(6,stack.size());
			assertEquals(0x00,stack.getBytes(2, 4)[0]);
			assertEquals(0x11,stack.getBytes(2, 4)[1]);
			assertEquals(0x10,stack.getBytes(2, 4)[2]);
			assertEquals(0x01,stack.getBytes(2, 4)[3]);
		}
		
		{//valid write (outside force growth)
			StackSegment stack = createStack(10,0);
			assertEquals(4,stack.size());
			stack.setBytes(5,new byte[]{0x11,0x10});
			assertEquals(7,stack.size());
			assertEquals(0x00,stack.getBytes(4, 1)[0]);
			assertEquals(0x11,stack.getBytes(5, 2)[0]);
			assertEquals(0x10,stack.getBytes(5, 2)[1]);
		}
		
		{//stack overflow test
			StackSegment stack = createStack(10,0);
			assertEquals(4,stack.size());
			try
			{
				stack.setBytes(0, new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00});
				fail();
			} catch(Exception e) {
				assertTrue(e.getMessage().equals("Stack overflow."));
			}
		}
		
		{//stack overflow test 2 (in more stages)
			StackSegment stack = createStack(10,0);
			assertEquals(4,stack.size());
			try
			{
				stack.setBytes(0, new byte[]{0x00,0x00,0x00,0x00,0x00});
				stack.setBytes(5, new byte[]{0x00,0x00,0x00,0x00,0x00});
				stack.setBytes(10, new byte[]{0x00});
				fail();
			} catch(Exception e) {
				assertTrue(e.getMessage().equals("Stack overflow."));
			}
		}
	}
	
}
