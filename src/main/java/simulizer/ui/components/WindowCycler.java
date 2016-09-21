package simulizer.ui.components;

import java.util.LinkedList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import simulizer.ui.interfaces.InternalWindow;

// I.e. CTRL + TAB
public class WindowCycler implements EventHandler<KeyEvent> {
	private final Workspace workspace;
	private int focusedIndex = 0;
	private boolean cycling = false;

	public WindowCycler(Workspace workspace) {
		this.workspace = workspace;
	}

	@Override
	public synchronized void handle(KeyEvent event) {
		if (event.getEventType() == KeyEvent.KEY_PRESSED && event.isControlDown() && event.getCode() == KeyCode.TAB) {
			List<InternalWindow> openWindows = workspace.getAllWindows();
			// Ignore event if there are no open windows
			if (!openWindows.isEmpty()) {
				InternalWindow selectedWindow = null;
				int startIndex = focusedIndex;
				cycling = true;

				// Skip over extracted windows
				do {
					// Increment or decrement based on holding shift
					if (event.isShiftDown())
						focusedIndex--;
					else
						focusedIndex++;

					// Wrap index
					if (focusedIndex < 0)
						focusedIndex = openWindows.size() - 1;
					else if (focusedIndex >= openWindows.size())
						focusedIndex = 0;

					selectedWindow = openWindows.get(focusedIndex);
				} while (focusedIndex != startIndex && selectedWindow.isExtracted());

				selectedWindow.toFront();
				selectedWindow.requestFocus();

			}
			event.consume();
		} else if (!event.isControlDown()) {
			cycling = false;
		}
	}

	synchronized void removedIndex(int index) {
		if (index <= focusedIndex)
			focusedIndex--;
	}

	synchronized void setWindowFocused(LinkedList<InternalWindow> openWindows, InternalWindow window) {
		if (!cycling && !openWindows.isEmpty()) {
			InternalWindow prevHead = openWindows.get(focusedIndex);

			// Window is already focused
			if (prevHead.equals(window))
				return;
			
			openWindows.remove(window);
			openWindows.add(openWindows.indexOf(prevHead), window);
			focusedIndex = openWindows.indexOf(window);
		}
	}
}
