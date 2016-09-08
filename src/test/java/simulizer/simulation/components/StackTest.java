package simulizer.simulation.components;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import category.UnitTests;
import simulizer.simulation.cpu.components.StackSegment;
import simulizer.simulation.exceptions.StackException;

import java.lang.reflect.Field;

/**this class is aimed at carrying out unit tests
 * on the stack segment in the CPU
 * @author Charlie Street
 *
 */
@Category({UnitTests.class})
public class StackTest {

	private void setStack(StackSegment seg, byte[] newStack) {
		try {
			Field stackField = StackSegment.class.getDeclaredField("stack");

            stackField.setAccessible(true);
            stackField.set(seg, newStack);

		} catch (IllegalAccessException | NoSuchFieldException e) {
		    fail();
		}
	}

	/**this method will test the getBytes method of the stack
	 * @throws StackException 
	 * 
	 */
	@Test
	public void testGetBytes() throws StackException
	{
		{//valid read
			StackSegment stack = new StackSegment(10);
            // addresses are relative to the top of the stack
			stack.setBytes(-4,new byte[]{0x11,0x10,0x21,0x20});
            byte[] read = stack.getBytes(-4,4);
			assertEquals(4, read.length);
			assertEquals(0x11,read[0]);
			assertEquals(0x10,read[1]);
			assertEquals(0x21,read[2]);
			assertEquals(0x20,read[3]);
		}
		
		{//invalid read (attempt to read above the stack)
			StackSegment stack = new StackSegment(10);
			try
			{
				stack.getBytes(-2,4); // attempt to read above the stack
				fail();//fail test if no exception thrown
			} catch(StackException e) {
				assertTrue(e.getMessage().equals("Invalid read on stack. (attempt to read above the top)"));
			}

			try
			{
				stack.getBytes(0,4); // attempt to read above the stack
				fail();//fail test if no exception thrown
			} catch(StackException e) {
				assertTrue(e.getMessage().equals("Invalid read on stack. (attempt to read above the top)"));
			}
		}

		{//invalid read (attempt to read past the maximum size for the stack segment)
			StackSegment stack = new StackSegment(10);
			try
			{
				stack.getBytes(-100,4); // attempt to read below the maximum size the segment can grow to
				fail();//fail test if no exception thrown
			} catch(StackException e) {
				assertTrue(e.getMessage().equals("Stack Overflow. (attempt to read from the stack beyond its maximum length)"));
			}

			try
			{
				stack.getBytes(-11,4); // attempt to read below the maximum size the segment can grow to
				fail();//fail test if no exception thrown
			} catch(StackException e) {
				assertTrue(e.getMessage().equals("Stack Overflow. (attempt to read from the stack beyond its maximum length)"));
			}
		}

		{//invalid read (attempt to read past the maximum size for the stack segment _and_ above the stack segment)
			StackSegment stack = new StackSegment(10);
			try
			{
				stack.getBytes(-20,40); // attempt to read below and above the stack
				fail();//fail test if no exception thrown
			} catch(StackException e) {
				assertTrue(
						e.getMessage().equals("Stack Overflow. (attempt to read from the stack beyond its maximum length)") ||
				        e.getMessage().equals("Invalid read on stack. (attempt to read above the top)"));
			}
		}


		{//invalid read (non-positive length)
			StackSegment stack = new StackSegment(10);
			try
			{
				stack.getBytes(-2,0); // length = 0
				fail();//fail test if no exception thrown
			} catch(StackException e) {
				assertTrue(e.getMessage().equals("Invalid read on stack. (non-positive length)"));
			}
			try
			{
				stack.getBytes(-2,-2); // length = -2
				fail();//fail test if no exception thrown
			} catch(StackException e) {
				assertTrue(e.getMessage().equals("Invalid read on stack. (non-positive length)"));
			}
		}

		{//valid read (half below currently allocated stack)
			StackSegment stack = new StackSegment(10); // can grow to 10 bytes
            setStack(stack, new byte[5]);
            // stack length 5
			stack.setBytes(-5, new byte[]{0x11,0x20,0x30,0x40,0x50});
			byte[] read = stack.getBytes(-7,4);
			assertEquals(4, read.length);
			assertEquals(0x00,read[0]);
			assertEquals(0x00,read[1]);
			assertEquals(0x11,read[2]);
			assertEquals(0x20,read[3]);

		}

		{//valid read (all below currently allocated stack)
			StackSegment stack = new StackSegment(10); // can grow to 10 bytes
			setStack(stack, new byte[5]);
			// stack length 5
			stack.setBytes(-5, new byte[]{0x11,0x20,0x30,0x40,0x50});
			byte[] read = stack.getBytes(-10,5);
			assertEquals(5, read.length);
			assertEquals(0x00,read[0]);
			assertEquals(0x00,read[1]);
			assertEquals(0x00,read[2]);
			assertEquals(0x00,read[3]);
			assertEquals(0x00,read[4]);
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
			StackSegment stack = new StackSegment(10);
			stack.setBytes(-4, new byte[]{0x11,0x10,0x11,0x10});
			byte[] read = stack.getBytes(-4, 4);
			assertEquals(0x11,read[0]);
			assertEquals(0x10,read[1]);
			assertEquals(0x11,read[2]);
			assertEquals(0x10,read[3]);
		}

		{//valid write: test big endian
			StackSegment stack = new StackSegment(10);
            // imagine rotating 90 degrees anti-clockwise
			stack.setBytes(-2, new byte[]{0x11,0x22});
			// topOfStack
			// 0x22
			// 0x11   <- $sp
			assertEquals(0x11,stack.getBytes(-2, 1)[0]);
			assertEquals(0x22,stack.getBytes(-1, 1)[0]);
		}

		{//valid write (growth of stack)
			StackSegment stack = new StackSegment(10);
            setStack(stack, new byte[5]);
			stack.setBytes(-10,new byte[]{0x00,0x11,0x10,0x01});
			byte[] read = stack.getBytes(-10, 4);
			assertEquals(0x00,read[0]);
			assertEquals(0x11,read[1]);
			assertEquals(0x10,read[2]);
			assertEquals(0x01,read[3]);
		}

		{//invalid write (non-positive length)
			StackSegment stack = new StackSegment(10); // can grow to 10 bytes
			try {
				stack.setBytes(-2, new byte[]{});
				fail();
			} catch(StackException e) {
				assertTrue(e.getMessage().equals("Invalid write on stack. (non-positive length)"));
			}
		}

		{//invalid write (above the stack)
			StackSegment stack = new StackSegment(10); // can grow to 10 bytes
			try {
				stack.setBytes(-2, new byte[]{0x11, 0x22, 0x33, 0x44});
				fail();
			} catch(StackException e) {
				assertTrue(e.getMessage().equals("Invalid write to stack. (attempt to write above the top)"));
			}

			try {
				stack.setBytes(0, new byte[]{0x11, 0x22, 0x33, 0x44});
				fail();
			} catch(StackException e) {
				assertTrue(e.getMessage().equals("Invalid write to stack. (attempt to write above the top)"));
			}
		}

		{//invalid write (stack overflow (past the maximum length the stack can grow to))
			StackSegment stack = new StackSegment(10); // can grow to 10 bytes
            try {
				stack.setBytes(-20, new byte[]{0x11, 0x10});
				fail();
            } catch(StackException e) {
                assertTrue(e.getMessage().equals("Stack Overflow. (attempt to write to the stack beyond its maximum length)"));
            }
		}


		{//invalid read (attempt to read past the maximum size for the stack segment _and_ above the stack segment)
			StackSegment stack = new StackSegment(10);
			try
			{
				stack.setBytes(-20, new byte[40]);
				fail();//fail test if no exception thrown
			} catch(StackException e) {
				assertTrue(
						e.getMessage().equals("Stack Overflow. (attempt to write to the stack beyond its maximum length)") ||
						e.getMessage().equals("Invalid write to stack. (attempt to write above the top)"));
			}
		}
	}

}
