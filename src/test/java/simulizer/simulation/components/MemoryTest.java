package simulizer.simulation.components;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import simulizer.assembler.Assembler;
import simulizer.assembler.representation.Address;
import simulizer.assembler.representation.Instruction;
import simulizer.assembler.representation.Program;
import simulizer.assembler.representation.Statement;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.cpu.components.MainMemory;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.simulation.exceptions.DecodeException;
import simulizer.simulation.exceptions.ExecuteException;
import simulizer.simulation.exceptions.HeapException;
import simulizer.simulation.exceptions.InstructionException;
import simulizer.simulation.exceptions.MemoryException;
import simulizer.simulation.exceptions.StackException;
import category.UnitTests;

/**this class aims to test the memory component of the cpu
 * most of the tests will be in the form of read(write x) = x
 * @author Charlie Street
 *
 */
@Category({UnitTests.class})
public class MemoryTest {

	/**this method will be used to create a test program with which the memory can be tested
	 * test program needed in order to actually populate the memory
	 * @param myInstructions the instructions I will add into the program for testing
	 * @return the assembled program object
	 */
	private Program createProgram(String myInstructions)
	{
		String program = ".data\n" + //forming string of program
						 "mystr: .asciiz \"This is my test String\"\n"+
						 "mynum: .word -10\n" +
						 "mynewnum: .byte 10\n" +
						 ".align 2\n" + 
						 ".text\n" + 
						 ".globl main\n" +
						 "main:\n" + 
						 myInstructions;
		
		Assembler assembler = new Assembler();
		return assembler.assemble(program, null);//assembling program
	}
	
