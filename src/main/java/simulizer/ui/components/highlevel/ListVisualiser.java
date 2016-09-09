package simulizer.ui.components.highlevel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import simulizer.highlevel.models.ListModel;
import simulizer.highlevel.models.ListModel.EmphasiseAction;
import simulizer.highlevel.models.ListModel.HighlightAction;
import simulizer.highlevel.models.ListModel.ListAction;
import simulizer.highlevel.models.ListModel.MarkerAction;
import simulizer.highlevel.models.ListModel.SwapAction;
import simulizer.highlevel.models.ModelAction;
import simulizer.ui.windows.HighLevelVisualisation;

/**
 * Performs the computations in order to visualise a list with
 * an arbitrary number of (long) elements. The list is displayed
 * starting with the first element on the left and the last element
 * on the right.
 *
 * There are a number of animation methods available, such as swapping
 * and emphasis/highlighting, which can be useful for search and sorting
 * algorithms.
 *
 * @author Kelsey McKenna
 *
 */
public class ListVisualiser extends DataStructureVisualiser { //TODO: fix synchronization of this class (currently synchronizing on non-final fields)
	private Canvas canvas = new Canvas();
	private long[] list;
	private ListModel model;

	// -- Animation parameters
	// |-- Swaps
	private boolean swapping = false;

	private int animatedLeftIndex;
	private String animatedLeftLabel;
	// Ratios of the width and height
	private DoubleProperty animatedLeftX = new SimpleDoubleProperty();
	private DoubleProperty animatedLeftY = new SimpleDoubleProperty();

	private int animatedRightIndex;
	private String animatedRightLabel;
	// Ratios of the width and height
	private DoubleProperty animatedRightX = new SimpleDoubleProperty();
	private DoubleProperty animatedRightY = new SimpleDoubleProperty();

	// |-- Emphasis
	private boolean emphasising = false;
	private int emphasiseIndex;
	private DoubleProperty emphasiseProgress = new SimpleDoubleProperty();

	// |-- Markers
	private Map<Integer, String> markers = new HashMap<>();
	private Set<Integer> highlightedMarkers = new HashSet<>();

	// Dimensions used for calculations
	private final static double XPAD = 10;
	private final static double YPAD = 10;
	private double rectLength;
	private double y0;
	private double x0;
	private double w;
	private double h;

	public ListVisualiser(ListModel model, HighLevelVisualisation vis) {
		super(model, vis);
		this.model = model;
		list = model.getList();
		getChildren().add(canvas);

		canvas.widthProperty().bind(super.widthProperty());
		canvas.widthProperty().addListener(e -> Platform.runLater(this::repaint));
		canvas.heightProperty().bind(super.heightProperty());
		canvas.heightProperty().addListener(e -> Platform.runLater(this::repaint));

		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(2);
		gc.setStroke(Color.BLACK);
		gc.setTextBaseline(VPos.CENTER);
		gc.setTextAlign(TextAlignment.CENTER);
	}

	@Override
	public void repaint() {
		if (model.size() == 0) return;

		GraphicsContext gc = canvas.getGraphicsContext2D();
		this.w = canvas.getWidth();
		this.h = canvas.getHeight();

		gc.clearRect(0, 0, w, h);

		calculateDimensions(gc);

		drawMarkers(gc);
		drawList(gc);
		if (swapping) {
			drawTextBox(gc, animatedLeftX.doubleValue() * w, animatedLeftY.doubleValue() * h, animatedLeftLabel);
			drawTextBox(gc, animatedRightX.doubleValue() * w, animatedRightY.doubleValue() * h, animatedRightLabel);
		}
	}

	private final Font markerFont = new Font("Arial", 25);

	/**
	 * Draws the any existing markers over the corresponding elements in the list
	 *
	 * @param gc
	 *            the graphics context for the canvas being drawn onto
	 */
	private void drawMarkers(GraphicsContext gc) {
		gc.setFont(markerFont);
		gc.setTextBaseline(VPos.BOTTOM);

		synchronized (markers) {
			for (Map.Entry<Integer, String> entry : markers.entrySet()) {
				gc.setFill(highlightedMarkers.contains(entry.getKey()) ? Color.RED : Color.BLACK);
				double x = getX(entry.getKey());
				gc.beginPath();
				gc.fillText(entry.getValue(), x + rectLength / 2, y0 - 7, rectLength);
				gc.closePath();
			}
		}
	}

