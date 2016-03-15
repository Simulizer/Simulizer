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

public class PipelineHistoryModel {
	private static final int MAX_SIZE = 10_000;
	private List<PipelineState> history = new ArrayList<>();
	private Set<Observer> observers = new HashSet<>();
	private PipelineHazardMessage.Hazard currentHazard;

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

			if (fetch == null || decode == null || execute == null)
				this.hazard = Optional.of(currentHazard);
			else
				this.hazard = Optional.empty();
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

	public void processPipelineStateMessage(PipelineStateMessage m) {
		if (size() >= MAX_SIZE) return;

		int currentFetchAddress = m.getFetched().getValue();

		// -- Calculate addresses before the pipeline
		List<Address> before = new ArrayList<>(3);
		for (int i = 3; i >= 1; --i)
			before.add(new Address(currentFetchAddress + 4 * i));

		// -- Get the addresses after the pipeline
		boolean isJump = false;
		// It *is* a jump if the current fetch address is not equal to `last` or `last + 4`
		if (history.size() > 0) {
			int lastFetchAddress = history.get(history.size() - 1).fetched.getValue();
			isJump = currentFetchAddress != lastFetchAddress && currentFetchAddress != lastFetchAddress + 4;
		}

		List<Address> after = new ArrayList<>(3);
		// If there has been a jump, clear what's after the pipeline
		// otherwise, add calculate the appropriate things
		if (!isJump) {
			int count = 0;

			// Add as many (up to 3) instructions after the pipeline,
			// and stop looking if you reach a jump instruction
			PipelineState state;
			for (int i = history.size() - 1; i >= 0 && count < 3 && !(state = history.get(i)).isJump; --i) {
				if (state.executed != null) {
					after.add(history.get(i).executed);
					++count;
				}
			}
		}

		PipelineState nextState = new PipelineState(before, m.getFetched(), m.getDecoded(), m.getExecuted(), after, isJump);
		history.add(nextState);
		notifyObservers(nextState);
	}

	public void processHazardStateMessage(PipelineHazardMessage m) {
		this.currentHazard = m.getHazard();
	}

	public void clear() {
		history.clear();
		notifyObservers(null);
	}

	private void notifyObservers(PipelineState nextState) {
		observers.forEach(t -> t.update(null, nextState));
	}
}
