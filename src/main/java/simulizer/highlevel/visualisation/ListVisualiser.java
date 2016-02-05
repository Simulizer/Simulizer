package simulizer.highlevel.visualisation;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class ListVisualiser<T> extends DataStructureVisualiser {
	private List<T> list;
	private List<Animation> animationBuffer;
	private List<Rectangle> rectangles = new ArrayList<>();

	public ListVisualiser(List<T> list) {
		this.setList(list);
		this.list = list;
	}

	public void setList(List<T> list) {
		this.list = list;

		rectangles.clear();

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
		Rectangle rect1 = rectangles.get(i);
		Rectangle rect2 = rectangles.get(j);
		// animationBuffer.add(
		// setupSwap(rect1, )
		// );
	}

	/**
	 * Animates (in parallel) the animations in the buffer; wipes the animation
	 * buffer.
	 */
	public void commit() {
		ParallelTransition animation = new ParallelTransition();
		animation.getChildren().addAll(animationBuffer);
		animation.play();
	}

	private static ParallelTransition setupSwap(Shape shape1, int x1, int y1, int w1, int h1, Shape shape2, int x2, int y2, int w2,
			int h2) {
		ParallelTransition svar = new ParallelTransition();
		svar.getChildren().addAll((Animation) getTransition(shape1, w1, h1, x1, y1, x2, y2));
		svar.getChildren().addAll((Animation) getTransition(shape2, w2, h2, x2, y2, x1, y1));

		return svar;
	}

	private static PathTransition getTransition(Shape shape, int shapeWidth, int shapeHeight, int x1, int y1, int x2, int y2) {
		Path path = new Path();
		path.getElements().add(new MoveTo(x1 + shapeWidth / 2, y1 + shapeHeight / 2));
		path.getElements().add(new HLineTo(x2 + shapeWidth / 2));
		PathTransition pathTransition = new PathTransition();
		pathTransition.setDuration(Duration.millis(500));
		pathTransition.setPath(path);
		pathTransition.setNode(shape);
		pathTransition.setCycleCount(1);

		return pathTransition;
	}
}