	/**
	 * Draws the elements of the list in squares horizontally, with the first element
	 * on the left and the last element on the right.
	 *
	 * @param gc
	 *            the graphics context for the canvas being drawn onto
	 */
	private void drawList(GraphicsContext gc) {
		gc.setTextBaseline(VPos.CENTER);
		synchronized (list) {
			for (int i = 0; i < list.length; ++i) {
				if (swapping && (i == animatedLeftIndex || i == animatedRightIndex)) continue;
				else if (emphasising && i == emphasiseIndex) {
					Color blend = Color.SKYBLUE.interpolate(Color.RED, emphasiseProgress.doubleValue());
					drawTextBox(gc, i, list[i] + "", blend);
				} else drawTextBox(gc, i, list[i] + "");
			}
		}
	}

	/**
	 * Helper method for drawing a text box.
	 *
	 * @param gc
	 *            the graphics context for the canvas being drawn onto
	 * @param i
	 *            the index of the item in the list which is being drawn
	 * @param text
	 *            the text to put inside the text box
	 */
	private void drawTextBox(GraphicsContext gc, int i, String text) {
		drawTextBox(gc, getX(i), y0, text);
	}

	/**
	 * Helper method for drawing a text box.
	 *
	 * @param gc
	 *            the graphics context for the canvas being drawn onto
	 * @param i
	 *            the index of the item in the list which is being drawn
	 * @param text
	 *            the text to put inside the text box
	 * @param bg
	 *            the background colour for the text box
	 */
	private void drawTextBox(GraphicsContext gc, int i, String text, Color bg) {
		drawTextBox(gc, getX(i), y0, text, bg);
	}

	/**
	 * Helper method for drawing a text box
	 *
	 * @param gc
	 *            the graphics context for the canvas being drawn onto
	 * @param x
	 *            the x coordinate of the top-left of the text box
	 * @param y
	 *            the y coordinate of the top-left of the text box
	 * @param text
	 *            the text to put inside the text box
	 */
	private void drawTextBox(GraphicsContext gc, double x, double y, String text) {
		drawTextBox(gc, x, y, text, Color.SKYBLUE);
	}

	private final Font itemFont = new Font("Arial", 55);

	/**
	 * Helper method for drawing a text box
	 *
	 * @param gc
	 *            the graphics context for the canvas being drawn onto
	 * @param x
	 *            the x coordinate of the top-left of the text box
	 * @param y
	 *            the y coordinate of the top-left of the text box
	 * @param text
	 *            the text to put inside the text box
	 * @param bg
	 *            the background colour for the text box
	 */
	private void drawTextBox(GraphicsContext gc, double x, double y, String text, Color bg) {
		drawBorderedRectangle(gc, bg, x, y, rectLength, rectLength);

		gc.setFont(itemFont);
		gc.setFill(Color.BLACK);
		gc.beginPath();
		gc.fillText(text, x + rectLength / 2, y + rectLength / 2, rectLength);
		gc.closePath();
	}

	/**
	 * Calculates the x coordinate of the top-left of the rectIndex-th list item to draw.
	 *
	 * @param rectIndex
	 *            the index of the list item to draw
	 * @return the x coordinate of the top-left of the rectIndex-th list item to draw.
	 */
	private double getX(int rectIndex) {
		return x0 + rectIndex * rectLength;
	}

	/**
	 * Calculates the dimensions required for further calculations based on the width and height
	 * of the viewport.
	 *
	 * @param gc
	 *            the graphics context for the canvas being drawn onto
	 */
	private void calculateDimensions(GraphicsContext gc) {
		double rectCalc = w - 2 * XPAD;

		rectLength = rectCalc < h - 2 * YPAD ? rectCalc : h - 2 * YPAD;
		this.rectLength = rectCalc / model.size();

		this.x0 = (w - model.size() * rectLength) / 2;
		this.y0 = h / 2 - rectLength / 2;
	}

	/**
	 * Helper method for drawing a bordered rectangle
	 *
	 * @param gc
	 *            the graphics context for the canvas being drawn onto
	 * @param fill
	 *            the main color for the rectangle
	 * @param x
	 *            the x coordinate of the top-left of the rectangle
	 * @param y
	 *            the y coordinate of the top-left of the rectangle
	 * @param w
	 *            the width of the rectangle
	 * @param h
	 *            the height of the rectangle
	 */
	private void drawBorderedRectangle(GraphicsContext gc, Color fill, double x, double y, double w, double h) {
		gc.setFill(fill);
		gc.beginPath();
		gc.fillRect(x, y, w, h);
		gc.strokeRect(x, y, w, h);
		gc.closePath();
	}

