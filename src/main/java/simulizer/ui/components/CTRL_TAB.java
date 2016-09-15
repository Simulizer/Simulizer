package simulizer.ui.components;

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
			InternalWindow f = null;
			boolean getNext = false;
			for (InternalWindow w : workspace.getAllWindows()) {
				if (!w.isExtracted()) {
					// Save first window
					if (f == null)
						f = w;

					// If previous window was focused, request focus
					if (getNext) {
						setPrimary(w);
						getNext = false;
						break;
					}

					// If focused, get the next window
					if ((selectedWindow == null && w.hasFocus()) || w.equals(selectedWindow))
						getNext = true;
				}
			}
			// If getNext is still true, then the last window was focused
			if (getNext)
				setPrimary(f);

			event.consume();
		} else if (!event.isControlDown()) {
			selectedWindow = null;
		}
	}

	private void setPrimary(InternalWindow w) {
		w.toFront();
		w.requestFocus();
		w.emphasise();
		selectedWindow = w;
	}
}
