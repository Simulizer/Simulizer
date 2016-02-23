package simulizer.simulation.components;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import simulizer.simulation.cpu.user_interaction.IOConsole;
import simulizer.simulation.exceptions.DecodeException;
import simulizer.simulation.instructions.AddressMode;
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
@Category({UnitTests.class})
public class DecodeTest {

	/**tests the decoding of operand format dest src src
	 * the arbitrary instruction used to test is add
	 * @throws DecodeException 
	 */
	@Test
	public void testDestSrcSrc() throws DecodeException
	{
		
		CPU cpu = new CPU(null,new IOConsole());
		Instruction instruction = Instruction.add;
		RegisterOperand op1 = new RegisterOperand(Register.t0);
		RegisterOperand op2 = new RegisterOperand(Register.t1);
		RegisterOperand op3 = new RegisterOperand(Register.t2);
		List<Operand> list = new ArrayList<Operand>();
		list.add(op1);
		list.add(op2);
		list.add(op3);
		
		InstructionFormat instr = cpu.decode(instruction,list);
		RTypeInstruction rtype= instr.asRType();
		assertTrue(rtype.mode.equals(AddressMode.RTYPE));
		assertTrue(rtype.getInstruction().equals(Instruction.add));
		assertEquals(0x00,rtype.getSrc1().get().getWord()[0]);
		assertEquals(0x00,rtype.getSrc1().get().getWord()[1]);
		assertEquals(0x00,rtype.getSrc1().get().getWord()[2]);
		assertEquals(0x00,rtype.getSrc1().get().getWord()[3]);
		assertEquals(0x00,rtype.getSrc2().get().getWord()[0]);
		assertEquals(0x00,rtype.getSrc2().get().getWord()[1]);
		assertEquals(0x00,rtype.getSrc2().get().getWord()[2]);
		assertEquals(0x00,rtype.getSrc2().get().getWord()[3]);
		assertTrue(!rtype.getDest().isPresent());
		assertTrue(rtype.getDestReg().equals(Register.t0));
	}
	
	/**method will test the dest src imm operand format in the decode
	 * instruction used: addi
	 * @throws DecodeException if an error in the decode
	 */
	@Test
	public void testDestSrcImm() throws DecodeException
	{
		CPU cpu = new CPU(null,new IOConsole());
		Instruction instruction = Instruction.addi;
		RegisterOperand op1 = new RegisterOperand(Register.t0);
		RegisterOperand op2 = new RegisterOperand(Register.t1);
		IntegerOperand op3 = new IntegerOperand(5);
		List<Operand> list = new ArrayList<Operand>();
		list.add(op1);
		list.add(op2);
		list.add(op3);
		
		InstructionFormat instr = cpu.decode(instruction,list);
		RTypeInstruction rtype= instr.asRType();
		assertTrue(rtype.mode.equals(AddressMode.RTYPE));
		assertTrue(rtype.getInstruction().equals(Instruction.addi));
		assertEquals(0x00,rtype.getSrc1().get().getWord()[0]);
		assertEquals(0x00,rtype.getSrc1().get().getWord()[1]);
		assertEquals(0x00,rtype.getSrc1().get().getWord()[2]);
		assertEquals(0x00,rtype.getSrc1().get().getWord()[3]);
		assertEquals(0x00,rtype.getSrc2().get().getWord()[0]);
		assertEquals(0x00,rtype.getSrc2().get().getWord()[1]);
		assertEquals(0x00,rtype.getSrc2().get().getWord()[2]);
		assertEquals(5,rtype.getSrc2().get().getWord()[3]);
		assertTrue(!rtype.getDest().isPresent());
		assertTrue(rtype.getDestReg().equals(Register.t0));
	}
	
	/**tests the dest src imm u operand format
	 * instruction used: addiu
	 * @throws DecodeException if something goes wrong during decode
	 */
	@Test
	public void testDestSrcImmU() throws DecodeException
	{
		CPU cpu = new CPU(null,new IOConsole());
		Instruction instruction = Instruction.addiu;
		RegisterOperand op1 = new RegisterOperand(Register.t0);
		RegisterOperand op2 = new RegisterOperand(Register.t1);
		IntegerOperand op3 = new IntegerOperand(5);
		List<Operand> list = new ArrayList<Operand>();
		list.add(op1);
		list.add(op2);
		list.add(op3);
		
		InstructionFormat instr = cpu.decode(instruction,list);
		RTypeInstruction rtype= instr.asRType();
		assertTrue(rtype.mode.equals(AddressMode.RTYPE));
		assertTrue(rtype.getInstruction().equals(Instruction.addiu));
		assertEquals(0x00,rtype.getSrc1().get().getWord()[0]);
		assertEquals(0x00,rtype.getSrc1().get().getWord()[1]);
		assertEquals(0x00,rtype.getSrc1().get().getWord()[2]);
		assertEquals(0x00,rtype.getSrc1().get().getWord()[3]);
		assertEquals(0x00,rtype.getSrc2().get().getWord()[0]);
		assertEquals(0x00,rtype.getSrc2().get().getWord()[1]);
		assertEquals(0x00,rtype.getSrc2().get().getWord()[2]);
		assertEquals(5,rtype.getSrc2().get().getWord()[3]);
		assertTrue(!rtype.getDest().isPresent());
		assertTrue(rtype.getDestReg().equals(Register.t0));
	}
	
