package simulizer.simulation.listeners;

import java.util.Iterator;
import java.util.List;

/**
 * this class deals with registering of listeners
 * as well as deciding how it should be processed
 * 
 * @author Charlie Street
 *
 */
public class ListenerController {

	private List<SimulationListener> listeners;

	/**
	 * constructor initialises fields
	 * 
	 * @param listeners
	 *            the listeners recognised by the cpu
	 */
	public ListenerController(List<SimulationListener> listeners) {
		this.listeners = listeners;
	}

	/**
	 * Register a listener to receive messages
	 * 
	 * @param l
	 *            the listener to send messages to
	 */
	public void registerListener(SimulationListener l) {
		synchronized (listeners) {
			this.listeners.add(l);
		}
	}

	/**
	 * Unregisters a listener from the list
	 * 
	 * @param l
	 *            the listener to be removed
	 */
	public void unregisterListener(SimulationListener l) {
		synchronized (listeners) {
			this.listeners.remove(l);
		}
	}

	/**
	 * send a message to all of the registered listeners
	 * 
	 * @param m
	 *            the message to send
	 */
	public void sendMessage(Message m) {
		synchronized (listeners) {
			Iterator<SimulationListener> iterator = listeners.iterator();
			while (iterator.hasNext()) {
				SimulationListener l = iterator.next();
				l.processMessage(m);

				if (m instanceof AnnotationMessage) {
					l.processAnnotationMessage((AnnotationMessage) m);
				} else if (m instanceof DataMovementMessage) {
					l.processDataMovementMessage((DataMovementMessage) m);
				} else if (m instanceof ExecuteStatementMessage) {
					l.processExecuteStatementMessage((ExecuteStatementMessage) m);
				} else if (m instanceof InstructionTypeMessage) {
					l.processInstructionTypeMessage((InstructionTypeMessage) m);
				} else if (m instanceof PipelineHazardMessage) {
					l.processPipelineHazardMessage((PipelineHazardMessage) m);
				} else if (m instanceof PipelineStateMessage) {
					l.processPipelineStateMessage((PipelineStateMessage) m);
				} else if (m instanceof ProblemMessage) {
					l.processProblemMessage((ProblemMessage) m);
				} else if (m instanceof RegisterChangedMessage) {
					l.processRegisterChangedMessage((RegisterChangedMessage) m);
				} else if (m instanceof SimulationMessage) {
					l.processSimulationMessage((SimulationMessage) m);
				} else if (m instanceof StageEnterMessage) {
					l.processStageEnterMessage((StageEnterMessage) m);
				}
			}
		}
	}
}