	/**method will test the read and write functionality of the memory
	 * everything except the test segment will be tested
	 * due to the nature of the memory I will test at the boundaries, if they work, everything in between will work
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * @throws DecodeException 
	 * @throws MemoryException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	@Test
	public void testReadWriteMem() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $v0, 9;\n" +
								"li $a0, 4;\n" +
								"syscall;\n";//allocating some heap mem
		
		Program program = createProgram(myInstructions);
		
		CPU cpu = new CPU(null,new IOTest());
		cpu.loadProgram(program);
		cpu.runProgram();//run the program
		
		//now to carry out tests
		Address dataSegStart = program.dataSegmentStart;
		Address dynamicSegStart = program.dynamicSegmentStart;
		Address stackPointer = new Address((int)DataConverter.decodeAsSigned(program.initialSP.getWord()));
		
		Field mem = cpu.getClass().getDeclaredField("memory");
		mem.setAccessible(true);
		MainMemory memory = (MainMemory)mem.get(cpu);//getting memory
		
		{//test 1: read write from boundary of data segment
			memory.writeToMem(dataSegStart.getValue(), new byte[]{0x00,0x11,0x12,0x33});
			byte[] result = memory.readFromMem(dataSegStart.getValue(), 4);
			assertEquals(4,result.length);
			assertEquals(0x00,result[0]);
			assertEquals(0x11,result[1]);
			assertEquals(0x12,result[2]);
			assertEquals(0x33,result[3]);
		}
		
		{//test 2: read write from boundary of heap
			memory.writeToMem(dynamicSegStart.getValue(), new byte[]{0x00,0x11,0x12,0x33});
			byte[] result = memory.readFromMem(dynamicSegStart.getValue(), 4);
			assertEquals(4,result.length);
			assertEquals(0x00,result[0]);
			assertEquals(0x11,result[1]);
			assertEquals(0x12,result[2]);
			assertEquals(0x33,result[3]);
		}
		
		{//test 3:read write from stack
			memory.writeToMem(stackPointer.getValue(), new byte[]{0x00,0x11,0x12,0x33});
			byte[] result = memory.readFromMem(stackPointer.getValue(), 4);
			assertEquals(4,result.length);
			assertEquals(0x00,result[0]);
			assertEquals(0x11,result[1]);
			assertEquals(0x12,result[2]);
			assertEquals(0x33,result[3]);
		}
		
		{//test address below dataSegement
			try {
				memory.writeToMem(dataSegStart.getValue()-1, new byte[]{0x00,0x11,0x12,0x33});
				fail();
			} catch(MemoryException e) {
				assertTrue(e.getMessage().equals("Writing to an invalid area of memory"));
			}
		}
		
		{//test reading below data segment without using read from text segment
			try {
				memory.readFromMem(dataSegStart.getValue()-1, 4);
				fail();
			} catch(MemoryException e) {
				assertTrue(e.getMessage().equals("Reading from invalid area of memory"));
			}
		}
		
		{//test reading at end of data segment going into heap (invalid read)
			try {
				memory.readFromMem(dynamicSegStart.getValue()-3,4);
				fail();
			} catch(MemoryException e) {
				assertTrue(e.getMessage().equals("Reading from invalid area of memory"));
			}
		}
		
		{//test writing at end of data segment going into heap (invalid write)
			try {
				memory.writeToMem(dynamicSegStart.getValue()-3,new byte[]{0x11,0x12,0x13,0x14});
				fail();
			} catch(MemoryException e) {
				assertTrue(e.getMessage().equals("Writing to an invalid area of memory"));
			}
			
		}
		
		{//test reading too far into heap when not allocated (invalid)
			try {
				memory.readFromMem(dynamicSegStart.getValue()+7, 4);
				fail();
			} catch(HeapException e) {
				assertTrue(e.getMessage().equals("Invalid read on heap. Out of Bounds."));
			}
		}
		
		{//test writing too far into heap when not allocated (invalid)
			try {
				memory.writeToMem(dynamicSegStart.getValue()+7, new byte[]{0x11,0x12,0x13,0x14});
				fail();
			} catch(MemoryException e) {
				assertTrue(e.getMessage().equals("Writing to an invalid area of memory"));
			}
		}
		
		{//test reading too far into stack when not reached yet (invalid)
			try {
				memory.readFromMem(stackPointer.getValue()-4,4);
				fail();
			} catch(StackException e) {
				assertTrue(e.getMessage().equals("Invalid read on stack."));
			}
		}
		
		{//test reading too far into stack when not reached yet (invalid)
			try {
				memory.writeToMem(stackPointer.getValue()-8, new byte[]{0x11,0x12,0x13,0x14});
				fail();
			} catch(StackException e) {
				assertTrue(e.getMessage().equals("Invalid write onto stack."));
			}
		}
			
	}
	
	
	/**this method will test the reading of the text segment
	 * it will check if it can find valid instructions in the segment
	 * invalid instructions within the segment
	 * and out of bounds checks
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * @throws DecodeException 
	 * @throws MemoryException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	@Test
	public void testReadTextSegment() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $v0, 9;\n" +
								"li $a0, 4;\n" +
								"syscall;\n";//allocating some heap mem

		Program program = createProgram(myInstructions);
		
		CPU cpu = new CPU(null,new IOTest());
		cpu.loadProgram(program);
		cpu.runProgram();//run the program
		
		//now to carry out tests
		Address dataSegStart = program.dataSegmentStart;
		Address textSegmentStart = program.textSegmentStart;
		
		Field mem = cpu.getClass().getDeclaredField("memory");
		mem.setAccessible(true);
		MainMemory memory = (MainMemory)mem.get(cpu);//getting memory
		
		{//test valid instructon read at start of text segment
			Statement statement = memory.readFromTextSegment(textSegmentStart);
			assertTrue(statement.getInstruction().equals(Instruction.li));
			assertEquals(9,statement.getOperandList().get(1).asIntegerOp().value);
		}
		
		{//test valid instructon read in middle of text segment
			Statement statement = memory.readFromTextSegment(new Address(textSegmentStart.getValue()+4));
			assertTrue(statement.getInstruction().equals(Instruction.li));
			assertEquals(4,statement.getOperandList().get(1).asIntegerOp().value);
		}
		
		{//test invalid address within text segment
			try {
				memory.readFromTextSegment(new Address(textSegmentStart.getValue()+3));
				fail();
			} catch(MemoryException e) {
				assertTrue(e.getMessage().equals("Reading from invalid area of memory"));
			}
		}
		
		{//test out of bounds of text segment
			try {
				memory.readFromTextSegment(dataSegStart);
				fail();
			} catch(MemoryException e) {
				assertTrue(e.getMessage().equals("Reading from invalid area of memory"));
			}
		}
	}
}
