package simulizer.simulation.components;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import simulizer.assembler.Assembler;
import simulizer.assembler.representation.Program;
import simulizer.assembler.representation.Register;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.simulation.data.representation.Word;
import simulizer.simulation.exceptions.DecodeException;
import simulizer.simulation.exceptions.ExecuteException;
import simulizer.simulation.exceptions.HeapException;
import simulizer.simulation.exceptions.InstructionException;
import simulizer.simulation.exceptions.MemoryException;
import simulizer.simulation.exceptions.StackException;

/**class will aim to test the execute functionality of the cpu
 * since it is so dependent on large amounts of the cpu
 * it will be tested as follows: small programs will be made to isolate
 * each intruction which can be executed, since decode is successfully tested
 * and the fetch is trivial, then this will test only the execute
 * it will then verify the results by looking at points of interest in the cpu
 * and checking registers/memory have been set correctly in accordance with the operation executed
 * these tests assume that decode, the ALU and the assembler work as intended; they have been tested and so 
 * we have confidence that they do indeed work
 * @author Charlie Street
 *
 */
public class ExecuteTest {

	/**this method will create a test program for the execute
	 * tests; these programs will be let to run but are
	 * written as single/a small number of instructions in order to isolate
	 * the testing on the execute method
	 * @param myInstructions the string for the specific instructions required
	 * @return the file as an assembled program object
	 */
	private Program createProgram(String myInstructions)
	{
		String program = ".data\n" + //forming string of program
						 "mystr: .asciiz \"This is my testString\"\n"+
						 ".align 2\n" + 
						 ".text\n" + 
						 ".globl main\n" +
						 "main:\n" + 
						 myInstructions;
		
		Assembler assembler = new Assembler();
		return assembler.assemble(program, null);//assembling program
	}
	
	/**method will access a register and get it's signed long value
	 * 
	 * @param cpu the cpu object to access
	 * @param register the specific gp register to be read from
	 * @return the long value of said register
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private long accessRegisterSigned(CPU cpu, Register register) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		Field reg = cpu.getClass().getDeclaredField("registers");//accesing private stuff for testing
		reg.setAccessible(true);
		Word[] registers = (Word[])reg.get(cpu);
		
		return DataConverter.decodeAsSigned(registers[register.getID()].getWord());
	}
	
	/**method will access a register and get it's unsigned long value
	 * 
	 * @param cpu the cpu object to access
	 * @param register the specific gp register to be read from
	 * @return the long value of said register
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private long accessRegisterUnsigned(CPU cpu, Register register) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		Field reg = cpu.getClass().getDeclaredField("registers");//accesing private stuff for testing
		reg.setAccessible(true);
		Word[] registers = (Word[])reg.get(cpu);
		
		return DataConverter.decodeAsUnsigned(registers[register.getID()].getWord());
	}
	
	/**method creates a cpu and then runs a program on it
	 * 
	 * @param myInstructions instructions to run
	 * @return the cpu object after execution
	 * @throws MemoryException
	 * @throws DecodeException
	 * @throws InstructionException
	 * @throws ExecuteException
	 * @throws HeapException
	 * @throws StackException
	 */
	private CPU createCPU(String myInstructions) throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException
	{
		CPU cpu = new CPU(null,new IOTest());
		cpu.loadProgram(this.createProgram(myInstructions));//loading program
		cpu.runProgram();//execute the program
		return cpu;
	}
	
