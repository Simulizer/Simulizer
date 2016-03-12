package simulizer.simulation.messages;

/**a message for signalling that a pipeline hazard has occured
 *
 * @author Charlie Street
 *
 */
public class PipelineHazardMessage extends Message{

	/**hazard types
	 *
	 * @author Charlie Street
	 *
	 */
	public enum Hazard {
		RAW,
		WAW,
		CONTROL;
	}

	private Hazard hazard;

	/**constructor initialises field
	 *
	 * @param hazard hazard being sent in message
	 */
	public PipelineHazardMessage(Hazard hazard) {
		this.hazard = hazard;
	}

	/**method will get the hazard
	 *
	 * @return the hazard being signalled
	 */
	public Hazard getHazard()
	{
		return this.hazard;
	}
}
