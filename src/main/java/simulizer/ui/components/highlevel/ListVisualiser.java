package simulizer.ui.components.highlevel;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.VLineTo;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import simulizer.ui.windows.HighLevelVisualisation;

/**
 * Visualises the sorting of a list
 *
 * @author Kelsey McKenna
 *
 * @param <T>
 *            the data type stored in the list
 */
public class ListVisualiser extends DataStructureVisualiser {
	private class Pair {
		public int a;
		public int b;

		public Pair(int a, int b) {
			this.a = a;
			this.b = b;
		}
	}

	private List<?> list;
	private List<Animation> animationBuffer = new ArrayList<>();
	private List<Pair> swapIndices = new ArrayList<>();
	private Rectangle[] rectangles;
	private Text[] textLabels;
	private Text[] markers;

	private double rectLength;
	private double y0;

	private final double XPAD = 10;
	private final double YPAD = 10;

	private boolean queuing;

	/**
	 * @param width
	 *            the width of the area to draw on
	 * @param height
	 *            the height of the area to draw on
	 * @param list
	 *            the list to be visualised
	 */
	public ListVisualiser(HighLevelVisualisation vis) {
		super(vis);
	}

	public void setList(List<?> list) {
		this.list = list;

		this.rectangles = new Rectangle[list.size()];
		this.textLabels = new Text[list.size()];
		this.markers = new Text[list.size() + 2]; // + 2 so you can put a marker to the left and right

		calculateDimensions();

		Platform.runLater(() -> {
			initRectsAndBoxes();
			resize();
		});
	}

	private void initRectsAndBoxes() {
		for (int i = 0; i < rectangles.length; ++i) {
			rectangles[i] = new Rectangle(getX(i), y0, rectLength, rectLength);
			rectangles[i].getStyleClass().add("list-item");

			setAttrs(rectangles[i], getX(i), y0, 0, 0);

			textLabels[i] = new Text("" + list.get(i));
			textLabels[i].setFont(new Font("Arial", 55)); // Need to set this here so that text size calculations work.
			textLabels[i].setTranslateX(getTextX(i, textLabels[i]));
			textLabels[i].setTranslateY(getTextY(i));

			vis.addAll(rectangles[i], textLabels[i]);
		}

		for (int i = 0; i < markers.length; ++i) {
			markers[i] = new Text("");
			markers[i].setFont(new Font(35));
			vis.add(markers[i]);
		}
	}

	private double getX(int rectIndex) {
		double blockWidth = list.size() * rectLength;
		return getWidth() / 2 - blockWidth / 2 + rectIndex * rectLength;
	}

	private double getTextX(int rectIndex, Text label) {
		return getX(rectIndex) + rectLength / 2 - label.getBoundsInLocal().getWidth() / 2;
	}

	private double getTextY(int rectIndex) {
		return y0 + rectLength / 2 + textLabels[rectIndex].getBoundsInLocal().getHeight() / 3;
	}

	private double getMarkerY(int index) {
		return y0 - markers[index + 1].getBoundsInLocal().getHeight() / 3;
	}

	public ListVisualiser batch() {
		this.queuing = true;
		return this;
	}

	/**
	 * Calculates the animation for swapping the items at the specified indices.
	 *
	 * @param i
	 *            the index of the first element
	 * @param j
	 *            the index of the second element
	 */
	public ListVisualiser swap(int i, int j) {
		Rectangle rect1 = rectangles[i];
		Rectangle rect2 = rectangles[j];

		Text text1 = textLabels[i];
		Text text2 = textLabels[j];

		animationBuffer.add(setupSwap(rect1, getX(i), y0, rect2, getX(j), y0));
		animationBuffer.add(setupTextSwap(text1, getTextX(i, text1), y0, text2, getTextX(j, text2), y0));
		swapIndices.add(new Pair(i, j));

		if (queuing) return this;
		else {
			commit();
			return this;
		}
	}

	/**
	 * Emphasises the specified element through an animation. E.g. could
	 * emphasise the current element being examined in binary search.
	 *
	 * @param i
	 *            the index of the element to be emphasised (can be outside valid range)
	 */
	public ListVisualiser emphasise(int i) {
		if (i >= 0 && i < rectangles.length) {
			Rectangle rect = rectangles[i];
			Text text = textLabels[i];

			FillTransition ft = new FillTransition(Duration.millis(300), rect, Color.WHITE, Color.RED);
			FillTransition tt = new FillTransition(Duration.millis(300), text, Color.BLACK, Color.WHITE);
			ft.setCycleCount(2);
			ft.setAutoReverse(true);

			tt.setCycleCount(2);
			tt.setAutoReverse(true);

			animationBuffer.add(new ParallelTransition(ft, tt));

			if (queuing) return this;
			else {
				commit();
				return this;
			}
		}

		return this;
	}

	/**
	 * Adds a marker at the specified index. If the index is -1,
	 * then the marker will be added to the left of the list. If the marker
	 * is equal to the length of the list, then the marker will be placed to
	 * the right of the list
	 *
	 * @param index
	 *            the index of where to place the label
	 * @param label
	 *            the label to place
	 */
	public void addMarker(int index, String label) {
		String existingText = markers[index + 1].getText();
		markers[index + 1].setText(existingText + " " + label);
		markers[index + 1].setTranslateX(getTextX(index, markers[index + 1]));
		markers[index + 1].setTranslateY(getMarkerY(index));

		resize();
	}

