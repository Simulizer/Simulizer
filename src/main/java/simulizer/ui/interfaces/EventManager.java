package simulizer.ui.interfaces;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.util.Pair;

public final class EventManager {

	// TODO: Replace Object to make code more readable
	private final Node n;
	private Set<Pair<Object, Object>> eventHandlers = new HashSet<Pair<Object, Object>>();
	private Set<Pair<Object, Object>> eventFilters = new HashSet<Pair<Object, Object>>();

	public EventManager(Node n) {
		this.n = n;
	}

	public synchronized <T extends Event> void addEventHandler(EventType<T> mouseDragged, EventHandler<T> eventHandler) {
		eventHandlers.add(new Pair<Object, Object>(mouseDragged, eventHandler));
		n.addEventHandler(mouseDragged, eventHandler);
	}

	public Set<Pair<Object, Object>> getEventHandlers() {
		return Collections.unmodifiableSet(eventHandlers);
	}

	public synchronized void removeEventHandler(EventType<Event> eventType, EventHandler<Event> eventHandler) {
		eventHandlers.removeIf(p -> p.getKey() == eventType && p.getValue() == eventHandler);
		n.removeEventHandler(eventType, eventHandler);
	}

	public synchronized <T extends Event> void addEventFilter(EventType<T> eventType, EventHandler<T> eventFilter) {
		eventFilters.add(new Pair<Object, Object>(eventType, eventFilter));
		n.addEventFilter(eventType, eventFilter);
	}

	public Set<Pair<Object, Object>> getEventFilters() {
		return Collections.unmodifiableSet(eventHandlers);
	}

	public synchronized <T extends Event> void removeEventFilter(EventType<T> eventType, EventHandler<T> eventFilter) {
		eventHandlers.removeIf(p -> p.getKey() == eventType && p.getValue() == eventFilter);
		n.removeEventFilter(eventType, eventFilter);
	}

	@SuppressWarnings("unchecked")
	public synchronized void transferTo(EventManager eventManager) {
		// DUPLICATED CODE
		// Event Handlers
		Iterator<Pair<Object, Object>> h = eventHandlers.iterator();
		while (h.hasNext()) {
			Pair<Object, Object> pair = h.next();
			removeEventHandler((EventType<Event>) pair.getKey(), (EventHandler<Event>) pair.getValue());
			eventManager.addEventHandler((EventType<Event>) pair.getKey(), (EventHandler<Event>) pair.getValue());
		}
		
		// Event Filter
		Iterator<Pair<Object, Object>> f = eventFilters.iterator();
		while (f.hasNext()) {
			Pair<Object, Object> pair = f.next();
			removeEventFilter((EventType<Event>) pair.getKey(), (EventHandler<Event>) pair.getValue());
			eventManager.addEventFilter((EventType<Event>) pair.getKey(), (EventHandler<Event>) pair.getValue());
		}
	}


}
