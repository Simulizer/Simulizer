package simulizer.simulation.cpu.components;

import java.util.Observable;
import java.util.Stack;

import simulizer.simulation.data.representation.*;

/**
 * representing a stack of word objects for our memory representation no real
 * need for a stack pointer since the built in Java stack object deals with this
 * itself (the sp register can somehow link us to the object of this class
 * 
 * @author Charlie Street
 *
 */
public class MipsStack extends Observable {
	private Stack<Word> stack;

	/**
	 * constructor will simply initialise the stack
	 * 
	 */
	public MipsStack() {
		super();
		this.stack = new Stack<Word>();
	}

	/**
	 * method just pushes a new word to the stack
	 * 
	 * @param word
	 */
	public void push(Word word) {
		this.stack.push(word);
		setChanged();
		notifyObservers();
	}

	/**
	 * this method pops something off of our stack
	 * 
	 * @return
	 */
	public Word pop() {
		setChanged();
		notifyObservers();
		return this.stack.pop();
	}
}
