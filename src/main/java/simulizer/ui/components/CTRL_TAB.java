package simulizer.ui.components;

import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import simulizer.ui.interfaces.InternalWindow;

// Replace with a better name
public class CTRL_TAB implements EventHandler<KeyEvent> {

	private final Workspace workspace;
	private InternalWindow selectedWindow = null;

	public CTRL_TAB(Workspace workspace) {
		this.workspace = workspace;
	}

	@Override
	public void handle(KeyEvent event) {
		if (event.getEventType() == KeyEvent.KEY_PRESSED && event.isControlDown() && event.getCode() == KeyCode.TAB) {
			List<InternalWindow> allWindows = workspace.getAllWindows();

			InternalWindow previous = null; // only used for CTRL + SHIFT + TAB
			boolean getNext = false; // only used for CTRL + TAB
			for (InternalWindow w : allWindows) {
				if (!w.isExtracted()) {
					// If previous window was focused, we are the next window
					if (getNext) {
						setPrimary(w);
						getNext = false;
						break;
					}

					// If this window is focused
					if ((selectedWindow == null && w.hasFocus()) || w.equals(selectedWindow)) {
						// Are we cycling backwards?
						if (event.isShiftDown()) {
							// Focus the previous window
							if (previous != null) {
								setPrimary(previous);
								break;
							} else {
								// We need the last window
								setPrimary(allWindows.get(allWindows.size() - 1));
								break;
							}

						} else
							// Get the next window
							getNext = true;
					}
					// Store this window as the previous window
					previous = w;
				}
			}
			// If getNext is still true, then the last window was focused
			if (getNext)
				setPrimary(workspace.getAllWindows().get(0));

			event.consume();

		} else if (!event.isControlDown()) {
			selectedWindow = null;
		}
	}

	private void setPrimary(InternalWindow w) {
		w.toFront();
		w.requestFocus();
		selectedWindow = w;
	}
}
