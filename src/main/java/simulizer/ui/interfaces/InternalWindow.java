package simulizer.ui.interfaces;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import jfxtras.labs.scene.control.window.CloseIcon;
import jfxtras.labs.scene.control.window.MinimizeIcon;
import jfxtras.labs.scene.control.window.Window;
import simulizer.ui.WindowManager;
import simulizer.ui.theme.Theme;

public abstract class InternalWindow extends Window {
	private WindowManager wm;

	public InternalWindow() {
		setScaleX(0);
		setScaleY(0);

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

		onMouseClickedProperty().addListener((e) -> toFront());

		// Adds a small window border
		setPadding(new Insets(0, 2, 2, 2));
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

	public final void emphasise() {
		// Ignore if window is just being opened
		if (getScaleX() == 1 && getScaleY() == 1) {
			ScaleTransition sc = new ScaleTransition(Duration.millis(175), this);
			sc.setToX(1.15);
			sc.setToY(1.15);
			sc.setCycleCount(2);
			sc.setAutoReverse(true);
			getStyleClass().add("highlighting");
			sc.setOnFinished((e) -> getStyleClass().remove("highlighting"));
			sc.play();
			toFront();
		}
	}

	public void setTheme(Theme theme) {
		getStylesheets().clear();
		getStylesheets().add(theme.getStyleSheet("window.css"));
	}

	/** Called when all internal window stuff is done */
	public void ready() {
		ScaleTransition sc = new ScaleTransition(Duration.millis(200), this);
		sc.setToX(1);
		sc.setToY(1);
		sc.play();
	}

}