	/**test the dest src operand format
	 * instruction used is abs
	 * @throws DecodeException if error during decode
	 */
	@Test
	public void testDestSrc() throws DecodeException
	{
		CPU cpu = new CPU(null,new IOConsole());
		Instruction instruction = Instruction.abs;
		RegisterOperand op1 = new RegisterOperand(Register.t0);
		RegisterOperand op2 = new RegisterOperand(Register.t0);
		List<Operand> opList = new ArrayList<Operand>();
		opList.add(op1);
		opList.add(op2);
		
		InstructionFormat instr = cpu.decode(instruction,opList);
		RTypeInstruction rtype = instr.asRType();
		assertTrue(rtype.mode.equals(AddressMode.RTYPE));
		assertTrue(rtype.getInstruction().equals(Instruction.abs));
		assertEquals(0x00,rtype.getSrc1().get().getWord()[0]);
		assertEquals(0x00,rtype.getSrc1().get().getWord()[1]);
		assertEquals(0x00,rtype.getSrc1().get().getWord()[2]);
		assertEquals(0x00,rtype.getSrc1().get().getWord()[3]);
		assertTrue(!rtype.getSrc2().isPresent());
		assertTrue(!rtype.getDest().isPresent());
		assertTrue(rtype.getDestReg().equals(Register.t0));
	}
	
	/**test the dest imm operand format
	 * instruction used: li
	 * @throws DecodeException id problem during decode
	 */
	@Test
	public void testImm() throws DecodeException
	{
		CPU cpu = new CPU(null,new IOConsole());
		Instruction instruction = Instruction.li;
		RegisterOperand op1 = new RegisterOperand(Register.t0);
		IntegerOperand op2 = new IntegerOperand(7);
		List<Operand> opList = new ArrayList<Operand>();
		opList.add(op1);
		opList.add(op2);
		
		InstructionFormat instr = cpu.decode(instruction,opList);
		LSInstruction lstype = instr.asLSType();
		assertTrue(lstype.mode.equals(AddressMode.LSTYPE));
		assertTrue(lstype.getInstruction().equals(Instruction.li));
		assertEquals(0x00,lstype.getImmediate().get().getWord()[0]);
		assertEquals(0x00,lstype.getImmediate().get().getWord()[1]);
		assertEquals(0x00,lstype.getImmediate().get().getWord()[2]);
		assertEquals(7,lstype.getImmediate().get().getWord()[3]);
		assertTrue(!lstype.getMemAddress().isPresent());
		assertTrue(!lstype.getRegister().isPresent());
		assertTrue(lstype.getRegisterName().get().equals(Register.t0));
	}
	
	/**will test the no arguments operand format
	 * instruction tested: syscall
	 * @throws DecodeException if an error during the decode
	 */
	@Test
	public void testNoArguments() throws DecodeException
	{
		CPU cpu = new CPU(null,new IOConsole());
		Instruction instruction = Instruction.syscall;
		
		InstructionFormat instr = cpu.decode(instruction, new ArrayList<Operand>());
		SpecialInstruction special = instr.asSpecial();
		assertTrue(special.mode.equals(AddressMode.SPECIAL));
		assertTrue(special.getInstruction().equals(Instruction.syscall));
	}
	
	/**will test the label operand format
	 * instruction tested: j
	 * @throws DecodeException
	 */
	@Test
	public void testLabel() throws DecodeException
	{
		CPU cpu = new CPU(null,new IOConsole());
		Instruction instruction = Instruction.j;
		
		AddressOperand op1 = new AddressOperand();
		op1.labelName = Optional.of("testName");//test label name
		List<Operand> opList = new ArrayList<Operand>();
		opList.add(op1);
		
		InstructionFormat instr = cpu.decode(instruction, opList);
		JTypeInstruction jtype = instr.asJType();
		assertTrue(jtype.mode.equals(AddressMode.JTYPE));
		assertTrue(jtype.getInstruction().equals(Instruction.j));
		assertTrue(jtype.getJumpAddress().get().equals(Address.NULL));
		assertFalse(jtype.getCurrentAddress().isPresent());
	}
	
	/**will test the register operand format
	 * instruction tested: jr
	 * @throws DecodeException if something goes wrong during decode
	 */
	@Test
	public void testRegister() throws DecodeException
	{
		CPU cpu = new CPU(null,new IOConsole());
		Instruction instruction = Instruction.jr;
		
		RegisterOperand op1 = new RegisterOperand(Register.t0);
		List<Operand> opList = new ArrayList<Operand>();
		opList.add(op1);
		
		InstructionFormat instr = cpu.decode(instruction,opList);
		JTypeInstruction jtype = instr.asJType();
		assertTrue(jtype.mode.equals(AddressMode.JTYPE));
		assertTrue(jtype.getInstruction().equals(Instruction.jr));
		assertFalse(jtype.getCurrentAddress().isPresent());
		assertEquals(0,jtype.getJumpAddress().get().getValue());
	}
	
	/**will test the cmp cmp label operand format
	 * instruction used: beq
	 * @throws DecodeException if error during decode
	 */
	@Test
	public void testCmpCmpLabel() throws DecodeException
	{
		
	}
	
	/**will test the cmp label operand format
	 * instruction used: bltz
	 * @throws DecodeException if error during decode
	 */
	@Test
	public void testCmpLabel() throws DecodeException
	{
		
	}
	
	/**will test the src addr operand format
	 * instruction used: sw
	 * @throws DecodeException if error during decode
	 */
	@Test
	public void testSrcAddr() throws DecodeException
	{
		
	}
	
	/**will test the dest addr operand format
	 * instruction used: lw
	 * @throws DecodeException if error during decode
	 */
	@Test
	public void testDestAddr() throws DecodeException
	{
		
	}
}
