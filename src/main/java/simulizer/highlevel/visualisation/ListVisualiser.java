package simulizer.highlevel.visualisation;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ListVisualiser<T> extends DataStructureVisualiser {
	private class Pair {
		public int a;
		public int b;

		public Pair(int a, int b) {
			this.a = a;
			this.b = b;
		}
	}

	@SuppressWarnings("unused")
	private List<T> list;
	private List<Animation> animationBuffer = new ArrayList<>();
	private List<Pair> swapIndices = new ArrayList<>();
	private Rectangle[] rectangles = new Rectangle[5];

	private Text[] textBoxes = new Text[5];

	private final int X0 = 10;
	private final int Y0 = 80;
	private final int SPACING = 10;

	public ListVisualiser(Pane contentPane, int width, int height, List<T> list) {
		super(contentPane, width, height);
		this.setList(list);
	}

	public void setList(List<T> list) {
		this.list = list;

		initRectsAndBoxes();
	}

	// Return 5 test rectangles as a list
	private void initRectsAndBoxes() {
		Color[] colors = { Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.PINK };

		for (int i = 0; i < colors.length; ++i) {
			colors[i] = colors[i];
		}

		Pane contentPane = getDrawingPane();
		int rectWidth = getRectWidth();

		for (int i = 0; i < rectangles.length; ++i) {
			rectangles[i] = new Rectangle(getX(i), Y0, rectWidth, rectWidth);
			rectangles[i].setFill(colors[i]);
			rectangles[i].setStroke(Color.BLACK);

			textBoxes[i] = new Text("" + i);
			textBoxes[i].setFont(new Font("Arial", 55));
			textBoxes[i].setTranslateX(getTextX(i));
			textBoxes[i].setTranslateY(getTextY(i));

			contentPane.getChildren().addAll(rectangles[i], textBoxes[i]);
		}
	}

	private int getX(int rectIndex) {
		return X0 + rectIndex * (getRectWidth() + SPACING);
	}
	
	private int getTextX(int rectIndex) {
		return (int) (getX(rectIndex) + getRectWidth() / 2 - textBoxes[rectIndex].getBoundsInLocal().getWidth() / 2);
	}
	
	private int getTextY(int rectIndex) {
		return (int) (Y0 + getRectWidth() / 2 + textBoxes[rectIndex].getBoundsInLocal().getHeight() / 3);
	}

	private int getRectWidth() {
		return (int) ((getWidth() - 2 * X0) / rectangles.length) - SPACING + 1;
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

		Text text1 = textBoxes[i];
		Text text2 = textBoxes[j];
		
		animationBuffer.add(setupSwap(rect1, getX(i), Y0, rect2, getX(j), Y0));
		animationBuffer.add(setupSwap2(text1, getTextX(i), Y0, text2, getTextX(j), Y0));
		swapIndices.add(new Pair(i, j));
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
			Text temp2 = textBoxes[p.a];
			textBoxes[p.a] = textBoxes[p.b];
			textBoxes[p.b] = temp2;
		}
		
		swapIndices.clear();
	}

	private static ParallelTransition setupSwap(Rectangle rect1, int x1, int y1, Rectangle rect2, int x2, int y2) {
		ParallelTransition svar = new ParallelTransition();
		svar.getChildren().addAll((Animation) getTransition(rect1, x1, y1, x2, y2));
		svar.getChildren().addAll((Animation) getTransition(rect2, x2, y2, x1, y1));

		return svar;
	}

	private static PathTransition getTransition(Rectangle rect, int x1, int y1, int x2, int y2) {
		int width = rect.widthProperty().intValue();
		int height = rect.heightProperty().intValue();

		Path path = new Path();
		path.getElements().add(new MoveTo(x1 + width / 2, y1 + height / 2));
		path.getElements().add(new HLineTo(x2 + width / 2));
		PathTransition pathTransition = new PathTransition();
		pathTransition.setDuration(Duration.millis(500));
		pathTransition.setPath(path);
		pathTransition.setNode(rect);
		pathTransition.setCycleCount(1);

		return pathTransition;
	}
	
	private static ParallelTransition setupSwap2(Text rect1, int x1, int y1, Text rect2, int x2, int y2) {
		ParallelTransition svar = new ParallelTransition();
		svar.getChildren().addAll((Animation) getTransition2(rect1, x1, y1, x2, y2));
		svar.getChildren().addAll((Animation) getTransition2(rect2, x2, y2, x1, y1));

		return svar;
	}

	private static PathTransition getTransition2(Text rect, int x1, int y1, int x2, int y2) {
		int width = (int) rect.getBoundsInLocal().getWidth();
		int height = (int) rect.getBoundsInLocal().getHeight();

		Path path = new Path();
		path.getElements().add(new MoveTo(x1 + width/2,y1+1.5*height));
		path.getElements().add(new HLineTo(x2 + width/2));
		PathTransition pathTransition = new PathTransition();
		pathTransition.setDuration(Duration.millis(500));
		pathTransition.setPath(path);
		pathTransition.setNode(rect);
		pathTransition.setCycleCount(1);

		return pathTransition;
	}
}
