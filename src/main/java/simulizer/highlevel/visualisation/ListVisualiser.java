package simulizer.highlevel.visualisation;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
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
public class ListVisualiser<T> extends DataStructureVisualiser {
	private class Pair {
		public int a;
		public int b;

		public Pair(int a, int b) {
			this.a = a;
			this.b = b;
		}
	}

	private List<T> list;
	private List<Animation> animationBuffer = new ArrayList<>();
	private List<Pair> swapIndices = new ArrayList<>();
	private Rectangle[] rectangles;
	private Text[] textLabels;

	private double rectLength;

	private final double XPAD = 10;
	private final double YPAD = 10;

	/**
	 * @param contentPane
	 *            the pane onto which the visualiser will draw
	 * @param width
	 *            the width of the area to draw on
	 * @param height
	 *            the height of the area to draw on
	 * @param list
	 *            the list to be visualised
	 */
	public ListVisualiser(HighLevelVisualisation vis, double width, double height, List<T> list) {
		super(vis, width, height);
		this.setList(list);
	}

	public void setList(List<T> list) {
		this.list = list;
		this.rectangles = new Rectangle[list.size()];
		this.textLabels = new Text[list.size()];

		initRectsAndBoxes();
		resize();
	}

	private void initRectsAndBoxes() {
		HighLevelVisualisation vis = getHighLevelVisualisation();
		double rectWidth = getRectLength();

		for (int i = 0; i < rectangles.length; ++i) {
			rectangles[i] = new Rectangle(getX(i), getY(), rectWidth, rectWidth);
			rectangles[i].getStyleClass().add("list-item");

			textLabels[i] = new Text("" + list.get(i));
			textLabels[i].setFont(new Font("Arial", 55)); // Need to set this here so that text size calculations work.
			textLabels[i].setTranslateX(getTextX(i));
			textLabels[i].setTranslateY(getTextY(i));

			vis.addAll(rectangles[i], textLabels[i]);
		}
	}

	private double getX(int rectIndex) {
		double rectLength = getRectLength();
		double blockWidth = list.size() * rectLength;
		return getWidth() / 2 - blockWidth / 2 + rectIndex * rectLength;
	}

	private double getY() {
		// double answer = getHighLevelVisualisation().getWindowHeight() / 2 - getRectLength() / 2;
		// System.out.println(answer);
		return super.getHeight() / 2 - getRectLength() / 2;
	}

	private double getTextX(int rectIndex) {
		return getX(rectIndex) + getRectLength() / 2 - textLabels[rectIndex].getBoundsInLocal().getWidth() / 2;
	}

	private double getTextY(int rectIndex) {
		return getY() + getRectLength() / 2 + textLabels[rectIndex].getBoundsInLocal().getHeight() / 3;
	}

	private double getRectLength() {
		return getRectLength(super.getWidth(), super.getHeight());
	}

	private double getRectLength(double windowWidth, double windowHeight) {
		double l;
		if (windowHeight < windowHeight) l = windowHeight - 2 * YPAD;
		else l = windowWidth - 2 * XPAD;

		return l / rectangles.length;
	}

	/**
	 * Calculates the animation for swapping the items at the specified indices.
	 *
	 * @param i
	 *            the index of the first element
	 * @param j
	 *            the index of the second element
	 */
	public void swap(int i, int j) {
		Rectangle rect1 = rectangles[i];
		Rectangle rect2 = rectangles[j];

		Text text1 = textLabels[i];
		Text text2 = textLabels[j];

		double y0 = getY();
		animationBuffer.add(setupSwap(rect1, getX(i), y0, rect2, getX(j), y0));
		animationBuffer.add(setupSwap2(text1, getTextX(i), y0, text2, getTextX(j), y0));
		swapIndices.add(new Pair(i, j));
	}

