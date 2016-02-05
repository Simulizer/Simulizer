package simulizer.simulation.cpu.components;

/**
 * this class is necessary for communications to all the register and choosing
 * one in particular
 * 
 * @author Charlie Street
 *
 */
public class RegisterBus extends Bus {
	private int registerIndex;

	/**
	 * will only call the super constructor and initialise the register index to
	 * -1 (i.e can't be used)
	 */
	public RegisterBus() {
		super();
		this.registerIndex = -1;
	}

	/**
	 * returns the target register
	 * 
	 * @return the target register
	 */
	public int getRegisterIndex() {
		return this.registerIndex;
	}

	/**
	 * sets the register index to a new value
	 * 
	 * @param index
	 *            the new register index
	 */
	public synchronized void setRegisterIndex(int index) {
		this.registerIndex = index;
	}
}
