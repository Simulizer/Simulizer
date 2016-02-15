package simulizer.ui.interfaces;

import javafx.scene.input.MouseEvent;
import jfxtras.labs.scene.control.window.CloseIcon;
import jfxtras.labs.scene.control.window.MinimizeIcon;
import jfxtras.labs.scene.control.window.Window;
import simulizer.ui.WindowManager;

public abstract class InternalWindow extends Window {
	private WindowManager wm;

	public InternalWindow() {
		// Sets to default title
		setTitle(WindowEnum.toEnum(this).toString());

		MinimizeIcon minimize = new MinimizeIcon(this);

		// TODO: Fix VERY HORRIBLE CODE
		minimize.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
			if (getHeight() > 30) {
				// Minimising
				setMinHeight(0.0);
			} else {
				// Maximising
				setMinHeight(getMinimalHeight());
			}
		});

		getRightIcons().add(minimize);

		CloseIcon close = new CloseIcon(this);
		getRightIcons().add(close);

		// TODO: Stop Internal Windows covering MainMenuBar
	}

	public void setBounds(double locX, double locY, double sizeX, double sizeY) {
		setLayoutX(locX);
		setLayoutY(locY);
		setPrefSize(sizeX, sizeY);
	}

	public double[] getBounds() {
		return new double[] { getLayoutX(), getLayoutY(), getBoundsInLocal().getWidth(), getBoundsInLocal().getHeight() };
	}

	protected double getMinimalHeight() {
		return 0.0;
	}

	protected final WindowManager getWindowManager() {
		return wm;
	}

	public final void setWindowManager(WindowManager wm) {
		this.wm = wm;
	}

	public void setTheme(String theme) {
		getStylesheets().clear();
		getStylesheets().add(theme + "/window.css");
	}

	/** Called when all internal window stuff is done */
	public void ready() {
		// Do nothing
	}
}
