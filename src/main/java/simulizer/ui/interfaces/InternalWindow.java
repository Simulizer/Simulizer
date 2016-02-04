package simulizer.ui.interfaces;

import jfxtras.labs.scene.control.window.CloseIcon;
import jfxtras.labs.scene.control.window.MinimizeIcon;
import jfxtras.labs.scene.control.window.Window;

public abstract class InternalWindow extends Window {

	public InternalWindow() {
		setTitle(getWindowName());
		getRightIcons().add(new MinimizeIcon(this));

		// TODO: Need to remove from openWindow list when closing
		CloseIcon close = new CloseIcon(this);
		getRightIcons().add(close);

		// TODO: Stop Internal Windows covering MainMenuBar
	}

	/** @return the name of the inner window */
	public abstract String getWindowName();

	public void setBounds(double locX, double locY, double sizeX, double sizeY) {
		setLayoutX(locX);
		setLayoutY(locY);
		setPrefSize(sizeX, sizeY);
	}

	public double[] getBounds() {
		return new double[] { getLayoutX(), getLayoutY(), getBoundsInLocal().getWidth(), getBoundsInLocal().getHeight() };
	}

	public void setTheme(String theme) {
		getStylesheets().add(theme + "/window.css");
	}
}
