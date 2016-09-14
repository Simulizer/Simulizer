package simulizer.simulation.components;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import simulizer.assembler.representation.Address;
import simulizer.assembler.representation.Instruction;
import simulizer.assembler.representation.Register;
import simulizer.assembler.representation.operand.AddressOperand;
import simulizer.assembler.representation.operand.IntegerOperand;
import simulizer.assembler.representation.operand.Operand;
import simulizer.assembler.representation.operand.RegisterOperand;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.cpu.user_interaction.ConsoleIO;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.simulation.exceptions.DecodeException;
import simulizer.simulation.instructions.AddressMode;
import simulizer.simulation.instructions.ITypeInstruction;
import simulizer.simulation.instructions.InstructionFormat;
import simulizer.simulation.instructions.JTypeInstruction;
import simulizer.simulation.instructions.LSInstruction;
import simulizer.simulation.instructions.RTypeInstruction;
import simulizer.simulation.instructions.SpecialInstruction;
import category.UnitTests;

/**this class will test the decode functionality of the CPU
 * where specific instructions are specified; they are one of the 
 * instructions in a given format, different instructions of the same format will 
 * decode to the same thing and so that is not being tested here
 * @author Charlie Street
 *
 */
@SuppressWarnings("OptionalGetWithoutIsPresent")
@Category({UnitTests.class})
public class DecodeTest {

	private CPU cpu;

	@Before
	public void setupCPU() {
		cpu = new CPU(new ConsoleIO());
		cpu.setCycleFreq(0); // infinitely fast
	}

	@After
	public void shutdownCPU() {
		cpu.shutdown();
		cpu = null;
	}

	/**tests the decoding of operand format dest src src
	 * the arbitrary instruction used to test is add
	 */
	@Test
	public void testDestSrcSrc() throws DecodeException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Instruction instruction = Instruction.add;
		RegisterOperand op1 = new RegisterOperand(Register.t0);
		RegisterOperand op2 = new RegisterOperand(Register.t1);
		RegisterOperand op3 = new RegisterOperand(Register.t2);
		List<Operand> list = new ArrayList<>();
		list.add(op1);
		list.add(op2);
		list.add(op3);
		
