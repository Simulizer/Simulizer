package simulizer.simulation.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import category.UnitTests;
import simulizer.assembler.representation.Address;
import simulizer.simulation.cpu.components.DynamicDataSegment;
import simulizer.simulation.exceptions.HeapException;

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

	private void setUpHeap() {
	    setUpHeap(20);
	}
	private void setUpHeap(int maxHeapSize) {
		Address startAddress = new Address(10);//spoof start address for testing purposes
		this.heap = new DynamicDataSegment(startAddress, maxHeapSize);
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
			assertEquals(10,heap.sbrk(8).getValue());
			assertEquals(18,heap.sbrk(4).getValue());
		}
		
		{//heap overflow
			setUpHeap(20); // max length
			assertEquals(10,heap.sbrk(20).getValue()); // OK
			exception.expect(HeapException.class);//expecting an exception when overflowing
			heap.sbrk(1);
		}

		{//valid: grow and shrink
			setUpHeap(); // max length
			assertEquals(10,heap.sbrk(5).getValue());
			assertEquals(10,heap.sbrk(-5).getValue());
		}

		{//valid: expand by 0. Used to retrieve break address
			setUpHeap(); // max length
			assertEquals(10,heap.sbrk(0).getValue());
			assertEquals(10,heap.sbrk(0).getValue()); // check no movement
		}

		{//invalid: shrink below the bottom of the heap
			setUpHeap(); // max length
			assertEquals(10,heap.sbrk(5).getValue());
			exception.expect(HeapException.class);//expecting an exception when overflowing
			heap.sbrk(-6);
		}

		{//invalid number of bytes (not multiple of 4)
			setUpHeap();
			exception.expect(HeapException.class);//expecting exception when sbrk run with non multiple of 4
			heap.sbrk(3);
			assertEquals(10,heap.sbrk(4).getValue());//now valid input
		}
		
		{//negative valid input
			setUpHeap();

			heap.sbrk(8);//moving it forward 8
			assertEquals(14,heap.sbrk(-4).getValue());
		}
		
		{//negative invalid input
			setUpHeap();
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
			heap.setBytes(5, new byte[]{0x11});
			assertEquals(0x11,heap.getBytes(5, 1)[0]);	
		}
		
		{//valid reads in bigger block sizes
			setUpHeap();
			heap.sbrk(8);
			heap.setBytes(2, new byte[]{0x11,0x10,0x78,0x65});
			byte[] result = heap.getBytes(2, 4);
			assertEquals(0x11,result[0]);
			assertEquals(0x10,result[1]);
			assertEquals(0x78,result[2]);
			assertEquals(0x65,result[3]);
		}
		
		{//invalid get (empty heap)
			setUpHeap();
			exception.expect(HeapException.class);
			heap.getBytes(0, 5);
		}

		{//invalid get (address below the heap)
			setUpHeap();
			heap.sbrk(8);
			exception.expect(HeapException.class);
			heap.getBytes(-1, 5);
		}

		{//invalid get (address above the heap)
			setUpHeap();
			heap.sbrk(8);
			exception.expect(HeapException.class);
			heap.getBytes(8, 5);
		}

		{//valid read on the boundary
			setUpHeap();
			heap.sbrk(8);
			heap.setBytes(8, new byte[]{0x11});
			assertEquals(0x11,heap.getBytes(8, 1)[0]);
		}
	}


	@Test
	public void testReadUntilNull() throws HeapException
	{
		{//valid read
			setUpHeap();
			heap.sbrk(8);
			heap.setBytes(5, new byte[]{0x11, 0x22, '\0'});
			byte[] read = heap.readUntilNull(5);
		}

		{//invalid read (no null before end)
			setUpHeap();
			heap.sbrk(8);
			heap.setBytes(5, new byte[]{0x11, 0x22, 0x11});
			try {
				byte[] read = heap.readUntilNull(5);
				fail();
			} catch (HeapException e) {
				assertTrue(e.getMessage().equals("Reading from invalid area of memory (scanning for a null character)"));
			}
		}

		{//invalid read (start address above the top of the heap)
			setUpHeap();
			heap.sbrk(8);
			try {
				byte[] read = heap.readUntilNull(8);
				fail();
			} catch (HeapException e) {
				assertTrue(e.getMessage().equals("Reading from invalid area of memory (scanning for a null character)"));
			}
		}

		{//invalid read (start address below the bottom of the heap)
			setUpHeap();
			heap.sbrk(8);
			try {
				byte[] read = heap.readUntilNull(-1);
				fail();
			} catch (HeapException e) {
				assertTrue(e.getMessage().equals("Reading from invalid area of memory (scanning for a null character)"));
			}
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
			heap.setBytes(2, new byte[]{0x11,0x10,0x78,0x65});
			byte[] result = heap.getBytes(2, 4);
			assertEquals(0x11,result[0]);
			assertEquals(0x10,result[1]);
			assertEquals(0x78,result[2]);
			assertEquals(0x65,result[3]);
		}
		
		{//invalid: empty array
			setUpHeap();
			heap.sbrk(8);
			exception.expect(HeapException.class);
			heap.setBytes(2, new byte[]{});
		}
		
		{//invalid: write beyond the break
			setUpHeap();
			heap.sbrk(8);
			exception.expect(HeapException.class);
			heap.setBytes(4, new byte[]{0x00,0x00,0x00,0x00,0x00});
		}

		{//invalid: write below the heap
			setUpHeap();
			exception.expect(HeapException.class);
			heap.setBytes(-1, new byte[]{0x00});
		}
	}
}

