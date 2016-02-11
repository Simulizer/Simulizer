package simulizer.simulation.cpu.components;

import simulizer.assembler.representation.Register;
import simulizer.simulation.data.representation.Word;

/** this class represents a single general purpose register in the CPU
 * @author Charlie Street */
public class GPRegister{
	private Word word;
	private Register pseudonym;// register name like $sp, $t0 etc

	/** initialising all connections/data associated with the register
	 * @param pseudonym the pseudonym given to this register */
	public GPRegister(Register pseudonym) {
		this.word = new Word();// initialising register contents
		this.pseudonym = pseudonym;
	}

	/** method returns the data currently stored within this register
	 * @return the word of data in the register */
	public Word getData() {
		return this.word;
	}

	/** this method sets new data into the register, to be careful it is synchronised for exclusive access to prevent any possible
	 * errors that may occur due to multi threading and the pipelining
	 * @param word the word to set in the register */
	public synchronized void setData(Word word) {
		this.word = word;
	}

	/** returns the registers pseudonym (might not be useful in the slightest)
	 * @return the registers pseudonym */
	public Register getPseudonym() {
		return this.pseudonym;
	}
}
