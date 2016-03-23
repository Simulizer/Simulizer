package simulizer.lowlevel.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observer;
import java.util.Optional;
import java.util.Set;

import simulizer.assembler.representation.Address;
import simulizer.simulation.messages.PipelineHazardMessage;
import simulizer.simulation.messages.PipelineStateMessage;

/**
 * Represents the model for the history of the pipeline, including the waiting and
 * completed instructions at each stage.
 *
 * @author Kelsey McKenna
 *
 */
public class PipelineHistoryModel {
	private static final int MAX_SIZE = 10_000;
	private List<PipelineState> history = new ArrayList<>();
	private Set<Observer> observers = new HashSet<>();
	private PipelineHazardMessage.Hazard currentHazard;

	/**
	 * Represents a single state in the pipeline. This has public fields
	 * for accessing the fetch, decode, and execute addresses, as well as
	 * lists of the addresses before (waiting) and after (complete) the
	 * pipeline. There is also information about whether or not the stage
	 * represents a jump instruction, and whether it has a hazard.
	 *
	 * @author Kelsey McKenna
	 *
	 */
	public class PipelineState {
		public final List<Address> before;
		public final Address fetched;
		public final Address decoded;
		public final Address executed;
		public final List<Address> after;
		public final boolean isJump;
		public final Optional<PipelineHazardMessage.Hazard> hazard;

		PipelineState(List<Address> before, Address fetch, Address decode, Address execute, List<Address> after, boolean isJump) {
			this.before = before;
			this.fetched = fetch;
			this.decoded = decode;
			this.executed = execute;
			this.after = after;
			this.isJump = isJump;

			if (fetch == null || decode == null || execute == null) {
				if (currentHazard == null) this.hazard = Optional.empty();
				else this.hazard = Optional.of(currentHazard);
			} else this.hazard = Optional.empty();
		}
	}

	public List<PipelineState> getHistory() {
		return history;
	}

	public int size() {
		return history.size();
	}

	public void addObserver(Observer observer) {
		observers.add(observer);
	}

	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}

	/**
	 * Processes the pipeline state message and adds it to the history
	 *
	 * @param m
	 *            the pipeline state message
	 */
	public void processPipelineStateMessage(final PipelineStateMessage m) {
		if (size() >= MAX_SIZE) return;

		final Address fetched = m.getFetched();
		final Address decoded = m.getDecoded();
		final Address executed = m.getExecuted();

		List<Address> before = new ArrayList<>(3);
		List<Address> after = new ArrayList<>(3);
		boolean isJump = false;

		if (fetched != null) {
			int currentFetchAddress = fetched.getValue();

			// -- Calculate addresses before the pipeline
			for (int i = 3; i >= 1; --i)
				before.add(new Address(currentFetchAddress + 4 * i));

			// -- Get the addresses after the pipeline
			// It *is* a jump if the current fetch address is not equal to `last` or `last + 4`
			if (history.size() > 0) {
				Address lastFetch = history.get(history.size() - 1).fetched;
				if (lastFetch != null) {
					int lastFetchAddress = lastFetch.getValue();
					isJump = currentFetchAddress != lastFetchAddress && currentFetchAddress != lastFetchAddress + 4;
				}
			}
		}

		// If there has been a jump, clear what's after the pipeline
		// otherwise, add calculate the appropriate things
		if (!isJump) {
			int count = 0;

			// Add as many (up to 3) instructions after the pipeline,
			// and stop looking if you reach a jump instruction
			PipelineState state;
			for (int i = history.size() - 1; i >= 0 && count < 3 && !(state = history.get(i)).isJump; --i) {
				if (state.executed != null) {
					after.add(state.executed);
					++count;
				}
			}
		}

		PipelineState nextState = new PipelineState(before, fetched, decoded, executed, after, isJump);
		history.add(nextState);
		notifyObservers(nextState);
	}

	/**
	 * Processes the given hazard message and attaches it to the next pipeline state message
	 *
	 * @param m
	 *            the pipeline hazard message
	 */
	public void processHazardStateMessage(PipelineHazardMessage m) {
		this.currentHazard = m.getHazard();
	}

	public void clear() {
		history.clear();
		notifyObservers(null);
	}

	/**
	 * Notifies the observers about the most recent pipeline state
	 *
	 * @param nextState
	 *            the pipeline state to send to the observers
	 */
	private void notifyObservers(PipelineState nextState) {
		observers.forEach(t -> t.update(null, nextState));
	}
}
