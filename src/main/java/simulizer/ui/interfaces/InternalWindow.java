package simulizer.ui.interfaces;

import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import jfxtras.scene.control.window.CloseIcon;
import jfxtras.scene.control.window.Window;
import simulizer.ui.WindowManager;
import simulizer.ui.layout.GridBounds;
import simulizer.ui.theme.Theme;

/**
 * InternalWindow contains the standard methods for being a window in the workspace
 * 
 * @author Michael
 *
 */
public abstract class InternalWindow extends Window {
	private double layX, layY, layWidth, layHeight, windowWidth, windowHeight;
	private WindowManager wm;
	private boolean isClosed = false;

	public InternalWindow() {

		// Using caching to smooth movement
		setCache(true);
		setCacheHint(CacheHint.SPEED);

		// TODO figure out why this isn't working
		setCursor(Cursor.DEFAULT);

		// Sets to default title
		setTitle(WindowEnum.getName(this));

		// Adds close icon
		CloseIcon close = new CloseIcon(this);
		getRightIcons().add(close);

		// Bring to front when clicked
		addEventFilter(MouseEvent.MOUSE_CLICKED, e -> toFront());

		// Update layout on move/resize
		addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> Platform.runLater(this::calculateLayout));

		// Adds a small window border
		setPadding(new Insets(0, 2, 2, 2));

		// For open animation
		setScaleX(0);
		setScaleY(0);
	}

	public void setToDefaultDimensions() {
		setNormalisedDimentions(0.1, 0.1, 0.8, 0.8);
	}

	/**
	 * Sets the normalised dimensions
	 *
	 * @param layX
	 *            the normalised x location within the workspace
	 * @param layY
	 *            the normalised y location within the workspace
	 * @param layWidth
	 *            the normalised width of the InternalWindow
	 * @param layHeight
	 *            the normalised height of the InternalWindow
	 */
	public void setNormalisedDimentions(double layX, double layY, double layWidth, double layHeight) {
		this.layX = layX;
		this.layY = layY;
		this.layWidth = layWidth;
		this.layHeight = layHeight;
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
		if (grid != null) {
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
	}

	/** Called when all internal window stuff is done */
	public void ready() {
		ScaleTransition sc = new ScaleTransition(Duration.millis(250), this);
		sc.setToX(1);
		sc.setToY(1);
		Platform.runLater(sc::playFromStart);
	}

	@Override
	public void close() {
		super.close();
		isClosed = true;
	}

	/**
	 * @return if the window is closed or not
	 */
	public boolean isClosed() {
		return isClosed;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof InternalWindow && WindowEnum.toEnum((InternalWindow) obj) == WindowEnum.toEnum(this);
	}

	/**
	 * Tells the InternalWindow the size of the workspace. The InternalWindow will resize accordingly
	 * 
	 * @param width
	 *            the width of the workspace
	 * @param height
	 *            the height of the workspace
	 */
	public void setWorkspaceSize(double width, double height) {
		if (width != Double.NaN && height != Double.NaN) {
			setLayoutX(layX * width);
			setPrefWidth(layWidth * width);
			setLayoutY(layY * height);
			setPrefHeight(layHeight * height);
			windowWidth = width;
			windowHeight = height;
		}
	}

	/**
	 * Calculates layout ratios
	 */
	private void calculateLayout() {
		if (windowWidth > 0 && windowHeight > 0) {
			layX = getLayoutX() / windowWidth;
			layWidth = getWidth() / windowWidth;
			layY = getLayoutY() / windowHeight;
			layHeight = getHeight() / windowHeight;
		}
	}
}
