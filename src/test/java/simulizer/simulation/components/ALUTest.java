package simulizer.simulation.components;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import category.UnitTests;
import simulizer.assembler.representation.Instruction;
import simulizer.simulation.cpu.components.ALU;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.simulation.data.representation.Word;
import simulizer.simulation.exceptions.InstructionException;


/**tests all ALU operations
 * 
 * @author Charlie Street
 * 
 */
@Category({UnitTests.class})
public class ALUTest {

	private ALU alu;
	
	/**initialising the alu
	 * 
	 */
	@Before
	public void ALUSetup()
	{
		this.alu = new ALU();
	}
	
	/**produces an optional signed word 
	 * this is the type required by the alu
	 * @param num the number to convert
	 * @return number in correct format
	 */
	public Optional<Word> signedW(long num)
	{
		return Optional.of(new Word(DataConverter.encodeAsSigned(num)));
	}
	
	/**produces an optional unsigned word 
	 * this is the type required by the alu
	 * @param num the number to convert
	 * @return number in correct format
	 */
	public Optional<Word> unsignedW(long num)
	{
		return Optional.of(new Word(DataConverter.encodeAsUnsigned(num)));
	}
	
	/**executes a given alu operation with data passed in 
	 * 
	 * @param instruction the instruction to execute
	 * @param word1 the first word of data
	 * @param word2 the second word to execute
	 * @return the long result
	 * @throws InstructionException
	 */
	public long executeS(Instruction instruction, Optional<Word> word1, Optional<Word> word2) throws InstructionException
	{
		return DataConverter.decodeAsSigned(this.alu.execute(instruction, word1, word2).getWord());
	}
	
	/**executes a given alu operation with data passed in 
	 * 
	 * @param instruction the instruction to execute
	 * @param word1 the first word of data
	 * @param word2 the second word to execute
	 * @return the long result
	 * @throws InstructionException
	 */
	public long executeU(Instruction instruction, Optional<Word> word1, Optional<Word> word2) throws InstructionException
	{
		return DataConverter.decodeAsUnsigned(this.alu.execute(instruction, word1, word2).getWord());
	}
	
