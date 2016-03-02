package simulizer.simulation.listeners;

import simulizer.assembler.representation.Register;

/**listener for determining if a register has been changed
 * 
 * @author Charlie Street
 *
 */
public class RegisterChangedMessage extends Message {

	public Register registerChanged;
	
	/**initialises field
	 * 
	 * @param register the register that has been changed
	 */
	public RegisterChangedMessage(Register register)
	{
		this.registerChanged = register;
	}
}
