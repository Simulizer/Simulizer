package simulizer.simulation.components;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import simulizer.assembler.representation.Address;
import simulizer.simulation.cpu.components.DynamicDataSegment;
import simulizer.simulation.exceptions.HeapException;
import category.UnitTests;

/**this class aims to test all methods within the dynamic data segment
 * of the cpu
 * @author Charlie Street
 *
 */
@Category({UnitTests.class})
public class DynamicDataSegmentTest {

	 @Rule
	public final ExpectedException exception = ExpectedException.none();//used for testing for exceptions being thrown

	
	private DynamicDataSegment heap;
	
	/**initialising the heap
	 * using spoofed start address
	 */
	@Before
	public void setUpHeap()
	{
		Address startAddress = new Address(10);//spoof start address for testing purposes
		this.heap = new DynamicDataSegment(startAddress);
	}
	
	/**this method aims to test the sbrk method
	 * in the dynamic data segment class
	 * @throws HeapException if problem with heap
	 */
	@Test
	public void sbrkTests() throws HeapException
	{
		{//valid positive input
			setUpHeap();
			assertEquals(0,heap.size());
			assertEquals(10,heap.sbrk(8).getValue());
			assertEquals(18,heap.sbrk(4).getValue());
			assertEquals(12,heap.size());
		}
		
		{//heap overflow
			setUpHeap();
			assertEquals(0,heap.size());
			assertEquals(10,heap.sbrk(1048576).getValue());
			assertEquals(1048576,heap.size());
			exception.expect(HeapException.class);//expecting an exception when overflowing
			heap.sbrk(4);
		}
		
		{//invalid number of bytes
			setUpHeap();
			assertEquals(0,heap.size());
			exception.expect(HeapException.class);//expecting exception when sbrk run with non multiple of 4
			heap.sbrk(3);
			assertEquals(10,heap.sbrk(4).getValue());//now valid input
			assertEquals(4,heap.size());
		}
		
		{//negative valid input
			setUpHeap();
			assertEquals(0,heap.size());
			
			heap.sbrk(8);//moving it forward 8
			assertEquals(14,heap.sbrk(-4).getValue());
			assertEquals(8,heap.size());
		}
		
		{//negative invalid input
			setUpHeap();
			assertEquals(0,heap.size());
			exception.expect(HeapException.class);
			heap.sbrk(-4);
		}
	}
	
	/**method will test the get bytes method of the dynamic data segment
	 * @throws HeapException 
	 * 
	 */
	@Test
	public void getBytesTest() throws HeapException
	{
		{//testing valid reads
			setUpHeap();
			heap.sbrk(8);
			heap.setByte((byte)0x11, 5);
			assertEquals(0x11,heap.getBytes(5, 1)[0]);	
		}
		
		{//valid reads in bigger block sizes
			setUpHeap();
			heap.sbrk(8);
			heap.setBytes(new byte[]{0x11,0x10,0x78,0x65}, 2);
			byte[] result = heap.getBytes(2, 4);
			assertEquals(0x11,result[0]);
			assertEquals(0x10,result[1]);
			assertEquals(0x78,result[2]);
			assertEquals(0x65,result[3]);
		}
		
		{//invalid get
			setUpHeap();
			exception.expect(HeapException.class);
			heap.getBytes(0, 5);
		}
	}
	
	/**method will test the setBytes method of DynamicDataSegment (if setBytes works so will setByte)
	 * 
	 * @throws HeapException if problem with the heap during use
	 */
	@Test
	public void setBytesTest() throws HeapException
	{
		{//valid setting of bytes (using corect getBytes code to test)
			setUpHeap();
			heap.sbrk(8);
			heap.setBytes(new byte[]{0x11,0x10,0x78,0x65}, 2);
			byte[] result = heap.getBytes(2, 4);
			assertEquals(0x11,result[0]);
			assertEquals(0x10,result[1]);
			assertEquals(0x78,result[2]);
			assertEquals(0x65,result[3]);
		}
		
		{//if empty byte array written shouldn't change anythin
			setUpHeap();
			heap.sbrk(8);
			heap.setByte((byte)0x11, 2);
			heap.setBytes(new byte[]{},2);
			assertEquals(0x11,heap.getBytes(2, 1)[0]);//shouldn't have been changed
		}
		
		{//invalid i.e will eventually run out of heap space
			setUpHeap();
			heap.sbrk(8);
			exception.expect(HeapException.class);
			heap.setBytes(new byte[]{0x00,0x00,0x00,0x00,0x00},4);
		}
	}
}
