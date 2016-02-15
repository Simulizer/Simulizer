package simulizer.simulation.components;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Before;

import org.junit.Test;

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
	
	/**testing all alu operations with 3 cases each
	 * @throws InstructionException when a bad instruction is used
	 * 
	 */
	@Test
	public void testALUOps() throws InstructionException
	{
		{//abs
			Optional<Word> minusTen = Optional.of(new Word(DataConverter.encodeAsSigned(-10)));
			Optional<Word> empty = Optional.empty();
			assertEquals(10,DataConverter.decodeAsSigned(this.alu.execute(Instruction.abs, minusTen, empty).getWord()));
			
			Optional<Word> twentySeven = Optional.of(new Word(DataConverter.encodeAsSigned(27)));
			assertEquals(27,DataConverter.decodeAsSigned(this.alu.execute(Instruction.abs, twentySeven, empty).getWord()));
			
			Optional<Word> zero = Optional.of(new Word(DataConverter.encodeAsSigned(0)));
			assertEquals(0,DataConverter.decodeAsSigned(this.alu.execute(Instruction.abs, zero, empty).getWord()));
		}
		
		{//and
			Optional<Word> zero = Optional.of(new Word(DataConverter.encodeAsSigned(0)));
			Optional<Word> seventeen = Optional.of(new Word(DataConverter.encodeAsSigned(17)));
			assertEquals(0,DataConverter.decodeAsSigned(this.alu.execute(Instruction.and, zero, seventeen).getWord()));
			
			Optional<Word> twentyFour = Optional.of(new Word(DataConverter.encodeAsSigned(24)));
			assertEquals(16,DataConverter.decodeAsSigned(this.alu.execute(Instruction.and, seventeen, twentyFour).getWord()));
			
			Optional<Word> minusFive = Optional.of(new Word(DataConverter.encodeAsSigned(-5)));
			Optional<Word> minusTwo = Optional.of(new Word(DataConverter.encodeAsSigned(-2)));
			assertEquals(-6,DataConverter.decodeAsSigned(this.alu.execute(Instruction.and, minusFive, minusTwo).getWord()));
		}
		
		{//add
			Optional<Word> four = Optional.of(new Word(DataConverter.encodeAsSigned(4)));
			Optional<Word> seven = Optional.of(new Word(DataConverter.encodeAsSigned(7)));
			assertEquals(11,DataConverter.decodeAsSigned(this.alu.execute(Instruction.add, four, seven).getWord()));
			
			Optional<Word> minusFour = Optional.of(new Word(DataConverter.encodeAsSigned(-4)));
			Optional<Word> minusTen = Optional.of(new Word(DataConverter.encodeAsSigned(-10)));
			assertEquals(-14,DataConverter.decodeAsSigned(this.alu.execute(Instruction.add, minusFour, minusTen).getWord()));
			
			Optional<Word> minusSeven = Optional.of(new Word(DataConverter.encodeAsSigned(-7)));
			Optional<Word> six = Optional.of(new Word(DataConverter.encodeAsSigned(6)));
			assertEquals(-1,DataConverter.decodeAsSigned(this.alu.execute(Instruction.add, minusSeven, six).getWord()));
		}
		
		{//addu
			Optional<Word> twoThirtyOne = Optional.of(new Word(DataConverter.encodeAsUnsigned((long)Math.pow(2,31))));
			Optional<Word> twoThirtyMinusOne = Optional.of(new Word(DataConverter.encodeAsUnsigned((long)Math.pow(2,31)-1)));
			assertEquals((long)(Math.pow(2, 31) + Math.pow(2, 31) - 1),DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.addu,twoThirtyOne,twoThirtyMinusOne).getWord()));
			
			Optional<Word> twoThirty = Optional.of(new Word(DataConverter.encodeAsUnsigned((long)Math.pow(2,30))));
			assertEquals((long)(Math.pow(2,31)),DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.addu,twoThirty,twoThirty).getWord()));
			
			Optional<Word> zero = Optional.of(new Word(DataConverter.encodeAsUnsigned(0)));
			Optional<Word> four = Optional.of(new Word(DataConverter.encodeAsUnsigned(4)));
			assertEquals(4,DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.addu, zero, four).getWord()));
		}
		
		{//addi (same tests as for add)
			Optional<Word> four = Optional.of(new Word(DataConverter.encodeAsSigned(4)));
			Optional<Word> seven = Optional.of(new Word(DataConverter.encodeAsSigned(7)));
			assertEquals(11,DataConverter.decodeAsSigned(this.alu.execute(Instruction.addi, four, seven).getWord()));
			
			Optional<Word> minusFour = Optional.of(new Word(DataConverter.encodeAsSigned(-4)));
			Optional<Word> minusTen = Optional.of(new Word(DataConverter.encodeAsSigned(-10)));
			assertEquals(-14,DataConverter.decodeAsSigned(this.alu.execute(Instruction.addi, minusFour, minusTen).getWord()));
			
			Optional<Word> minusSeven = Optional.of(new Word(DataConverter.encodeAsSigned(-7)));
			Optional<Word> six = Optional.of(new Word(DataConverter.encodeAsSigned(6)));
			assertEquals(-1,DataConverter.decodeAsSigned(this.alu.execute(Instruction.addi, minusSeven, six).getWord()));
		}
		
		{//addiu
			Optional<Word> twoThirtyOne = Optional.of(new Word(DataConverter.encodeAsUnsigned((long)Math.pow(2,31))));
			Optional<Word> twoThirtyMinusOne = Optional.of(new Word(DataConverter.encodeAsUnsigned((long)Math.pow(2,31)-1)));
			assertEquals((long)(Math.pow(2, 31) + Math.pow(2, 31) - 1),DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.addiu,twoThirtyOne,twoThirtyMinusOne).getWord()));
			
			Optional<Word> twoThirty = Optional.of(new Word(DataConverter.encodeAsUnsigned((long)Math.pow(2,30))));
			assertEquals((long)(Math.pow(2,31)),DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.addiu,twoThirty,twoThirty).getWord()));
			
			Optional<Word> zero = Optional.of(new Word(DataConverter.encodeAsUnsigned(0)));
			Optional<Word> four = Optional.of(new Word(DataConverter.encodeAsUnsigned(4)));
			assertEquals(4,DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.addiu, zero, four).getWord()));
		}
		
		{//sub
			Optional<Word> four = Optional.of(new Word(DataConverter.encodeAsSigned(4)));
			Optional<Word> seven = Optional.of(new Word(DataConverter.encodeAsSigned(7)));
			assertEquals(-3,DataConverter.decodeAsSigned(this.alu.execute(Instruction.sub, four, seven).getWord()));
			assertEquals(3,DataConverter.decodeAsSigned(this.alu.execute(Instruction.sub, seven, four).getWord()));
			
			Optional<Word> minusFour = Optional.of(new Word(DataConverter.encodeAsSigned(-4)));
			Optional<Word> minusTen = Optional.of(new Word(DataConverter.encodeAsSigned(-10)));
			assertEquals(6,DataConverter.decodeAsSigned(this.alu.execute(Instruction.sub, minusFour, minusTen).getWord()));
		}
		
		{//subu
			Optional<Word> minusTwoThirtyOneMinusOne = Optional.of(new Word(DataConverter.encodeAsUnsigned(-1*(long)(Math.pow(2,31)-1))));
			Optional<Word> twoThirtyOne = Optional.of(new Word(DataConverter.encodeAsUnsigned((long)Math.pow(2,31))));
			assertEquals(-1*(long)(Math.pow(2, 32)-1),DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.subu, minusTwoThirtyOneMinusOne, twoThirtyOne).getWord()));
			assertEquals(0,DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.subu,twoThirtyOne,twoThirtyOne).getWord()));
			
			Optional<Word> zero = Optional.of(new Word(DataConverter.encodeAsUnsigned(0)));
			Optional<Word> twoThirty = Optional.of(new Word(DataConverter.encodeAsUnsigned((long)Math.pow(2,30))));
			assertEquals(-1*(long)Math.pow(2, 30),DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.subu,zero,twoThirty).getWord()));
		}
		
		{//subi
			Optional<Word> four = Optional.of(new Word(DataConverter.encodeAsSigned(4)));
			Optional<Word> seven = Optional.of(new Word(DataConverter.encodeAsSigned(7)));
			assertEquals(-3,DataConverter.decodeAsSigned(this.alu.execute(Instruction.subi, four, seven).getWord()));
			assertEquals(3,DataConverter.decodeAsSigned(this.alu.execute(Instruction.subi, seven, four).getWord()));
			
			Optional<Word> minusFour = Optional.of(new Word(DataConverter.encodeAsSigned(-4)));
			Optional<Word> minusTen = Optional.of(new Word(DataConverter.encodeAsSigned(-10)));
			assertEquals(6,DataConverter.decodeAsSigned(this.alu.execute(Instruction.subi, minusFour, minusTen).getWord()));
		}
		
		{//subiu
			Optional<Word> minusTwoThirtyOneMinusOne = Optional.of(new Word(DataConverter.encodeAsUnsigned(-1*(long)(Math.pow(2,31)-1))));
			Optional<Word> twoThirtyOne = Optional.of(new Word(DataConverter.encodeAsUnsigned((long)Math.pow(2,31))));
			assertEquals(-1*(long)(Math.pow(2, 32)-1),DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.subiu, minusTwoThirtyOneMinusOne, twoThirtyOne).getWord()));
			assertEquals(0,DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.subiu,twoThirtyOne,twoThirtyOne).getWord()));
			
			Optional<Word> zero = Optional.of(new Word(DataConverter.encodeAsUnsigned(0)));
			Optional<Word> twoThirty = Optional.of(new Word(DataConverter.encodeAsUnsigned((long)Math.pow(2,30))));
			assertEquals(-1*(long)Math.pow(2, 30),DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.subiu,zero,twoThirty).getWord()));
		}
		
		{//mul
			Optional<Word> twoFifteenMinusOne = Optional.of(new Word(DataConverter.encodeAsSigned((long)(Math.pow(2,15)-1))));
			Optional<Word> twoFifteen = Optional.of(new Word(DataConverter.encodeAsSigned((long)Math.pow(2,15))));
			assertEquals(1073709056L,DataConverter.decodeAsSigned(this.alu.execute(Instruction.mul, twoFifteenMinusOne, twoFifteen).getWord()));
			
			Optional<Word> zero = Optional.of(new Word(DataConverter.encodeAsSigned(0)));
			assertEquals(0,DataConverter.decodeAsSigned(this.alu.execute(Instruction.mul, zero, twoFifteen).getWord()));
			
			Optional<Word> minusFour = Optional.of(new Word(DataConverter.encodeAsSigned(-4)));
			Optional<Word> minusThree = Optional.of(new Word(DataConverter.encodeAsSigned(-3)));
			assertEquals(12,DataConverter.decodeAsSigned(this.alu.execute(Instruction.mul, minusFour, minusThree).getWord()));
		}
		
		{//mulo (check this one)
			Optional<Word> twoFifteenMinusOne = Optional.of(new Word(DataConverter.encodeAsSigned((long)(Math.pow(2,15)-1))));
			Optional<Word> twoFifteen = Optional.of(new Word(DataConverter.encodeAsSigned((long)Math.pow(2,15))));
			assertEquals(1073709056L,DataConverter.decodeAsSigned(this.alu.execute(Instruction.mulo, twoFifteenMinusOne, twoFifteen).getWord()));
			
			Optional<Word> zero = Optional.of(new Word(DataConverter.encodeAsSigned(0)));
			assertEquals(0,DataConverter.decodeAsSigned(this.alu.execute(Instruction.mulo, zero, twoFifteen).getWord()));
			
			Optional<Word> minusFour = Optional.of(new Word(DataConverter.encodeAsSigned(-4)));
			Optional<Word> minusThree = Optional.of(new Word(DataConverter.encodeAsSigned(-3)));
			assertEquals(12,DataConverter.decodeAsSigned(this.alu.execute(Instruction.mulo, minusFour, minusThree).getWord()));
		}
		
		{//mulou
			Optional<Word> twoSixteenMinusOne = Optional.of(new Word(DataConverter.encodeAsUnsigned((long)(Math.pow(2,16)-1))));
			Optional<Word> twoSixteen = Optional.of(new Word(DataConverter.encodeAsUnsigned((long)Math.pow(2,16))));
			assertEquals(4294901760L,DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.mulou, twoSixteenMinusOne, twoSixteen).getWord()));
			
			Optional<Word> zero = Optional.of(new Word(DataConverter.encodeAsUnsigned(0)));
			assertEquals(0,DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.mulou, zero, twoSixteen).getWord()));
			
			Optional<Word> minusFour = Optional.of(new Word(DataConverter.encodeAsUnsigned(-4)));
			Optional<Word> minusThree = Optional.of(new Word(DataConverter.encodeAsUnsigned(-3)));
			assertEquals(12,DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.mulou, minusFour, minusThree).getWord()));
		}
		
		{//div
			Optional<Word> zero = Optional.of(new Word(DataConverter.encodeAsSigned(0)));
			Optional<Word> four = Optional.of(new Word(DataConverter.encodeAsSigned(4)));
			assertEquals(0,DataConverter.decodeAsSigned(this.alu.execute(Instruction.div,zero,four).getWord()));
			
			Optional<Word> two = Optional.of(new Word(DataConverter.encodeAsSigned(2)));
			assertEquals(2,DataConverter.decodeAsSigned(this.alu.execute(Instruction.div,four,two).getWord()));
			
			Optional<Word> minusTwo = Optional.of(new Word(DataConverter.encodeAsSigned(-2)));
			assertEquals(-2,DataConverter.decodeAsSigned(this.alu.execute(Instruction.div,four,minusTwo).getWord()));
		}
		
		{//divu
			Optional<Word> zero = Optional.of(new Word(DataConverter.encodeAsUnsigned(0)));
			Optional<Word> four = Optional.of(new Word(DataConverter.encodeAsUnsigned(4)));
			assertEquals(0,DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.divu,zero,four).getWord()));
			
			Optional<Word> twoThirtyTwoMinusOne = Optional.of(new Word(DataConverter.encodeAsUnsigned((long)(Math.pow(2, 32)-1))));
			assertEquals(1,DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.divu,twoThirtyTwoMinusOne,twoThirtyTwoMinusOne).getWord()));
			
			Optional<Word> minusTwoThirtyTwoMinusOne = Optional.of(new Word(DataConverter.encodeAsUnsigned(-1*(long)(Math.pow(2, 32)-1))));
			assertEquals(-1,DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.divu,twoThirtyTwoMinusOne,minusTwoThirtyTwoMinusOne).getWord()));
		}
		
		{//neg
			Optional<Word> empty = Optional.empty();
			Optional<Word> zero = Optional.of(new Word(DataConverter.encodeAsSigned(0)));
			Optional<Word> one = Optional.of(new Word(DataConverter.encodeAsSigned(1)));
			Optional<Word> minusOne = Optional.of(new Word(DataConverter.encodeAsSigned(-1)));
			assertEquals(0,DataConverter.decodeAsSigned(this.alu.execute(Instruction.neg, zero, empty).getWord()));
			assertEquals(-1,DataConverter.decodeAsSigned(this.alu.execute(Instruction.neg, one, empty).getWord()));
			assertEquals(1,DataConverter.decodeAsSigned(this.alu.execute(Instruction.neg, minusOne, empty).getWord()));
		}
		
		{//negu
			Optional<Word> empty = Optional.empty();
			Optional<Word> zero = Optional.of(new Word(DataConverter.encodeAsUnsigned(0)));
			Optional<Word> twoThirtyOne = Optional.of(new Word(DataConverter.encodeAsUnsigned((long)Math.pow(2, 31))));
			Optional<Word> minusTwoThirtyOne = Optional.of(new Word(DataConverter.encodeAsUnsigned(-1*(long)Math.pow(2, 31))));
			assertEquals(0,DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.negu, zero, empty).getWord()));
			assertEquals(-1*(long)Math.pow(2, 31),DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.negu, twoThirtyOne, empty).getWord()));
			assertEquals((long)Math.pow(2, 31),DataConverter.decodeAsUnsigned(this.alu.execute(Instruction.negu, minusTwoThirtyOne, empty).getWord()));
		}
		
		{//nor
			Optional<Word> zero = Optional.of(new Word(DataConverter.encodeAsSigned(0)));
			assertEquals(-1,DataConverter.decodeAsSigned(this.alu.execute(Instruction.nor,zero,zero).getWord()));
			
			Optional<Word> one = Optional.of(new Word(DataConverter.encodeAsSigned(1)));
			assertEquals(-2,DataConverter.decodeAsSigned(this.alu.execute(Instruction.nor,zero,one).getWord()));
			
			Optional<Word> minusOne = Optional.of(new Word(DataConverter.encodeAsSigned(-1)));
			assertEquals(0,DataConverter.decodeAsSigned(this.alu.execute(Instruction.nor,minusOne,one).getWord()));
		}
		
		{//not
			Optional<Word> empty = Optional.empty();
			Optional<Word> zero = Optional.of(new Word(DataConverter.encodeAsSigned(0)));
			Optional<Word> minusOne = Optional.of(new Word(DataConverter.encodeAsSigned(-1)));
			Optional<Word> one = Optional.of(new Word(DataConverter.encodeAsSigned(1)));
			
			assertEquals(-1,DataConverter.decodeAsSigned(this.alu.execute(Instruction.not,zero,empty).getWord()));
			assertEquals(0,DataConverter.decodeAsSigned(this.alu.execute(Instruction.not,minusOne,empty).getWord()));
			assertEquals(-2,DataConverter.decodeAsSigned(this.alu.execute(Instruction.not,one,empty).getWord()));
		}
		
		{//or
			Optional<Word> zero = Optional.of(new Word(DataConverter.encodeAsSigned(0)));
			Optional<Word> one = Optional.of(new Word(DataConverter.encodeAsSigned(1)));
			Optional<Word> four = Optional.of(new Word(DataConverter.encodeAsSigned(4)));
			Optional<Word> sixteen = Optional.of(new Word(DataConverter.encodeAsSigned(16)));
			
			assertEquals(1,DataConverter.decodeAsSigned(this.alu.execute(Instruction.or,zero,one).getWord()));
			assertEquals(5,DataConverter.decodeAsSigned(this.alu.execute(Instruction.or,one,four).getWord()));
			assertEquals(20,DataConverter.decodeAsSigned(this.alu.execute(Instruction.or,sixteen,four).getWord()));
			
		}
		
		{//ori
			Optional<Word> zero = Optional.of(new Word(DataConverter.encodeAsSigned(0)));
			Optional<Word> one = Optional.of(new Word(DataConverter.encodeAsSigned(1)));
			Optional<Word> four = Optional.of(new Word(DataConverter.encodeAsSigned(4)));
			Optional<Word> sixteen = Optional.of(new Word(DataConverter.encodeAsSigned(16)));
			
			assertEquals(1,DataConverter.decodeAsSigned(this.alu.execute(Instruction.ori,zero,one).getWord()));
			assertEquals(5,DataConverter.decodeAsSigned(this.alu.execute(Instruction.ori,one,four).getWord()));
			assertEquals(20,DataConverter.decodeAsSigned(this.alu.execute(Instruction.ori,sixteen,four).getWord()));
		}
		
		{//xor
			Optional<Word> one = Optional.of(new Word(DataConverter.encodeAsSigned(1)));
			Optional<Word> three = Optional.of(new Word(DataConverter.encodeAsSigned(3)));
			Optional<Word> zero = Optional.of(new Word(DataConverter.encodeAsSigned(0)));
			Optional<Word> four = Optional.of(new Word(DataConverter.encodeAsSigned(4)));
			Optional<Word> fourteen = Optional.of(new Word(DataConverter.encodeAsSigned(14)));
			assertEquals(2,DataConverter.decodeAsSigned(this.alu.execute(Instruction.xor,three,one).getWord()));
			assertEquals(3,DataConverter.decodeAsSigned(this.alu.execute(Instruction.xor,three,zero).getWord()));
			assertEquals(10,DataConverter.decodeAsSigned(this.alu.execute(Instruction.xor,four,fourteen).getWord()));
		}
		
		{//xori
			Optional<Word> one = Optional.of(new Word(DataConverter.encodeAsSigned(1)));
			Optional<Word> three = Optional.of(new Word(DataConverter.encodeAsSigned(3)));
			Optional<Word> zero = Optional.of(new Word(DataConverter.encodeAsSigned(0)));
			Optional<Word> four = Optional.of(new Word(DataConverter.encodeAsSigned(4)));
			Optional<Word> fourteen = Optional.of(new Word(DataConverter.encodeAsSigned(14)));
			assertEquals(2,DataConverter.decodeAsSigned(this.alu.execute(Instruction.xori,three,one).getWord()));
			assertEquals(3,DataConverter.decodeAsSigned(this.alu.execute(Instruction.xori,three,zero).getWord()));
			assertEquals(10,DataConverter.decodeAsSigned(this.alu.execute(Instruction.xori,four,fourteen).getWord()));
		}
	}
}