	@Override
	public String getName() {
		return "List";
	}

	@Override
	@SuppressWarnings("unchecked")
	public void processChange(ModelAction<?> action) {
		Timeline timeline = null;

		if (action instanceof SwapAction) {
			// Swap animation
			SwapAction swap = (SwapAction) action;

			swapping = true;

			animatedLeftIndex = swap.a;
			animatedRightIndex = swap.b;

			// This is correct
			synchronized (list) {
				animatedLeftLabel = "" + list[animatedLeftIndex];
				animatedRightLabel = "" + list[animatedRightIndex];
			}

			double startXLeft = getX(animatedLeftIndex) / w;
			double startXRight = getX(animatedRightIndex) / w;

			double startY = y0 / h;
			double upY = (y0 - rectLength / 2 - 10) / h;
			double downY = (y0 + rectLength / 2 + 10) / h;

			// @formatter:off
			timeline = new Timeline(
				new KeyFrame(Duration.seconds(0),
					new KeyValue(animatedLeftX, startXLeft),
					new KeyValue(animatedLeftY, startY),
					new KeyValue(animatedRightX, startXRight),
					new KeyValue(animatedRightY, startY)
				),
				new KeyFrame(Duration.seconds(0.5),
					new KeyValue(animatedLeftX, startXLeft),
					new KeyValue(animatedLeftY, upY),
					new KeyValue(animatedRightX, startXRight),
					new KeyValue(animatedRightY, downY)
				),
				new KeyFrame(Duration.seconds(0.8),
					new KeyValue(animatedLeftX, startXRight),
					new KeyValue(animatedLeftY, upY),
					new KeyValue(animatedRightX, startXLeft),
					new KeyValue(animatedRightY, downY)
				),
				new KeyFrame(Duration.seconds(1.3),
					e -> swapping = false,
					new KeyValue(animatedLeftX, startXRight),
					new KeyValue(animatedLeftY, startY),
					new KeyValue(animatedRightX, startXLeft),
					new KeyValue(animatedRightY, startY)
				)
			);
			// @formatter:on

		} else if (action instanceof MarkerAction) {
			// Marker action
			MarkerAction marker = (MarkerAction) action;
			synchronized (markers) {
				if (marker.index.isPresent()) {
					int index = marker.index.get();
					if (marker.name.isPresent()) {
						// We need to add the marker
						String name = marker.name.get();
						String existing = markers.get(index);
						if (existing != null) markers.put(index, existing + " " + name);
						else markers.put(index, name);
					} else {
						// We need to clear the marker
						markers.remove(index);
						highlightedMarkers.remove(index);
					}
				} else {
					markers.clear();
					highlightedMarkers.clear();
				}
			}
		} else if (action instanceof EmphasiseAction) {
			// Emphasise an element
			EmphasiseAction emphasise = (EmphasiseAction) action;
			this.emphasiseIndex = emphasise.index;
			emphasising = true;

			// @formatter:off
			timeline = new Timeline(
				new KeyFrame(Duration.seconds(0),
					new KeyValue(emphasiseProgress, 0.0)
				),
				new KeyFrame(Duration.seconds(0.5),
					new KeyValue(emphasiseProgress, 1.0)
				),
				new KeyFrame(Duration.seconds(1),
					e -> emphasising = false,
					new KeyValue(emphasiseProgress, 0.0)
				)
			);
			// @formatter:on

		} else if (action instanceof HighlightAction) {
			// Highlight a marker
			highlightedMarkers.add(((HighlightAction) action).index);

		} else if (action instanceof ListAction) {
			// List changed
			ListAction list = (ListAction) action;
			synchronized (this.list) {
				synchronized (markers) { // TODO: don't hold more than 1 lock (might deadlock)
					this.list = list.structure;
					markers.clear();
				}
			}
		}

		if (timeline != null) {
			timeline.setCycleCount(1);
			timeline.setRate(rate);
			timeline.setOnFinished(e -> {
				synchronized (list) {
					list = ((ModelAction<long[]>) action).structure;
				}
				setUpdatePaused(false);
			});
			timeline.play();
			setUpdatePaused(true);
		}
	}

}
