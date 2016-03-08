package simulizer.simulation.listeners;

import simulizer.assembler.representation.Address;


/**class represents a message which gives the information on the state
 * of the pipeline at a given tick
 * @author Charlie Street
 *
 */
public class PipelineStateMessage extends Message{

	private Address fetched;
	private Address decoded;
	private Address executed;
	
	/**method will initialise all fields of information for the message
	 * 
	 * @param fetched the address of the instruction just fetched
	 * @param decoded the address of the instruction just decoded
	 * @param executed the address of the instruction just executed
	 */
	public PipelineStateMessage(Address fetched, Address decoded, Address executed) {
		this.fetched = fetched;
		this.decoded = decoded;
		this.executed = executed;
	}
	
	/**get the fetched address
	 * 
	 * @return the fetched address
	 */
	public Address getFetched() {
		return this.fetched;
	}
	
	/**get the decoded address
	 * 
	 * @return the decoded address
	 */
	public Address getDecoded() {
		return this.decoded;
	}
	
	/**get the executed address
	 * 
	 * @return the executed address
	 */
	public Address getExecuted() {
		return this.executed;
	}
	
}
