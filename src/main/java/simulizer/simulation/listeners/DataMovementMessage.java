package simulizer.simulation.listeners;

import java.util.Optional;

import simulizer.assembler.representation.Statement;
import simulizer.simulation.data.representation.Word;

/**
 * A message to signal the movement of data in the CPU simulation
 * @author Charlie Street
 */
public class DataMovementMessage extends Message {
	private Optional<Word> data;
	private Optional<Statement> instruction;//may be helpful
	
	/**constructor initialises fields
	 * 
	 * @param data the data being moved
	 * @param instruction the instruction being moved
	 */
	public DataMovementMessage(Optional<Word> data, Optional<Statement> instruction) {
		this.data = data;
		this.instruction = instruction;
	}
	
	/**returns data sent in message
	 * 
	 * @return data in message
	 */
	public Optional<Word> getData() {
		return this.data;
	}
	
	/**method returns instruction in message (or empty if not present)
	 * 
	 * @return instruction moved 
	 */
	public Optional<Statement> getInstruction() {
		return this.instruction;
	}
}
