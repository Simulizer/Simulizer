package simulizer.ui.interfaces;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import jfxtras.scene.control.window.CloseIcon;
import jfxtras.scene.control.window.MinimizeIcon;
import jfxtras.scene.control.window.Window;
import simulizer.ui.WindowManager;
import simulizer.ui.layout.GridBounds;
import simulizer.ui.theme.Theme;

public abstract class InternalWindow extends Window implements Observer {
	private double windowWidth, windowHeight;
	private GridBounds grid;
	private WindowManager wm;

	public InternalWindow() {
		setScaleX(0);
		setScaleY(0);

		setCursor(Cursor.DEFAULT);

		// Sets to default title
		setTitle(WindowEnum.toEnum(this).toString());

		// Adds minimise icon
		MinimizeIcon minimize = new MinimizeIcon(this);
		minimize.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
			if (getHeight() > 30)
				setMinHeight(0.0); // Minimising
			else
				setMinHeight(getMinimalHeight()); // Maximising
		});
		getRightIcons().add(minimize);

		// Adds close icon
		CloseIcon close = new CloseIcon(this);
		getRightIcons().add(close);

		// Bring to front when clicked
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
		windowWidth = wm.getPane().getWidth();
		windowHeight = wm.getPane().getHeight();
		// wm.addObserver(this);
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

	public void setGridBounds(GridBounds grid) {
		if (this.grid == null) {
			// Listens for Resize/Move Events
			widthProperty().addListener(resizeEvent);
			heightProperty().addListener(resizeEvent);
			layoutXProperty().addListener(resizeEvent);
			layoutYProperty().addListener(resizeEvent);
		}
		this.grid = grid;
	}

	@Override
	public void update(Observable arg0, Object obj) {
		double[] dim = (double[]) obj;
		if (windowWidth != dim[0]) {
			double ratio = dim[0] / windowWidth;
			setLayoutX(getLayoutX() * ratio);
			setPrefWidth(getWidth() * ratio);
			windowWidth = dim[0];
		}
		if (windowHeight != dim[1]) {
			double ratio = dim[1] / windowHeight;
			setLayoutY(getLayoutY() * ratio);
			setPrefHeight(getHeight() * ratio);
			windowHeight = dim[1];
		}
	}

	/** Snaps InternalWindow to grid when it is resized */
	private ChangeListener<Number> resizeEvent = new ChangeListener<Number>() {
		// Thanks to: http://stackoverflow.com/questions/10773000/how-to-listen-for-resize-events-in-javafx#answer-25812859
		final Timer timer = new Timer();
		TimerTask task = null;
		final long delayTime = 200; // Delay before resize to grid

		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if (task != null)
				task.cancel();
			task = new TimerTask() {
				@Override
				public void run() {
					double[] coords = { getLayoutX(), getLayoutY(), getLayoutX() + getWidth(), getLayoutY() + getHeight() };
					coords = grid.moveToGrid(coords);
					if (coords[2] != getLayoutX() + getWidth()) {
						System.out.println("Changed Width to: " + (coords[2] - coords[0]));
						setPrefWidth(coords[2] - coords[0]);
					}
					if (coords[3] != getLayoutY() + getHeight()) {
						System.out.println("Changed Height to: " + (coords[3] - coords[1]));
						setPrefHeight(coords[3] - coords[1]);
					}
					if (coords[0] != getLayoutX()) {
						System.out.println("Changed X to: " + coords[0]);
						setLayoutX(coords[0]);
					}
					if (coords[1] != getLayoutY()) {
						System.out.println("Changed Y to: " + (coords[1]));
						setLayoutY(coords[1]);
					}
					task.cancel();
				}
			};
			timer.schedule(task, delayTime);
		}

	};

	/** Called when all internal window stuff is done */
	public void ready() {
		ScaleTransition sc = new ScaleTransition(Duration.millis(200), this);
		sc.setToX(1);
		sc.setToY(1);
		sc.play();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof InternalWindow))
			return false;
		return WindowEnum.toEnum((InternalWindow) obj) == WindowEnum.toEnum(this);
	}

}