	/**testing all alu operations with 3 cases each
	 * @throws InstructionException when a bad instruction is used
	 * 
	 */
	@Test
	public void testALUOps() throws InstructionException
	{
		{//abs
			assertEquals(10,executeS(Instruction.abs,signedW(-10),Optional.empty()));
			assertEquals(27,executeS(Instruction.abs,signedW(27),Optional.empty()));
			assertEquals(0,executeS(Instruction.abs,signedW(0),Optional.empty()));
		}
		
		{//and
			assertEquals(0,executeS(Instruction.and,signedW(0),signedW(17)));
			assertEquals(16,executeS(Instruction.and,signedW(17),signedW(24)));
			assertEquals(-6,executeS(Instruction.and,signedW(-5),signedW(-2)));
		}
		
		{//andi
			assertEquals(0,executeS(Instruction.andi,signedW(0),signedW(17)));
			assertEquals(16,executeS(Instruction.andi,signedW(17),signedW(24)));
			assertEquals(-6,executeS(Instruction.andi,signedW(-5),signedW(-2)));
		}
		
		{//add
			
			assertEquals(11,executeS(Instruction.add,signedW(4),signedW(7)));
			assertEquals(-14,executeS(Instruction.add,signedW(-4),signedW(-10)));
			assertEquals(-1,executeS(Instruction.add,signedW(-7),signedW(6)));
		}
		
		{//addu
			
			assertEquals((long)(Math.pow(2, 31) + Math.pow(2, 31) - 1),executeU(Instruction.addu,unsignedW((long)Math.pow(2,31)),unsignedW((long)Math.pow(2,31)-1)));
			assertEquals((long)(Math.pow(2,31)),executeU(Instruction.addu,unsignedW((long)Math.pow(2,30)),unsignedW((long)Math.pow(2,30))));
			assertEquals(4,executeU(Instruction.addu,unsignedW(0),unsignedW(4)));
			assertEquals(4,executeU(Instruction.addu,unsignedW(20),unsignedW(-16))); // interpret as unsigned: still works
		}
		
		{//addi (same tests as for add)
			assertEquals(11,executeS(Instruction.addi,signedW(4),signedW(7)));
			assertEquals(-14,executeS(Instruction.addi,signedW(-4),signedW(-10)));
			assertEquals(-1,executeS(Instruction.addi,signedW(-7),signedW(6)));
		}
		
		{//addiu
			assertEquals((long)(Math.pow(2, 31) + Math.pow(2, 31) - 1),executeU(Instruction.addiu,unsignedW((long)Math.pow(2,31)),unsignedW((long)Math.pow(2,31)-1)));
			assertEquals((long)(Math.pow(2,31)),executeU(Instruction.addiu,unsignedW((long)Math.pow(2,30)),unsignedW((long)Math.pow(2,30))));
			assertEquals(4,executeU(Instruction.addiu,unsignedW(0),unsignedW(4)));
			assertEquals(4,executeU(Instruction.addiu,unsignedW(20),unsignedW(-16))); // interpret as unsigned: still works
		}
		
		{//sub
			assertEquals(-3,executeS(Instruction.sub,signedW(4),signedW(7)));
			assertEquals(3,executeS(Instruction.sub,signedW(7),signedW(4)));
			assertEquals(6,executeS(Instruction.sub,signedW(-4),signedW(-10)));
		}
		
		{//subu
			assertEquals(0,executeU(Instruction.subu,unsignedW((long)Math.pow(2,31)),unsignedW((long)Math.pow(2,31))));
			assertEquals((long)Math.pow(2,31),executeU(Instruction.subu,unsignedW((long)Math.pow(2,31)),unsignedW(0)));
			assertEquals((long)Math.pow(2,32)-2,executeU(Instruction.subu,unsignedW((long)Math.pow(2,32)-1),unsignedW(1)));
			assertEquals(4,executeS(Instruction.subu,unsignedW(20),unsignedW(16)));
		}
		
		{//subi
			assertEquals(-3,executeS(Instruction.subi,signedW(4),signedW(7)));
			assertEquals(3,executeS(Instruction.subi,signedW(7),signedW(4)));
			assertEquals(6,executeS(Instruction.subi,signedW(-4),signedW(-10)));
		}
		
		{//subiu
			assertEquals(0,executeU(Instruction.subu,unsignedW((long)Math.pow(2,31)),unsignedW((long)Math.pow(2,31))));
			assertEquals((long)Math.pow(2,31),executeU(Instruction.subu,unsignedW((long)Math.pow(2,31)),unsignedW(0)));
			assertEquals((long)Math.pow(2,32)-2,executeU(Instruction.subu,unsignedW((long)Math.pow(2,32)-1),unsignedW(1)));
			assertEquals(4,executeS(Instruction.subiu,unsignedW(20),unsignedW(16)));
		}
		
		{//mul
			
			assertEquals(1073709056L,executeS(Instruction.mul,signedW((long)(Math.pow(2,15)-1)),signedW((long)Math.pow(2,15))));
			assertEquals(0,executeS(Instruction.mul,signedW(0),signedW((long)Math.pow(2,15))));
			assertEquals(12,executeS(Instruction.mul,signedW(-4),signedW(-3)));
		}
		
		{//mulo (check this one)
			assertEquals(1073709056L,executeS(Instruction.mulo,signedW((long)(Math.pow(2,15)-1)),signedW((long)Math.pow(2,15))));
			assertEquals(0,executeS(Instruction.mulo,signedW(0),signedW((long)Math.pow(2,15))));
			assertEquals(12,executeS(Instruction.mulo,signedW(-4),signedW(-3)));
		}
		
		{//mulou
			
			assertEquals(4294901760L,executeU(Instruction.mulou,unsignedW((long)(Math.pow(2,16)-1)),unsignedW((long)Math.pow(2,16))));
			assertEquals(0,executeU(Instruction.mulou,unsignedW(0),unsignedW((long)Math.pow(2,16))));
			assertEquals(12,executeU(Instruction.mulou,unsignedW(4),unsignedW(3)));
		}
		
		{//div
			assertEquals(0,executeS(Instruction.div,signedW(0),signedW(4)));
			assertEquals(2,executeS(Instruction.div,signedW(4),signedW(2)));
			assertEquals(-2,executeS(Instruction.div,signedW(4),signedW(-2)));
		}
		
		{//divu
			assertEquals(0,executeU(Instruction.divu,unsignedW(0),unsignedW(4)));
			assertEquals(1,executeU(Instruction.divu,unsignedW((long)(Math.pow(2, 32)-1)),unsignedW((long)(Math.pow(2, 32)-1))));
			assertEquals(2,executeU(Instruction.divu,unsignedW(4),unsignedW(2)));
		}
		
		{//rem
			assertEquals(0,executeS(Instruction.rem,signedW(0),signedW(4)));
			assertEquals(0,executeS(Instruction.rem,signedW(4),signedW(2)));
			assertEquals(0,executeS(Instruction.rem,signedW(4),signedW(-2)));
		}
		
		{//remu
			assertEquals(0,executeU(Instruction.remu,unsignedW(0),unsignedW(4)));
			assertEquals(0,executeU(Instruction.remu,unsignedW((long)(Math.pow(2, 32)-1)),unsignedW((long)(Math.pow(2, 32)-1))));
			assertEquals(0,executeU(Instruction.remu,unsignedW(4),unsignedW(2)));
		}
		
		{//neg
			assertEquals(0,executeS(Instruction.neg,signedW(0),Optional.empty()));
			assertEquals(-1,executeS(Instruction.neg,signedW(1),Optional.empty()));
			assertEquals(1,executeS(Instruction.neg,signedW(-1),Optional.empty()));
		}
		
		{//negu
			assertEquals(0,executeU(Instruction.negu,signedW(0),Optional.empty()));
			assertEquals((long)Math.pow(2, 30),executeU(Instruction.negu,signedW(-1*(long)Math.pow(2, 30)),Optional.empty()));
			assertEquals(1,executeU(Instruction.negu,signedW(-1),Optional.empty()));
		}
		
		{//nor
			assertEquals(-1,executeS(Instruction.nor,signedW(0),signedW(0)));
			assertEquals(-2,executeS(Instruction.nor,signedW(0),signedW(1)));
			assertEquals(0,executeS(Instruction.nor,signedW(-1),signedW(1)));
		}
		 
		{//not
			assertEquals(-1,executeS(Instruction.not,signedW(0),Optional.empty()));
			assertEquals(0,executeS(Instruction.not,signedW(-1),Optional.empty()));
			assertEquals(-2,executeS(Instruction.not,signedW(1),Optional.empty()));
		}
		
		{//or			
			assertEquals(1,executeS(Instruction.or,signedW(0),signedW(1)));
			assertEquals(5,executeS(Instruction.or,signedW(1),signedW(4)));
			assertEquals(20,executeS(Instruction.or,signedW(16),signedW(4)));
		}
		
		{//ori
			assertEquals(1,executeS(Instruction.ori,signedW(0),signedW(1)));
			assertEquals(5,executeS(Instruction.ori,signedW(1),signedW(4)));
			assertEquals(20,executeS(Instruction.ori,signedW(16),signedW(4)));
		}
		
		{//xor
			assertEquals(10,executeS(Instruction.xor,signedW(4),signedW(14)));
			assertEquals(2,executeS(Instruction.xor,signedW(3),signedW(1)));
			assertEquals(3,executeS(Instruction.xor,signedW(3),signedW(0)));
		}
		
		{//xori
			assertEquals(10,executeS(Instruction.xor,signedW(4),signedW(14)));
			assertEquals(2,executeS(Instruction.xor,signedW(3),signedW(1)));
			assertEquals(3,executeS(Instruction.xor,signedW(3),signedW(0)));
		}
		
		{//b
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeS(Instruction.b,signedW(0),Optional.empty()));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeS(Instruction.b,signedW(-20),Optional.empty()));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeS(Instruction.b,signedW(20),Optional.empty()));
		}
		
		{//beq
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeS(Instruction.beq,signedW(0),signedW(1)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeS(Instruction.beq,signedW(0),signedW(0)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeS(Instruction.beq,signedW(0),signedW(-1)));
		}
		
		{//bne
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeS(Instruction.bne,signedW(0),signedW(1)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeS(Instruction.bne,signedW(0),signedW(0)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeS(Instruction.bne,signedW(0),signedW(-1)));
		}
		
		{//bgez (entering random items in second slot to test that it isn't used by the ALU)
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeS(Instruction.bgez,signedW(0),signedW(1)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeS(Instruction.bgez,signedW(-1),signedW(0)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeS(Instruction.bgez,signedW(1),signedW(-1)));
		}
		
		{//bgtz (now with Optional.empty() as second item)
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeS(Instruction.bgtz,signedW(0),Optional.empty()));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeS(Instruction.bgtz,signedW(-1),Optional.empty()));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeS(Instruction.bgtz,signedW(1),Optional.empty()));
		}
		
		{//blez 
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeS(Instruction.blez,signedW(0),Optional.empty()));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeS(Instruction.blez,signedW(-1),Optional.empty()));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeS(Instruction.blez,signedW(1),Optional.empty()));
		}
		
		{//bltz
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeS(Instruction.bltz,signedW(0),Optional.empty()));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeS(Instruction.bltz,signedW(-1),Optional.empty()));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeS(Instruction.bltz,signedW(1),Optional.empty()));
		}
		
		{//beqz
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeS(Instruction.beqz,signedW(0),Optional.empty()));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeS(Instruction.beqz,signedW(-1),Optional.empty()));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeS(Instruction.beqz,signedW(1),Optional.empty()));
		}
		
		{//bge
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeS(Instruction.bge,signedW(0),signedW(1)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeS(Instruction.bge,signedW(0),signedW(0)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeS(Instruction.bge,signedW(0),signedW(-1)));
		}
		
		{//bgeu
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeU(Instruction.bgeu,unsignedW(0),unsignedW((long)Math.pow(2, 32)-1)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeU(Instruction.bgeu,unsignedW(0),unsignedW(0)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeU(Instruction.bgeu,unsignedW((long)Math.pow(2, 32)-1),unsignedW(0)));
		}
		
		{//bgt
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeS(Instruction.bgt,signedW(0),signedW(1)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeS(Instruction.bgt,signedW(0),signedW(0)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeS(Instruction.bgt,signedW(0),signedW(-1)));
		}
		
		{//bgtu
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeU(Instruction.bgtu,unsignedW(0),unsignedW((long)Math.pow(2, 32)-1)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeU(Instruction.bgtu,unsignedW(0),unsignedW(0)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeU(Instruction.bgtu,unsignedW((long)Math.pow(2, 32)-1),unsignedW(0)));
		}
		
		{//ble
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeS(Instruction.ble,signedW(0),signedW(1)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeS(Instruction.ble,signedW(0),signedW(0)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeS(Instruction.ble,signedW(0),signedW(-1)));
		}
		
		{//bleu
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeU(Instruction.bleu,unsignedW(0),unsignedW((long)Math.pow(2, 32)-1)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeU(Instruction.bleu,unsignedW(0),unsignedW(0)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeU(Instruction.bleu,unsignedW((long)Math.pow(2, 32)-1),unsignedW(0)));
		}
		
		{//blt
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeS(Instruction.blt,signedW(0),signedW(1)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeS(Instruction.blt,signedW(0),signedW(0)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeS(Instruction.blt,signedW(0),signedW(-1)));
		}
		
		{//bltu
			assertEquals(DataConverter.decodeAsSigned(ALU.branchTrue),executeU(Instruction.bltu,unsignedW(0),unsignedW((long)Math.pow(2, 32)-1)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeU(Instruction.bltu,unsignedW(0),unsignedW(0)));
			assertEquals(DataConverter.decodeAsSigned(ALU.branchFalse),executeU(Instruction.bltu,unsignedW((long)Math.pow(2, 32)-1),unsignedW(0)));
		}
		
		{//seq
			assertEquals(0,executeS(Instruction.seq,signedW(0),signedW(1)));
			assertEquals(1,executeS(Instruction.seq,signedW(0),signedW(0)));
			assertEquals(0,executeS(Instruction.seq,signedW(0),signedW(-1)));
		}
		
		{//sne
			assertEquals(1,executeS(Instruction.sne,signedW(0),signedW(1)));
			assertEquals(0,executeS(Instruction.sne,signedW(0),signedW(0)));
			assertEquals(1,executeS(Instruction.sne,signedW(0),signedW(-1)));
		}
		
		{//sge
			assertEquals(0,executeS(Instruction.sge,signedW(0),signedW(1)));
			assertEquals(1,executeS(Instruction.sge,signedW(0),signedW(0)));
			assertEquals(1,executeS(Instruction.sge,signedW(0),signedW(-1)));
		}
		
		{//sgeu
			assertEquals(0,executeU(Instruction.sgeu,unsignedW(0),unsignedW((long)Math.pow(2,32)-1)));
			assertEquals(1,executeU(Instruction.sgeu,unsignedW(0),unsignedW(0)));
			assertEquals(1,executeU(Instruction.sgeu,unsignedW((long)Math.pow(2,32)-1),unsignedW(0)));
		}
		
		{//sgt
			assertEquals(0,executeS(Instruction.sgt,signedW(0),signedW(1)));
			assertEquals(0,executeS(Instruction.sgt,signedW(0),signedW(0)));
			assertEquals(1,executeS(Instruction.sgt,signedW(0),signedW(-1)));
		}
		
		{//sgtu
			assertEquals(0,executeU(Instruction.sgtu,unsignedW(0),unsignedW((long)Math.pow(2,32)-1)));
			assertEquals(0,executeU(Instruction.sgtu,unsignedW(0),unsignedW(0)));
			assertEquals(1,executeU(Instruction.sgtu,unsignedW((long)Math.pow(2,32)-1),unsignedW(0)));
		}
		
		{//sle
			assertEquals(1,executeS(Instruction.sle,signedW(0),signedW(1)));
			assertEquals(1,executeS(Instruction.sle,signedW(0),signedW(0)));
			assertEquals(0,executeS(Instruction.sle,signedW(0),signedW(-1)));
		}
		
		{//sleu
			assertEquals(1,executeU(Instruction.sleu,unsignedW(0),unsignedW((long)Math.pow(2,32)-1)));
			assertEquals(1,executeU(Instruction.sleu,unsignedW(0),unsignedW(0)));
			assertEquals(0,executeU(Instruction.sleu,unsignedW((long)Math.pow(2,32)-1),unsignedW(0)));
		}
		
		{//slt
			assertEquals(1,executeS(Instruction.slt,signedW(0),signedW(1)));
			assertEquals(0,executeS(Instruction.slt,signedW(0),signedW(0)));
			assertEquals(0,executeS(Instruction.slt,signedW(0),signedW(-1)));
		}
		
		{//sltu
			assertEquals(1,executeU(Instruction.sltu,unsignedW(0),unsignedW((long)Math.pow(2,32)-1)));
			assertEquals(0,executeU(Instruction.sltu,unsignedW(0),unsignedW(0)));
			assertEquals(0,executeU(Instruction.sltu,unsignedW((long)Math.pow(2,32)-1),unsignedW(0)));
		}
		
		{//slti
			assertEquals(1,executeS(Instruction.slti,signedW(0),signedW(1)));
			assertEquals(0,executeS(Instruction.slti,signedW(0),signedW(0)));
			assertEquals(0,executeS(Instruction.slti,signedW(0),signedW(-1)));
		}
		
		{//sltiu
			assertEquals(1,executeU(Instruction.sltiu,unsignedW(0),unsignedW((long)Math.pow(2,32)-1)));
			assertEquals(0,executeU(Instruction.sltiu,unsignedW(0),unsignedW(0)));
			assertEquals(0,executeU(Instruction.sltiu,unsignedW((long)Math.pow(2,32)-1),unsignedW(0)));
		}
		
		{//move
			assertEquals(0, executeS(Instruction.move, signedW(0),signedW(17)));
			assertEquals(-6, executeS(Instruction.move, signedW(-6),signedW(0)));
			assertEquals(5, executeS(Instruction.move, signedW(5),Optional.empty()));
		}	
		
		{//sll
			assertEquals(28,executeU(Instruction.sll,unsignedW(7),unsignedW(2)));
			assertEquals(384,executeU(Instruction.sll,unsignedW(48),unsignedW(3)));
			assertEquals(3200,executeU(Instruction.sll,unsignedW(100),unsignedW(5)));
		}
		
		{//sllv
			assertEquals(28,executeU(Instruction.sllv,unsignedW(7),unsignedW(2)));
			assertEquals(384,executeU(Instruction.sllv,unsignedW(48),unsignedW(3)));
			assertEquals(3200,executeU(Instruction.sllv,unsignedW(100),unsignedW(5)));
		}
		
		{//srl
			assertEquals(3,executeU(Instruction.srl,unsignedW(7),unsignedW(1)));
			assertEquals(6,executeU(Instruction.srl,unsignedW(48),unsignedW(3)));
			assertEquals(3,executeU(Instruction.srl,unsignedW(100),unsignedW(5)));
		}
		
		{//srlv
			assertEquals(3,executeU(Instruction.srlv,unsignedW(7),unsignedW(1)));
			assertEquals(6,executeU(Instruction.srlv,unsignedW(48),unsignedW(3)));
			assertEquals(3,executeU(Instruction.srlv,unsignedW(100),unsignedW(5)));
		}
		
		{//sra
			assertEquals(-14,executeS(Instruction.sra,signedW(-53),signedW(2)));
			assertEquals(-3,executeS(Instruction.sra,signedW(-6),signedW(1)));
			assertEquals(6,executeS(Instruction.sra,signedW(48),signedW(3)));
		}
		
		{//srav
			assertEquals(-14,executeS(Instruction.srav,signedW(-53),signedW(2)));
			assertEquals(-3,executeS(Instruction.srav,signedW(-6),signedW(1)));
			assertEquals(6,executeS(Instruction.srav,signedW(48),signedW(3)));
		}
		
		{//rol
			assertEquals(384,executeU(Instruction.rol,unsignedW(48),unsignedW(3)));
			assertEquals(4261412866L,executeU(Instruction.rol,unsignedW(2130706433),unsignedW(1)));
			assertEquals(1,executeU(Instruction.rol,unsignedW(2147483648L),unsignedW(1)));
		}
		
		{//ror 
			assertEquals(6,executeU(Instruction.ror,unsignedW(48),unsignedW(3)));
			assertEquals(2147483648L,executeU(Instruction.ror,unsignedW(1),unsignedW(1)));
			assertEquals(2130706433,executeU(Instruction.ror,unsignedW(4261412866L),unsignedW(1)));
		}
	}
}
