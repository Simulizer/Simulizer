package simulizer.simulation.listeners;

import simulizer.simulation.instructions.AddressMode;

/**this class is for messages specifying instruction types being executed
 * it will hence help specify the datapath being used
 * @author charlie street
 *
 */
public class InstructionTypeMessage extends Message{
	private AddressMode mode;
	
	/**initialises field
	 * 
	 * @param mode addressing mode of instruction, specifies data path
	 */
	public InstructionTypeMessage(AddressMode mode) {
		this.mode = mode;
	}
	
	/**gets the addressing mode used
	 * 
	 * @return address mode
	 */
	public AddressMode getMode() {
		return this.mode;
	}
}
