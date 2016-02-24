package simulizer.ui.interfaces;

import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.ScaleTransition;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import jfxtras.scene.control.window.CloseIcon;
import jfxtras.scene.control.window.MinimizeIcon;
import jfxtras.scene.control.window.Window;
import simulizer.ui.WindowManager;
import simulizer.ui.layout.GridBounds;
import simulizer.ui.theme.Theme;

public abstract class InternalWindow extends Window {
	private double windowWidth = -1, windowHeight = -1;
	private GridBounds grid;
	private WindowManager wm;

	public InternalWindow() {
		setScaleX(0);
		setScaleY(0);

		// Using caching to smooth movement
		setCache(true);
		setCacheHint(CacheHint.SPEED);

		setCursor(Cursor.DEFAULT);

		// Using caching to smooth window movement
		setCache(true);
		setCacheHint(CacheHint.SPEED);

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

	/**
	 * Forcefully puts a window in a location (will override any MainWindow resize)
	 * 
	 * @param x
	 *            the x location within the workspace
	 * @param y
	 *            the y location within the workspace
	 * @param width
	 *            the width of the InternalWindow
	 * @param height
	 *            the height of the InternalWindow
	 */
	public void setBoundsWithoutResize(double x, double y, double width, double height) {
		setLayoutX(x);
		setLayoutY(y);
		setPrefSize(width, height);
		setWidth(width);
		setHeight(height);
	}

	/**
	 * This method should be overridden if a minimum height is set
	 * 
	 * @return the minimum height when not minimised
	 */
	protected double getMinimalHeight() {
		return 0.0;
	}

	/**
	 * An easy way for the InternalWindow to get access to the WindowManager
	 * 
	 * @return the WindowManager
	 */
	protected final WindowManager getWindowManager() {
		return wm;
	}

	/**
	 * Sets the WindowManager (used when creating the Window)
	 * 
	 * @param wm
	 *            the WindowManager
	 */
	public final void setWindowManager(WindowManager wm) {
		this.wm = wm;
		// TODO: windowWidth = wm.getPane().getWidth();
		// TODO: windowHeight = wm.getPane().getHeight();
		// wm.addObserver(this);
	}

	/**
	 * Performs an animation to get the users attention
	 */
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

	/**
	 * Sets the theme to use
	 * 
	 * @param theme
	 *            the theme to use
	 */
	public void setTheme(Theme theme) {
		getStylesheets().clear();
		getStylesheets().add(theme.getStyleSheet("window.css"));
	}

	/**
	 * Sets the GridBounds (used when creating the window)
	 * 
	 * @param grid
	 *            the GridBounds to snap to
	 */
	public void setGridBounds(GridBounds grid) {
		if (this.grid == null) {
			// Thanks to: http://stackoverflow.com/questions/10773000/how-to-listen-for-resize-events-in-javafx#answer-25812859
			addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
				private final Timer timer = new Timer();
				private TimerTask task = null;
				private final long delayTime = grid.getTimeout(); // Delay before resize to grid
				private double width = getWidth(), height = getHeight();
				private boolean resize = false;

				@Override
				public void handle(MouseEvent event) {
					resize = !(getWidth() == width && getHeight() == height);
					if (task != null)
						task.cancel();
					task = new TimerTask() {
						@Override
						public void run() {
							double[] coords = { getLayoutX(), getLayoutY(), getLayoutX() + getWidth(), getLayoutY() + getHeight() };
							coords = grid.moveToGrid(coords, resize);
							if (coords[2] != getLayoutX() + getWidth())
								setPrefWidth(coords[2] - coords[0]);
							if (coords[3] != getLayoutY() + getHeight())
								setPrefHeight(coords[3] - coords[1]);
							if (coords[0] != getLayoutX())
								setLayoutX(coords[0]);
							if (coords[1] != getLayoutY())
								setLayoutY(coords[1]);
							task.cancel();
						}
					};
					timer.schedule(task, delayTime);

					// Set the width & height
					width = getWidth();
					height = getHeight();
				}

			});
		}
		this.grid = grid;
	}

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

	public void setWorkspaceSize(double width, double height) {
		// Resize InternalWindow to new dimensions
		if (windowWidth != width) {
			double ratio = width / windowWidth;
			setLayoutX(getLayoutX() * ratio);
			setPrefWidth(getWidth() * ratio);
			windowWidth = width;
		}
		if (windowHeight != height) {
			double ratio = height / windowHeight;
			setLayoutY(getLayoutY() * ratio);
			setPrefHeight(getHeight() * ratio);
			windowHeight = height;
		}
	}

}