		Method decoder = cpu.getClass().getDeclaredMethod("decode", Instruction.class, List.class);
		decoder.setAccessible(true);
		InstructionFormat instr = (InstructionFormat)decoder.invoke(cpu,instruction,list);
		RTypeInstruction rtype= instr.asRType();
		assertTrue(rtype.mode.equals(AddressMode.RTYPE));
		assertTrue(rtype.getInstruction().equals(Instruction.add));
		assertEquals(0x00,rtype.getSrc1().get().getBytes()[0]);
		assertEquals(0x00,rtype.getSrc1().get().getBytes()[1]);
		assertEquals(0x00,rtype.getSrc1().get().getBytes()[2]);
		assertEquals(0x00,rtype.getSrc1().get().getBytes()[3]);
		assertEquals(0x00,rtype.getSrc2().get().getBytes()[0]);
		assertEquals(0x00,rtype.getSrc2().get().getBytes()[1]);
		assertEquals(0x00,rtype.getSrc2().get().getBytes()[2]);
		assertEquals(0x00,rtype.getSrc2().get().getBytes()[3]);
		assertTrue(!rtype.getDest().isPresent());
		assertTrue(rtype.getDestReg().equals(Register.t0));
	}
	
	/**method will test the dest src imm operand format in the decode
	 * instruction used: addi
	 */
	@Test
	public void testDestSrcImm() throws DecodeException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Instruction instruction = Instruction.addi;
		RegisterOperand op1 = new RegisterOperand(Register.t0);
		RegisterOperand op2 = new RegisterOperand(Register.t1);
		IntegerOperand op3 = new IntegerOperand(5);
		List<Operand> list = new ArrayList<>();
		list.add(op1);
		list.add(op2);
		list.add(op3);
		
		Method decoder = cpu.getClass().getDeclaredMethod("decode", Instruction.class, List.class);//accesing private method
		decoder.setAccessible(true);
		InstructionFormat instr = (InstructionFormat)decoder.invoke(cpu,instruction,list);
		RTypeInstruction rtype= instr.asRType();
		assertTrue(rtype.mode.equals(AddressMode.RTYPE));
		assertTrue(rtype.getInstruction().equals(Instruction.addi));
		assertEquals(0x00,rtype.getSrc1().get().getBytes()[0]);
		assertEquals(0x00,rtype.getSrc1().get().getBytes()[1]);
		assertEquals(0x00,rtype.getSrc1().get().getBytes()[2]);
		assertEquals(0x00,rtype.getSrc1().get().getBytes()[3]);
		assertEquals(0x00,rtype.getSrc2().get().getBytes()[0]);
		assertEquals(0x00,rtype.getSrc2().get().getBytes()[1]);
		assertEquals(0x00,rtype.getSrc2().get().getBytes()[2]);
		assertEquals(5,rtype.getSrc2().get().getBytes()[3]);
		assertTrue(!rtype.getDest().isPresent());
		assertTrue(rtype.getDestReg().equals(Register.t0));
	}
	
	/**tests the dest src imm u operand format
	 * instruction used: addiu
	 */
    @Test
	public void testDestSrcImmU() throws DecodeException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Instruction instruction = Instruction.addiu;
		RegisterOperand op1 = new RegisterOperand(Register.t0);
		RegisterOperand op2 = new RegisterOperand(Register.t1);
		IntegerOperand op3 = new IntegerOperand(5);
		List<Operand> list = new ArrayList<>();
		list.add(op1);
		list.add(op2);
		list.add(op3);
		
		Method decoder = cpu.getClass().getDeclaredMethod("decode", Instruction.class, List.class);
		decoder.setAccessible(true);
		InstructionFormat instr = (InstructionFormat)decoder.invoke(cpu,instruction,list);
		RTypeInstruction rtype= instr.asRType();
		assertTrue(rtype.mode.equals(AddressMode.RTYPE));
		assertTrue(rtype.getInstruction().equals(Instruction.addiu));
		assertEquals(0x00,rtype.getSrc1().get().getBytes()[0]);
		assertEquals(0x00,rtype.getSrc1().get().getBytes()[1]);
		assertEquals(0x00,rtype.getSrc1().get().getBytes()[2]);
		assertEquals(0x00,rtype.getSrc1().get().getBytes()[3]);
		assertEquals(0x00,rtype.getSrc2().get().getBytes()[0]);
		assertEquals(0x00,rtype.getSrc2().get().getBytes()[1]);
		assertEquals(0x00,rtype.getSrc2().get().getBytes()[2]);
		assertEquals(5,rtype.getSrc2().get().getBytes()[3]);
		assertTrue(!rtype.getDest().isPresent());
		assertTrue(rtype.getDestReg().equals(Register.t0));
	}
	
	/**test the dest src operand format
	 * instruction used is abs
	 */
	@Test
	public void testDestSrc() throws DecodeException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Instruction instruction = Instruction.abs;
		RegisterOperand op1 = new RegisterOperand(Register.t0);
		RegisterOperand op2 = new RegisterOperand(Register.t0);
		List<Operand> opList = new ArrayList<>();
		opList.add(op1);
		opList.add(op2);
		
		Method decoder = cpu.getClass().getDeclaredMethod("decode", Instruction.class, List.class);
		decoder.setAccessible(true);
		InstructionFormat instr = (InstructionFormat)decoder.invoke(cpu,instruction,opList);
		RTypeInstruction rtype = instr.asRType();
		assertTrue(rtype.mode.equals(AddressMode.RTYPE));
		assertTrue(rtype.getInstruction().equals(Instruction.abs));
		assertEquals(0x00,rtype.getSrc1().get().getBytes()[0]);
		assertEquals(0x00,rtype.getSrc1().get().getBytes()[1]);
		assertEquals(0x00,rtype.getSrc1().get().getBytes()[2]);
		assertEquals(0x00,rtype.getSrc1().get().getBytes()[3]);
		assertTrue(!rtype.getSrc2().isPresent());
		assertTrue(!rtype.getDest().isPresent());
		assertTrue(rtype.getDestReg().equals(Register.t0));
	}
	
	/**test the dest imm operand format
	 * instruction used: li
	 */
	@Test
	public void testImm() throws DecodeException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		Instruction instruction = Instruction.li;
		RegisterOperand op1 = new RegisterOperand(Register.t0);
		IntegerOperand op2 = new IntegerOperand(7);
		List<Operand> opList = new ArrayList<>();
		opList.add(op1);
		opList.add(op2);
		
		Method decoder = cpu.getClass().getDeclaredMethod("decode", Instruction.class, List.class);
		decoder.setAccessible(true);
		InstructionFormat instr = (InstructionFormat)decoder.invoke(cpu,instruction,opList);
		LSInstruction lstype = instr.asLSType();
		assertTrue(lstype.mode.equals(AddressMode.LSTYPE));
		assertTrue(lstype.getInstruction().equals(Instruction.li));
		assertEquals(0x00,lstype.getImmediate().get().getBytes()[0]);
		assertEquals(0x00,lstype.getImmediate().get().getBytes()[1]);
		assertEquals(0x00,lstype.getImmediate().get().getBytes()[2]);
		assertEquals(7,lstype.getImmediate().get().getBytes()[3]);
		assertTrue(!lstype.getMemAddress().isPresent());
		assertTrue(!lstype.getRegister().isPresent());
		assertTrue(lstype.getRegisterName().get().equals(Register.t0));
	}
	
	/**will test the no arguments operand format
	 * instruction tested: syscall
	 */
	@Test
	public void testNoArguments() throws DecodeException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Instruction instruction = Instruction.syscall;
		
		Method decoder = cpu.getClass().getDeclaredMethod("decode", Instruction.class, List.class);
		decoder.setAccessible(true);
		InstructionFormat instr = (InstructionFormat)decoder.invoke(cpu,instruction,new ArrayList<Operand>());
		SpecialInstruction special = instr.asSpecial();
		assertTrue(special.mode.equals(AddressMode.SPECIAL));
		assertTrue(special.getInstruction().equals(Instruction.syscall));
	}

	/**will test the label operand format
	 * instruction tested: j
	 */
	@Test
	public void testLabel() throws DecodeException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException
	{
		Instruction instruction = Instruction.j;
		
		Field labels = cpu.getClass().getDeclaredField("labels");//accesing private stuff for testing
		labels.setAccessible(true);
		Map<String,Address> map = new HashMap<>();
		map.put("testName", new Address(10));
		labels.set(cpu, map);
		
		Field pc = cpu.getClass().getDeclaredField("programCounter");//accessing private PC
		pc.setAccessible(true);
		pc.set(cpu, new Address(15));

		AddressOperand op1 = new AddressOperand(Optional.of("testName"), Optional.empty(), Optional.empty());
		List<Operand> opList = new ArrayList<>();
		opList.add(op1);
		
		Method decoder = cpu.getClass().getDeclaredMethod("decode", Instruction.class, List.class);//accessing private method
		decoder.setAccessible(true);
		InstructionFormat instr = (InstructionFormat)decoder.invoke(cpu,instruction,opList);
		JTypeInstruction jtype = instr.asJType();
		assertTrue(jtype.mode.equals(AddressMode.JTYPE));
		assertTrue(jtype.getInstruction().equals(Instruction.j));
		assertEquals(10,jtype.getJumpAddress().get().getValue());
		assertEquals(15,DataConverter.decodeAsSigned(jtype.getCurrentAddress().get().getBytes()));
	}
	
	/**will test the register operand format
	 * instruction tested: jr
	 */
	@Test
	public void testRegister() throws DecodeException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException {
		Instruction instruction = Instruction.jr;
		
		RegisterOperand op1 = new RegisterOperand(Register.t0);
		List<Operand> opList = new ArrayList<>();
		opList.add(op1);

		Method decoder = cpu.getClass().getDeclaredMethod("decode", Instruction.class, List.class);
		Field programCounter = cpu.getClass().getDeclaredField("programCounter");
		decoder.setAccessible(true);
        programCounter.setAccessible(true);
		programCounter.set(cpu, new Address(100));
		InstructionFormat instr = (InstructionFormat)decoder.invoke(cpu,instruction,opList);
		JTypeInstruction jtype = instr.asJType();
		assertEquals(AddressMode.JTYPE, jtype.mode);
		assertEquals(Instruction.jr, jtype.getInstruction());
		assertTrue(jtype.getCurrentAddress().isPresent());
		assertEquals(0,jtype.getJumpAddress().get().getValue());
		assertTrue(Arrays.equals(DataConverter.encodeAsUnsigned(100), jtype.getCurrentAddress().get().getBytes()));
	}
	
	/**will test the cmp cmp label operand format
	 * instruction used: beq
	 */
	@Test
	public void testCmpCmpLabel() throws DecodeException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException
	{
		Instruction instruction = Instruction.beq;
		
		Field labels = cpu.getClass().getDeclaredField("labels");//accesing private stuff for testing
		labels.setAccessible(true);
		Map<String,Address> map = new HashMap<>();
		map.put("testName", new Address(17));
		labels.set(cpu, map);
		
		RegisterOperand op1 = new RegisterOperand(Register.t0);
		RegisterOperand op2 = new RegisterOperand(Register.t1);
		AddressOperand op3 = new AddressOperand(Optional.of("testName"), Optional.empty(), Optional.empty());
		List<Operand> opList = new ArrayList<>();
		opList.add(op1);
		opList.add(op2);
		opList.add(op3);
		
		Method decoder = cpu.getClass().getDeclaredMethod("decode", Instruction.class, List.class);
		decoder.setAccessible(true);
		InstructionFormat instr = (InstructionFormat)decoder.invoke(cpu,instruction,opList);
		
		ITypeInstruction itype = instr.asIType();
		assertTrue(itype.mode.equals(AddressMode.ITYPE));
		assertTrue(itype.getInstruction().equals(Instruction.beq));
		assertEquals(0x00,itype.getCmp1().get().getBytes()[0]);
		assertEquals(0x00,itype.getCmp1().get().getBytes()[1]);
		assertEquals(0x00,itype.getCmp1().get().getBytes()[2]);
		assertEquals(0x00,itype.getCmp1().get().getBytes()[3]);
		assertEquals(0x00,itype.getCmp2().get().getBytes()[0]);
		assertEquals(0x00,itype.getCmp2().get().getBytes()[1]);
		assertEquals(0x00,itype.getCmp2().get().getBytes()[2]);
		assertEquals(0x00,itype.getCmp2().get().getBytes()[3]);
		assertEquals(17,itype.getBranchAddress().get().getValue());
	}
	
	/**will test the cmp label operand format
	 * instruction used: bltz
	 */
	@Test
	public void testCmpLabel() throws DecodeException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
	{
		Instruction instruction = Instruction.bltz;
		
		Field labels = cpu.getClass().getDeclaredField("labels");//accesing private stuff for testing
		labels.setAccessible(true);
		Map<String,Address> map = new HashMap<>();
		map.put("testName", new Address(25));
		labels.set(cpu, map);
		
		RegisterOperand op1 = new RegisterOperand(Register.s0);
		AddressOperand op2 = new AddressOperand(Optional.of("testName"), Optional.empty(), Optional.empty());
		List<Operand> opList = new ArrayList<>();
		opList.add(op1);
		opList.add(op2);
		
		Method decoder = cpu.getClass().getDeclaredMethod("decode", Instruction.class, List.class);
		decoder.setAccessible(true);
		InstructionFormat instr = (InstructionFormat)decoder.invoke(cpu,instruction,opList);
		
		ITypeInstruction itype = instr.asIType();
		assertTrue(itype.mode.equals(AddressMode.ITYPE));
		assertTrue(itype.getInstruction().equals(Instruction.bltz));
		assertEquals(0x00,itype.getCmp1().get().getBytes()[0]);
		assertEquals(0x00,itype.getCmp1().get().getBytes()[1]);
		assertEquals(0x00,itype.getCmp1().get().getBytes()[2]);
		assertEquals(0x00,itype.getCmp1().get().getBytes()[3]);
		assertFalse(itype.getCmp2().isPresent());
		assertEquals(25,itype.getBranchAddress().get().getValue());
		
	}
	
	/**will test the src addr operand format
	 * instruction used: sw
	 */
	@Test
	public void testSrcAddr() throws DecodeException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Instruction instruction = Instruction.sw;
		
		RegisterOperand op1 = new RegisterOperand(Register.s0);
		AddressOperand op2 = new AddressOperand(Optional.empty(), Optional.of(18), Optional.of(Register.s1));
		List<Operand> opList = new ArrayList<>();
		opList.add(op1);
		opList.add(op2);
		
		Method decoder = cpu.getClass().getDeclaredMethod("decode", Instruction.class, List.class);
		decoder.setAccessible(true);
		InstructionFormat instr = (InstructionFormat)decoder.invoke(cpu,instruction,opList);
		
		LSInstruction lstype = instr.asLSType();
		assertTrue(lstype.mode.equals(AddressMode.LSTYPE));
		assertTrue(lstype.getInstruction().equals(Instruction.sw));
		assertEquals(0,DataConverter.decodeAsSigned(lstype.getRegister().get().getBytes()));
		assertFalse(lstype.getRegisterName().isPresent());
		assertFalse(lstype.getImmediate().isPresent());
		assertEquals(18,lstype.getMemAddress().get().getValue());
		
	}
	
	/**will test the dest addr operand format
	 * instruction used: lw
	 */
	@Test
	public void testDestAddr() throws DecodeException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Instruction instruction = Instruction.lw;
		
		RegisterOperand op1 = new RegisterOperand(Register.s0);
		AddressOperand op2 = new AddressOperand(Optional.empty(), Optional.of(23), Optional.of(Register.s1));
		List<Operand> opList = new ArrayList<>();
		opList.add(op1);
		opList.add(op2);
		
		Method decoder = cpu.getClass().getDeclaredMethod("decode", Instruction.class, List.class);
		decoder.setAccessible(true);
		InstructionFormat instr = (InstructionFormat)decoder.invoke(cpu,instruction,opList);
		
		LSInstruction lstype = instr.asLSType();
		assertTrue(lstype.mode.equals(AddressMode.LSTYPE));
		assertTrue(lstype.getInstruction().equals(Instruction.lw));
		
		assertFalse(lstype.getRegister().isPresent());
		assertTrue(lstype.getRegisterName().get().equals(Register.s0));
		assertFalse(lstype.getImmediate().isPresent());
		assertEquals(23,lstype.getMemAddress().get().getValue());	
	}
	
	/**all decode cases do the same thing and check the operand format
	 * this test will check that on a given operand format, if the wrong no. of operands are entered
	 * an exception will be thrown
	 * all decode cases have a variation of the same check, if one works, by default so will the rest
	 */
	@Test
	public void testBadOperands() throws DecodeException, NoSuchMethodException, SecurityException
	{
		Instruction instruction = Instruction.add;
		
		List<Operand> opList = new ArrayList<>();//should take 3 operands giving it none
		Method decoder = cpu.getClass().getDeclaredMethod("decode", Instruction.class, List.class);
		decoder.setAccessible(true);
		
		try
		{
			decoder.invoke(cpu,instruction,opList);//should throw exception
			fail();
		} catch(Exception e) {
			assertTrue(true);
		}
	}
}
