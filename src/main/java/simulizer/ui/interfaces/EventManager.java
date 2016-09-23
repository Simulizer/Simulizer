package simulizer.ui.interfaces;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;

public final class EventManager {
	private final Node n;
	private Set<PropertyEvent<?>> propertyEvents = new HashSet<>();;
	private Set<TypedEvent<?>> typedEvents = new HashSet<>();

	public EventManager(Node n) {
		this.n = n;
	}

	public synchronized <T> void addPropertyListener(Property<T> property, ChangeListener<? super T> listener) {
		addPropertyListener(property.getName(), listener);
	}

	public synchronized <T> void removePropertyListener(Property<T> property, ChangeListener<? super T> listener) {
		removePropertyListener(property.getName(), listener);
	}
	
	public synchronized <T> void addPropertyListener(ReadOnlyProperty<T> property, ChangeListener<? super T> listener) {
		addPropertyListener(property.getName(), listener);
	}

	public synchronized <T> void removePropertyListener(ReadOnlyProperty<T> property, ChangeListener<? super T> listener) {
		removePropertyListener(property.getName(), listener);
	}

	/**
	 * Adds a Property listener like widthProperty()
	 * 
	 * @param property
	 *            Use widthProperty().getName() or whatever property you are using
	 * @param listener
	 *            The listener for that property
	 */
	private synchronized <T> void addPropertyListener(String property, ChangeListener<? super T> listener) {
		propertyEvents.add(new PropertyEvent<>(property, listener));
		invokeListener("addListener", property, listener);
	}

	/**
	 * Removes a Property listener like widthProperty()
	 * 
	 * @param property
	 *            Use widthProperty().getName() or whatever property you are using
	 * @param listener
	 *            The listener for that property
	 */
	private synchronized <T> void removePropertyListener(String property, ChangeListener<? super T> listener) {
		propertyEvents.removeIf(p -> p.property == property && p.listener == listener);
		invokeListener("removeListener", property, listener);
	}

	/**
	 * Invokes n.property().action(listener);
	 * 
	 * @param action
	 *            the action to perform (either addListener or removeListener)
	 * @param property
	 *            the property to perform the action on (i.e. widthProperty) (use widthProperty().getName())
	 * @param listener
	 *            the listener to add or remove
	 */
	private <T> void invokeListener(String action, String property, ChangeListener<? super T> listener) {
		try {
			Method propertyMethod = n.getClass().getMethod(property + "Property", (Class[]) null);
			Object propertyObj = propertyMethod.invoke(n);
			Method addMethod = propertyObj.getClass().getMethod(action, ChangeListener.class);
			addMethod.setAccessible(true);
			addMethod.invoke(propertyObj, listener);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized <T extends Event> void addEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
		addTypedEvent(false, eventType, eventHandler);
	}

	public synchronized <T extends Event> void removeEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
		delTypedEvent(false, eventType, eventHandler);
	}

	public synchronized <T extends Event> void addEventFilter(EventType<T> eventType, EventHandler<? super T> eventFilter) {
		addTypedEvent(true, eventType, eventFilter);
	}

	public synchronized <T extends Event> void removeEventFilter(EventType<T> eventType, EventHandler<? super T> eventFilter) {
		delTypedEvent(true, eventType, eventFilter);
	}

	private <T extends Event> void addTypedEvent(boolean filter, EventType<T> type, EventHandler<? super T> handle) {
		typedEvents.add(new TypedEvent<>(filter, type, handle));
		if (filter)
			n.addEventFilter(type, handle);
		else
			n.addEventHandler(type, handle);
	}

	private <T extends Event> void delTypedEvent(boolean filter, EventType<T> type, EventHandler<? super T> handle) {
		typedEvents.removeIf(g -> g.filter == filter && g.type == type && g.handle == handle);
		if (filter)
			n.removeEventFilter(type, handle);
		else
			n.removeEventHandler(type, handle);
	}

	@SuppressWarnings("unchecked")
	public synchronized <T extends Event, P> void transferTo(EventManager eventManager) {
		// Transfer typedEvents
		Iterator<TypedEvent<?>> typedIterator = typedEvents.iterator();
		while (typedIterator.hasNext()) {
			TypedEvent<T> event = (TypedEvent<T>) typedIterator.next();
			if (event.filter)
				n.removeEventFilter(event.type, event.handle);
			else
				n.removeEventHandler(event.type, event.handle);
			typedIterator.remove();
			eventManager.addTypedEvent(event.filter, event.type, event.handle);
		}

		// Transfer propertyEvents
		Iterator<PropertyEvent<?>> propertyIterator = propertyEvents.iterator();
		while (propertyIterator.hasNext()) {
			PropertyEvent<P> event = (PropertyEvent<P>) propertyIterator.next();
			invokeListener("removeListener", event.property, event.listener);
			propertyIterator.remove();
			eventManager.addPropertyListener(event.property, event.listener);
		}
	}

	private class TypedEvent<T extends Event> {
		public final boolean filter;
		public final EventType<T> type;
		public final EventHandler<? super T> handle;

		public TypedEvent(boolean filter, EventType<T> type, EventHandler<? super T> handle) {
			this.filter = filter;
			this.type = type;
			this.handle = handle;
		}
	}

	private class PropertyEvent<T> {
		public final String property;
		public final ChangeListener<? super T> listener;

		public PropertyEvent(String property, ChangeListener<? super T> listener) {
			this.property = property;
			this.listener = listener;
		}
	}
}
