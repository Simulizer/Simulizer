package simulizer.ui.interfaces;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxtras.scene.control.window.Window;
import simulizer.Simulizer;
import simulizer.ui.WindowManager;
import simulizer.ui.components.MainMenuBar;
import simulizer.ui.layout.GridBounds;
import simulizer.ui.theme.Theme;
import simulizer.utils.UIUtils;

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
	private volatile boolean isExtracted = false;
	private Stage extractedStage = new Stage();
	private StackPane contentPane;
	private MainMenuBar menuBar;
	private EventManager internalEventManager = new EventManager(this), externalEventManager;

	public InternalWindow() {

		UIUtils.assertFXThread(); // needed to create a stage

		// Using caching to smooth movement
		setCache(true);
		setCacheHint(CacheHint.SPEED);

		// TODO figure out why this isn't working (after getContentPane was added, is it now working?)
		getContentPane().setCursor(Cursor.DEFAULT);

		// Sets to default title
		setTitle(WindowEnum.getName(this));

		// Bring to front when clicked
		addEventFilter(MouseEvent.MOUSE_CLICKED, e -> toFront());

		// Update layout on move/resize
		addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> Platform.runLater(this::calculateLayout));

		// Fix internal window to main window
		// @formatter:off
		addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
			// Check whether feature is enabled
			if ((boolean) wm.getSettings().get("internal-window.mouse-borders"))
				// Check mouse cursor is still inside workspace
				if (e.getSceneX() < 0                            ||                              // Left border check
					e.getSceneY() < wm.getMenuBar().getHeight()  ||                              // Top border check
					e.getSceneX() > wm.getWorkspace().getWidth() ||                              // Right border check
					e.getSceneY() > wm.getWorkspace().getHeight() + wm.getMenuBar().getHeight()) // Bottom border check
						e.consume();
		});
		// @formatter:on

		// Pseudo Class
		pseudoClassStateChanged(PseudoClass.getPseudoClass("internal"), true);

		// For open animation
		setScaleX(0);
		setScaleY(0);
		setMinWidth(0);
		setMinHeight(0);
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
	public final void setNormalisedDimentions(double layX, double layY, double layWidth, double layHeight) {
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
		if (this.wm == null) {
			this.wm = wm;

			// Add window icons
			if ((boolean) wm.getSettings().get("internal-window.extractable.enabled"))
				getRightIcons().add(new CustomExtractIcon(this));
			getRightIcons().add(new CustomCloseIcon(this));
		}
	}

	/**
	 * Emphasise a window to draw the user's attention
	 */
	public final void emphasise() {
		emphasise(1.1);
	}

	/**
	 * Performs an animation to get the users attention
	 *
	 * @param sf
	 *            the scale factor to enlarge the window by (1.0 => no scale)
	 */
	protected final void emphasise(double sf) {
		// Ignore if window is just being opened
		if (getScaleX() == 1 && getScaleY() == 1) {
			ScaleTransition sc = new ScaleTransition(Duration.millis(175), this);
			sc.setToX(sf);
			sc.setToY(sf);
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
	public synchronized void setTheme(Theme theme) {
		getStylesheets().clear();
		getStylesheets().add(theme.getStyleSheet("window.css"));
		if (contentPane != null) {
			contentPane.getStylesheets().clear();
			contentPane.getStylesheets().add(theme.getStyleSheet("window.css"));
		}
	}

	/**
	 * Sets the GridBounds (used when creating the window)
	 *
	 * @param grid
	 *            the GridBounds to snap to
	 */
	public final void setGridBounds(GridBounds grid) {
		if (grid != null) {
			// Thanks to: http://stackoverflow.com/questions/10773000/how-to-listen-for-resize-events-in-javafx#answer-25812859
			addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
				private final Timer timer = new Timer(true);
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
							// TODO: this is doing floating point equality!
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

	/**
	 * Sets an internal windows title (use this instead of setTitle so that extracted windows have their title updated)
	 *
	 * @param title
	 *            the title of the internal window
	 */
	protected synchronized void setWindowTitle(String title) {
		setTitle(title);
		if (isExtracted)
			extractedStage.setTitle(title);
	}

	@Override
	public void close() {
		isClosed = true;
		if (isExtracted)
			toggleWindowExtracted();
		super.close();
	}

	/**
	 * @return if the window is closed or not
	 */
	public final boolean isClosed() {
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
	public final void setWorkspaceSize(double width, double height) {
		if (!Double.isNaN(width) && !Double.isNaN(height) && !isExtracted) {
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

	@Override
	public synchronized Pane getContentPane() {
		// Overridden because the content pane is sometimes in an extracted window
		if (contentPane == null)
			return super.getContentPane();
		else
			return contentPane;
	}

	/**
	 * @return if the window is extracted or not
	 */
	public final boolean isExtracted() {
		return isExtracted;
	}

	/**
	 * @return main menu bar if window has one
	 */
	public final MainMenuBar getMenuBar() {
		return menuBar;
	}

	protected final EventManager getEventManager() {
		return internalEventManager;
	}

	/**
	 * Switches between the internal window being inside the workspace and in it's own separate window.
	 */
	public final synchronized void toggleWindowExtracted() {
		if (isExtracted) {
			// Restore to workspace
			// Close the window
			extractedStage.close();
			extractedStage = null;
			menuBar = null;

			// Move all contentPane components back to the Internal Window
			for (Iterator<Node> i = contentPane.getChildren().iterator(); i.hasNext();) {
				Node n = i.next();
				i.remove();
				super.getContentPane().getChildren().add(n);
			}

			// Transfer Events
			externalEventManager.transferTo(internalEventManager);
			externalEventManager = null;

			// Add internal window into the workspace
			wm.getWorkspace().getPane().getChildren().add(this);
			contentPane = null;

			// Resize the internal window into the workspace
			isExtracted = false;
			setWorkspaceSize(wm.getWorkspace().getWidth(), wm.getWorkspace().getHeight());
		} else {
			// Extract to a separate window
			// Remove this internal window pane from workspace
			wm.getWorkspace().getPane().getChildren().remove(this);

			// Move all components to a new StackPane (because JavaFX...)
			contentPane = new StackPane();
			for (Iterator<Node> i = super.getContentPane().getChildren().iterator(); i.hasNext();) {
				Node n = i.next();
				i.remove();
				contentPane.getChildren().add(n);
			}

			// Create a new window to put the content pane in
			extractedStage = new Stage();
			extractedStage.setTitle(getTitle());
			extractedStage.getIcons().add(Simulizer.getIcon());
			extractedStage.setOnCloseRequest(e -> toggleWindowExtracted());
			extractedStage.setWidth(getWidth());
			extractedStage.setHeight(getHeight());

			// Create the scene
			Scene scene;
			if ((boolean) wm.getSettings().get("internal-window.extractable.menu-bar")) {
				GridPane root = new GridPane();
				scene = new Scene(root);

				menuBar = new MainMenuBar(wm);
				GridPane.setHgrow(menuBar, Priority.ALWAYS);
				root.add(menuBar, 0, 0);

				GridPane.setHgrow(contentPane, Priority.ALWAYS);
				GridPane.setVgrow(contentPane, Priority.ALWAYS);
				root.add(contentPane, 0, 1);

			} else {
				scene = new Scene(contentPane);
			}

			// Pseudo Class
			contentPane.pseudoClassStateChanged(PseudoClass.getPseudoClass("external"), true);

			// Transfer Events
			externalEventManager = new EventManager(contentPane);
			internalEventManager.transferTo(externalEventManager);

			// Fix style
			contentPane.getStylesheets().addAll(getStylesheets());
			contentPane.getStyleClass().addAll(getStyleClass());

			// Add the content pane to the window and show
			extractedStage.setScene(scene);
			extractedStage.show();
			isExtracted = true;
		}
	}
}
