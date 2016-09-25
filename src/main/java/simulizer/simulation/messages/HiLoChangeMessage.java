package simulizer.simulation.messages;

/**class for messages when hi or lo are changed
 * 
 * @author Charlie Street
 *
 */
public class HiLoChangeMessage extends RegisterChangedMessage {
	
	/**register changed field is null
	 * 
	 */
	public HiLoChangeMessage() {
		super(null);
	}
}