	/**test the execution of the add instruction
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
	 * 
	 */
	@Test
	public void testAddExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $t0, 5;\n"+//my instructions to run
								"li $t1, 7;\n"+
								"add $t2, $t1, $t0;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		//now to access the register to check
		assertEquals(5,accessRegisterSigned(cpu, Register.t0));
		assertEquals(7,accessRegisterSigned(cpu, Register.t1));
		assertEquals(12,accessRegisterSigned(cpu, Register.t2));
	}
	/**method will test the execution of the abs instruction
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * @throws DecodeException 
	 * @throws MemoryException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * 
	 */
	@Test
	public void testAbsExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $t0, -5;\n" + 
								"abs $t2, $t0;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(-5,accessRegisterSigned(cpu,Register.t0));//checking for correct register contents
		assertEquals(5,accessRegisterSigned(cpu,Register.t2));
	}
	
	/**method tests the execution of the and instruction
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * @throws DecodeException 
	 * @throws MemoryException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * 
	 */
	@Test
	public void testAndExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $t0, 12;\n" + 
								"li $t1, 15;\n" +
								"and $t2, $t1 ,$t0;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(12,accessRegisterSigned(cpu,Register.t0));
		assertEquals(15,accessRegisterSigned(cpu,Register.t1));
		assertEquals(12,accessRegisterSigned(cpu,Register.t2));
	}
	
	/**this method will test the addu instruction execution
	 * 
	 * @throws MemoryException
	 * @throws DecodeException
	 * @throws InstructionException
	 * @throws ExecuteException
	 * @throws HeapException
	 * @throws StackException
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	@Test
	public void testAdduExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $t0, 47;\n" +
					 			"li $t1, 689;\n" +
					 			"addu $s0, $t0, $t1;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(47,accessRegisterUnsigned(cpu,Register.t0));
		assertEquals(689,accessRegisterUnsigned(cpu,Register.t1));
		assertEquals(736,accessRegisterUnsigned(cpu,Register.s0));
	}
	
	/**method will test the addi instruction execution
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * @throws DecodeException 
	 * @throws MemoryException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * 
	 */
	@Test
	public void testAddiExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $a0, 1234;\n" +
								"addi $s1, $a0, 6;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(1234,accessRegisterSigned(cpu,Register.a0));
		assertEquals(1240,accessRegisterSigned(cpu,Register.s1));
	}
	
	/**method will test the addiu instruction execution
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * @throws DecodeException 
	 * @throws MemoryException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * 
	 */
	@Test
	public void testAddiuExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $a0, 12345;\n" + 
								"addiu $s3, $a0, 5;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(12345,accessRegisterUnsigned(cpu,Register.a0));
		assertEquals(12350,accessRegisterUnsigned(cpu,Register.s3));
	}
	
	/**method will test the sub instruction execution
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * @throws DecodeException 
	 * @throws MemoryException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * 
	 */
	@Test
	public void testSubExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $t3, 145;\n" +
							    "li $t4, -55;\n" +
							    "sub $t5, $t3, $t4;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(145,accessRegisterSigned(cpu,Register.t3));
		assertEquals(-55,accessRegisterSigned(cpu,Register.t4));
		assertEquals(200,accessRegisterSigned(cpu,Register.t5));
	}
	
	/**method will test the subu instruction execution
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * @throws DecodeException 
	 * @throws MemoryException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * 
	 */
	@Test
	public void testSubuExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $t3, 123456789;\n" + 
								"li $t4, 123456789;\n" +
								"subu $t5, $t3, $t3;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(123456789,accessRegisterUnsigned(cpu,Register.t3));
		assertEquals(123456789,accessRegisterUnsigned(cpu,Register.t4));
		assertEquals(0,accessRegisterUnsigned(cpu,Register.t5));
	}
	
	/**method will test the subi instruction execute
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * @throws DecodeException 
	 * @throws MemoryException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * 
	 */
	@Test
	public void testSubiExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $t8, 12345;\n" + 
								"subi $t9, $t8, 12344;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(12345,accessRegisterSigned(cpu,Register.t8));
		assertEquals(1,accessRegisterSigned(cpu,Register.t9));
	}
	
	/**method will test the subiu instruction execution
	 * 
	 * @throws MemoryException
	 * @throws DecodeException
	 * @throws InstructionException
	 * @throws ExecuteException
	 * @throws HeapException
	 * @throws StackException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testSubiuExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $t8, 123456789;\n" +
								"subiu $t9, $t8, 123456788;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(123456789,accessRegisterUnsigned(cpu,Register.t8));
		assertEquals(1,accessRegisterUnsigned(cpu,Register.t9));
	}
	
	/**method will test the mul instruction execution
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * @throws DecodeException 
	 * @throws MemoryException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * 
	 */
	@Test
	public void testMulExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $s2, 45;\n" +
								"li $s1, 55;\n" +
								"mul $s0, $s1, $s2;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(45,accessRegisterSigned(cpu,Register.s2));
		assertEquals(55,accessRegisterSigned(cpu,Register.s1));
		assertEquals(2475,accessRegisterSigned(cpu,Register.s0));
		
	}
	
	/**method will test the mulo instruction execution
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * @throws DecodeException 
	 * @throws MemoryException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * 
	 */
	@Test
	public void testMuloExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $s2, 1234;\n" +
								"li $s3, 1235;\n" +
								"mulo $s1, $s2, $s3;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(1234,accessRegisterSigned(cpu,Register.s2));
		assertEquals(1235,accessRegisterSigned(cpu,Register.s3));
		assertEquals(1523990,accessRegisterSigned(cpu,Register.s1));
	}
	
	/**method will test the mulou instruction execution
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * @throws DecodeException 
	 * @throws MemoryException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * 
	 */
	@Test
	public void testMulouExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException 
	{
		String myInstructions = "li $s2, 12345;\n" +
								"li $s3, 12346;\n" +
								"mulou $s1, $s2, $s3;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(12345,accessRegisterUnsigned(cpu,Register.s2));
		assertEquals(12346,accessRegisterUnsigned(cpu,Register.s3));
		assertEquals(152411370,accessRegisterUnsigned(cpu,Register.s1));
	}
	
	/**method will test the div instruction execution
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * @throws DecodeException 
	 * @throws MemoryException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * 
	 */
	@Test
	public void testDivExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $t5, 45;\n"+
								"li $t6, 9;\n" +
								"div $t7, $t5, $t6;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(45,accessRegisterSigned(cpu,Register.t5));
		assertEquals(9,accessRegisterSigned(cpu,Register.t6));
		assertEquals(5,accessRegisterSigned(cpu,Register.t7));
	}
	
	/**method will test the divu instruction execution
	 * 
	 * @throws MemoryException
	 * @throws DecodeException
	 * @throws InstructionException
	 * @throws ExecuteException
	 * @throws HeapException
	 * @throws StackException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testDivuExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $t5, 123456789;\n" +
								"li $t6, 123456788;\n" +
								"divu $t7, $t5, $t6;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(123456789,accessRegisterUnsigned(cpu,Register.t5));
		assertEquals(123456788,accessRegisterUnsigned(cpu,Register.t6));
		assertEquals(1,accessRegisterUnsigned(cpu,Register.t7));
	}
	
	/**method will test the neg instruction execution
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * @throws DecodeException 
	 * @throws MemoryException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * 
	 */
	@Test
	public void testNegExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $t5, -10;\n" +
							   "neg $t6, $t5;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(-10,accessRegisterSigned(cpu,Register.t5));
		assertEquals(10,accessRegisterSigned(cpu,Register.t6));	
	}
	
	/**method will test the negu instruction execution
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * @throws DecodeException 
	 * @throws MemoryException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * 
	 */
	@Test
	public void testNeguExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $s0, -23455;\n" +
								"negu $s1, $s0;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(-23455,accessRegisterSigned(cpu,Register.s0));
		assertEquals(23455,accessRegisterUnsigned(cpu,Register.s1));
	}
	
	/**method will test the nor instruction execution
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * @throws DecodeException 
	 * @throws MemoryException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * 
	 */
	@Test
	public void testNorExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $s0, 56;\n" +
								"li $s1, 71;\n" +
								"nor $s2, $s0, $s1;\n";
		
		CPU cpu = createCPU(myInstructions);
		assertEquals(56,accessRegisterSigned(cpu,Register.s0));
		assertEquals(71,accessRegisterSigned(cpu,Register.s1));
		assertEquals(-128,accessRegisterSigned(cpu,Register.s2));
	}
	
	/**method will test the not instruction execution
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * @throws DecodeException 
	 * @throws MemoryException 
	 * 
	 */
	@Test
	public void testNotExecute() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException
	{
		String myInstructions = "li $s1, 0;\n" +
							    "not $s2, $s1;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(0,accessRegisterSigned(cpu,Register.s1));
		assertEquals(-1,accessRegisterSigned(cpu,Register.s2));
	}
	
	/**method will test the or instruction execution
	 * 
	 * @throws MemoryException
	 * @throws DecodeException
	 * @throws InstructionException
	 * @throws ExecuteException
	 * @throws HeapException
	 * @throws StackException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testOrExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $s0, 56;\n" +
								"li $s1, 71;\n" +
								"or $s2, $s0, $s1;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(56,accessRegisterSigned(cpu,Register.s0));
		assertEquals(71,accessRegisterSigned(cpu,Register.s1));
		assertEquals(127,accessRegisterSigned(cpu,Register.s2));
	}
	
	/**method will test the ori instruction execution
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * @throws DecodeException 
	 * @throws MemoryException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * 
	 */
	@Test
	public void testOriExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $a0, 56;\n" +
								"ori $t7, $a0, 71;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(56,accessRegisterSigned(cpu,Register.a0));
		assertEquals(127,accessRegisterSigned(cpu,Register.t7));
	}
	
	/**method will test the xor instruction execution
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * @throws DecodeException 
	 * @throws MemoryException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * 
	 */
	@Test
	public void testXorExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $a0, 77;\n" +
								"li $a1, 67;\n" +
								"xor $v0, $a0, $a1;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(77,accessRegisterSigned(cpu,Register.a0));
		assertEquals(67,accessRegisterSigned(cpu,Register.a1));
		assertEquals(14,accessRegisterSigned(cpu,Register.v0));
	}
	
	/**method will test the xori insruction execution
	 * 
	 * @throws MemoryException
	 * @throws DecodeException
	 * @throws InstructionException
	 * @throws ExecuteException
	 * @throws HeapException
	 * @throws StackException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testXoriExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $a0, 77;\n" +
								"xori $v0, $a0, 67;\n";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(77,accessRegisterSigned(cpu,Register.a0));
		assertEquals(14,accessRegisterSigned(cpu,Register.v0));
	}
	
	/**method tests the li instruction's execution
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * @throws DecodeException 
	 * @throws MemoryException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * 
	 */
	@Test
	public void testLiExecute() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		String myInstructions = "li $v0, 5";
		
		CPU cpu = createCPU(myInstructions);
		
		assertEquals(5,accessRegisterSigned(cpu,Register.v0));
	}
}