	public void clearMarkers() {
		for (Text marker : markers)
			marker.setText("");
	}

	public void setTextLabel(int i, String label) {
		textLabels[i].setText(label);
		resize();
	}

	/**
	 * Animates (in parallel) the animations in the buffer; wipes the animation
	 * buffer.
	 */
	public void commit() {
		queuing = false;

		ParallelTransition animation = new ParallelTransition();
		animation.getChildren().addAll(animationBuffer);
		animation.play();
		animationBuffer.clear();

		// Swap the elements in memory
		for (Pair p : swapIndices) {
			Rectangle temp = rectangles[p.a];
			rectangles[p.a] = rectangles[p.b];
			rectangles[p.b] = temp;

			double xTemp = rectangles[p.a].getX();
			rectangles[p.a].setX(rectangles[p.b].getX());
			rectangles[p.b].setX(xTemp);

			// Commenting different parts of the below has strange side-effects
			Text temp2 = textLabels[p.a];
			textLabels[p.a] = textLabels[p.b];
			textLabels[p.b] = temp2;

			xTemp = textLabels[p.a].getX();
			textLabels[p.a].setX(textLabels[p.b].getX());
			textLabels[p.b].setX(xTemp);
		}

		swapIndices.clear();
	}

	private ParallelTransition setupSwap(Rectangle rect1, double x1, double y1, Rectangle rect2, double x2, double y2) {
		ParallelTransition svar = new ParallelTransition();
		svar.getChildren().addAll((Animation) getTransition(rect1, x1, y1, x2, y2, true));
		svar.getChildren().addAll((Animation) getTransition(rect2, x2, y2, x1, y1, false));

		return svar;
	}

	private PathTransition getTransition(Rectangle rect, double x1, double y1, double x2, double y2, boolean above) {
		int width = rect.widthProperty().intValue();
		int height = rect.heightProperty().intValue();

		double mul = above ? -1 : 2.7;
		double y = y1 + height / 2;

		Path path = new Path();
		path.getElements().add(new MoveTo(x1 + width / 2, y));
		path.getElements().add(new VLineTo(y1 + mul * (height / 2 + 10)));
		path.getElements().add(new HLineTo(x2 + width / 2));
		path.getElements().add(new VLineTo(y));
		PathTransition pathTransition = new PathTransition();
		pathTransition.setDuration(Duration.millis(getRate()));
		pathTransition.setPath(path);
		pathTransition.setNode(rect);
		pathTransition.setCycleCount(1);

		return pathTransition;
	}

	private ParallelTransition setupTextSwap(Text rect1, double x1, double y1, Text rect2, double x2, double y2) {
		ParallelTransition svar = new ParallelTransition();
		svar.getChildren().addAll((Animation) getTextTransition(rect1, x1, y1, x2, y2, true));
		svar.getChildren().addAll((Animation) getTextTransition(rect2, x2, y2, x1, y1, false));

		return svar;
	}

	private PathTransition getTextTransition(Text rect, double x1, double y1, double x2, double y2, boolean above) {
		double width = rect.getBoundsInLocal().getWidth();

		double mul = above ? -1 : 2.7;
		double y = y0 + rectLength / 2;

		Path path = new Path();
		path.getElements().add(new MoveTo(x1 + width / 2, y));
		path.getElements().add(new VLineTo(y0 + mul * (rectLength / 2 + 10)));
		path.getElements().add(new HLineTo(x2 + width / 2));
		path.getElements().add(new VLineTo(y));
		PathTransition pathTransition = new PathTransition();
		pathTransition.setDuration(Duration.millis(getRate()));
		pathTransition.setPath(path);
		pathTransition.setNode(rect);
		pathTransition.setCycleCount(1);

		return pathTransition;
	}

	private void calculateDimensions() {
		double width = vis.getWindowWidth();
		double height = vis.getWindowHeight();

		double rectCalc;
		if (height < width) rectCalc = height - 2 * YPAD;
		else rectCalc = width - 2 * XPAD;
		this.rectLength = rectCalc / rectangles.length;

		this.y0 = height / 2 - rectLength / 2;
	}

	@Override
	public void resize() {
		if (rectangles == null || textLabels == null) return;

		calculateDimensions();

		for (int i = 0; i < rectangles.length; ++i) {
			setAttrs(rectangles[i], getX(i), y0, rectLength, rectLength);

			textLabels[i].setTranslateX(getTextX(i, textLabels[i]));
			textLabels[i].setTranslateY(getTextY(i));

			if (markers[i] != null) {
				markers[i].setTranslateX(getTextX(i - 1, markers[i]));
				markers[i].setTranslateY(getMarkerY(i - 1));
			}
		}

		for (int i = rectangles.length; i < rectangles.length + 2; ++i) {
			if (markers[i] != null) {
				markers[i].setTranslateX(getTextX(i - 1, markers[i]));
				markers[i].setTranslateY(getMarkerY(i - 1));
			}
		}

		setWidth(vis.getWidth());
		setHeight(vis.getHeight());
	}

}