	/**
	 * Emphasises the specified element through an animation. E.g. could
	 * emphasise the current element being examined in binary search.
	 *
	 * @param i
	 *            the index of the element to be emphasised
	 */
	public void emphasise(int i) {
		Rectangle rect = rectangles[i];
		Text text = textLabels[i];

		FillTransition ft = new FillTransition(Duration.millis(300), rect, Color.WHITE, Color.RED);
		FillTransition tt = new FillTransition(Duration.millis(300), text, Color.BLACK, Color.WHITE);
		ft.setCycleCount(2);
		ft.setAutoReverse(true);

		tt.setCycleCount(2);
		tt.setAutoReverse(true);

		animationBuffer.add(new ParallelTransition(ft, tt));
	}

	/**
	 * Animates (in parallel) the animations in the buffer; wipes the animation
	 * buffer.
	 */
	public void commit() {
		ParallelTransition animation = new ParallelTransition();
		animation.getChildren().addAll(animationBuffer);
		animation.play();
		animationBuffer.clear();

		// Swap the elements in memory
		for (Pair p : swapIndices) {
			Rectangle temp = rectangles[p.a];
			rectangles[p.a] = rectangles[p.b];
			rectangles[p.b] = temp;

			// Commenting different parts of the below has strange side-effects
			Text temp2 = textLabels[p.a];
			textLabels[p.a] = textLabels[p.b];
			textLabels[p.b] = temp2;
		}

		swapIndices.clear();
	}

	private ParallelTransition setupSwap(Rectangle rect1, double x1, double y1, Rectangle rect2, double x2, double y2) {
		ParallelTransition svar = new ParallelTransition();
		svar.getChildren().addAll((Animation) getTransition(rect1, x1, y1, x2, y2));
		svar.getChildren().addAll((Animation) getTransition(rect2, x2, y2, x1, y1));

		return svar;
	}

	private PathTransition getTransition(Rectangle rect, double x1, double y1, double x2, double y2) {
		int width = rect.widthProperty().intValue();
		int height = rect.heightProperty().intValue();

		Path path = new Path();
		path.getElements().add(new MoveTo(x1 + width / 2, y1 + height / 2));
		path.getElements().add(new HLineTo(x2 + width / 2));
		PathTransition pathTransition = new PathTransition();
		pathTransition.setDuration(Duration.millis(getRate()));
		pathTransition.setPath(path);
		pathTransition.setNode(rect);
		pathTransition.setCycleCount(1);

		return pathTransition;
	}

	private ParallelTransition setupSwap2(Text rect1, double x1, double y1, Text rect2, double x2, double y2) {
		ParallelTransition svar = new ParallelTransition();
		svar.getChildren().addAll((Animation) getTransition2(rect1, x1, y1, x2, y2));
		svar.getChildren().addAll((Animation) getTransition2(rect2, x2, y2, x1, y1));

		return svar;
	}

	private PathTransition getTransition2(Text rect, double x1, double y1, double x2, double y2) {
		int width = (int) rect.getBoundsInLocal().getWidth();
		int height = (int) rect.getBoundsInLocal().getHeight();

		Path path = new Path();
		path.getElements().add(new MoveTo(x1 + width / 2, y1 + 1.5 * height));
		path.getElements().add(new HLineTo(x2 + width / 2));
		PathTransition pathTransition = new PathTransition();
		pathTransition.setDuration(Duration.millis(getRate()));
		pathTransition.setPath(path);
		pathTransition.setNode(rect);
		pathTransition.setCycleCount(1);

		return pathTransition;
	}

	@Override
	public void resize() {
		double windowWidth = getHighLevelVisualisation().getWindowWidth();
		double windowHeight = getHighLevelVisualisation().getWindowHeight();

		double rectWidth = getRectLength(windowWidth, windowHeight);

		for (int i = 0; i < rectangles.length; ++i) {
			setAttrs(rectangles[i], getX(i), getY(), rectWidth, rectWidth);
			rectangles[i].getStyleClass().add("list-item");

			textLabels[i].setTranslateX(getTextX(i));
			textLabels[i].setTranslateY(getTextY(i));
		}

		setWidth(windowWidth);
		setHeight(windowHeight);
	}

}
