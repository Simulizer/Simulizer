package simulizer.ui.components.highlevel;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Queue;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import simulizer.highlevel.models.ListModel.Action;
import simulizer.highlevel.models.ListModel.Swap;
import simulizer.ui.windows.HighLevelVisualisation;

public class ListVisualiser extends DataStructureVisualiser {
	private Canvas canvas = new Canvas();
	private long[] list;
	private ListModel model;

	private final Queue<Action> actionQueue = new LinkedList<>();
	private boolean animating = false;
	private int animatedItemIndex;
	private DoubleProperty animatedItemX = new SimpleDoubleProperty();
	private DoubleProperty animatedItemY = new SimpleDoubleProperty();
	private int FRAME_RATE = 45;
	private AnimationTimer timer = new AnimationTimer() {
		long lastTime = -1;

		@Override
		public void handle(long now) {
			// 30 FPS
			if (lastTime == -1 || now - lastTime > 1e9 / FRAME_RATE) {
				lastTime = now;
				repaint();
			}
		}
	};

	// Dimensions used for calculations
	private final double XPAD = 10;
	private final double YPAD = 10;
	private double rectLength;
	private double y0;
	private double x0;

	public ListVisualiser(ListModel model, HighLevelVisualisation vis) {
		super(model, vis);
		this.model = model;
		list = model.getList();
		getChildren().add(canvas);

		canvas.widthProperty().bind(super.widthProperty());
		canvas.heightProperty().bind(super.heightProperty());

		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(2);
		gc.setStroke(Color.BLACK);
		gc.setTextBaseline(VPos.CENTER);

		repaint();
	}

	@Override
	public void update(Observable o, Object obj) {
		super.update(o, obj);
		actionQueue.add((Action) obj);
		runAnimations();
	}

	private void runAnimations() {
		if (animating)
			return;
		else {
			Action action = actionQueue.poll();

			// Currently only swaps are implemented
			if (!(action instanceof Swap))
				return;

			Swap swap = (Swap) action;

			Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.3), e -> {
				// Apply Update
				list = swap.list;

				animating = false;
				repaint();

				if (actionQueue.isEmpty()) {
					timer.stop();
					System.out.println("List animation timer stopped");
				} else
					runAnimations();
			}));

			timeline.setCycleCount(1);
			timeline.setRate(actionQueue.size() + 1); // TODO: Be more accurate

			timer.start();
			timeline.play();

			animating = true;
		}
	}

	@Override
	public void repaint() {
		if (model.size() == 0)
			return;

		GraphicsContext gc = canvas.getGraphicsContext2D();
		final double width = canvas.getWidth();
		final double height = canvas.getHeight();

		calculateDimensions(gc, width, height);

		// TODO Platform.runLater() ?
		gc.clearRect(0, 0, width, height);
		drawList(gc);
	}

	private void drawList(GraphicsContext gc) {
		for (int i = 0; i < list.length; ++i) {
			drawBorderedRectangle(gc, Color.SKYBLUE, getX(i), y0, rectLength, rectLength);

			gc.setTextAlign(TextAlignment.CENTER);
			gc.setTextBaseline(VPos.CENTER);
			gc.setTextAlign(TextAlignment.CENTER);
			gc.setFont(new Font("Arial", 55));
			gc.setFill(Color.BLACK);
			gc.fillText(list[i] + "", getX(i) + rectLength / 2, y0 + rectLength / 2);
		}
	}

	private double getX(int rectIndex) {
		return x0 + rectIndex * rectLength;
	}

	private void calculateDimensions(GraphicsContext gc, double width, double height) {
		double rectCalc = width - 2 * XPAD;

		rectLength = rectCalc < height - 2 * YPAD ? rectCalc : height - 2 * YPAD;
		this.rectLength = rectCalc / model.size();

		this.x0 = (width - model.size() * rectLength) / 2;
		this.y0 = height / 2 - rectLength / 2;
	}

	private void drawBorderedRectangle(GraphicsContext gc, Color fill, double x, double y, double w, double h) {
		gc.setFill(fill);
		gc.fillRect(x, y, w, h);
		gc.strokeRect(x, y, w, h);
	}

	@Override
	public String getName() {
		return "List";
	}

}
