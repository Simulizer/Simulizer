package simulizer.ui.interfaces;

import javafx.scene.input.MouseEvent;
import jfxtras.labs.scene.control.window.CloseIcon;
import jfxtras.labs.scene.control.window.MinimizeIcon;
import jfxtras.labs.scene.control.window.Window;
import simulizer.ui.theme.Theme;

public abstract class InternalWindow extends Window {

	public InternalWindow() {
		// Sets to default title
		setTitle(WindowEnum.toEnum(this).toString());

		// Adds minimise icon
		MinimizeIcon minimize = new MinimizeIcon(this);
		minimize.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
			if (getHeight() > 30) setMinHeight(0.0); // Minimising
			else setMinHeight(getMinimalHeight()); // Maximising
		});
		getRightIcons().add(minimize);

		// Adds close icon
		CloseIcon close = new CloseIcon(this);
		getRightIcons().add(close);

		// Stops window covering MenuBar
		layoutYProperty().addListener((observableValue, oldY, newY) -> {
			if (newY.doubleValue() <= 25) {
				setLayoutY(25);
			}
		});
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

	public void setTheme(Theme theme) {
		getStylesheets().clear();
		getStylesheets().add(theme.getStyleSheet("window.css"));
	}
}
