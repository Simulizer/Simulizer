package simulizer.ui.components.highlevel;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
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
import simulizer.highlevel.models.ListModel.Emphasise;
import simulizer.highlevel.models.ListModel.Marker;
import simulizer.highlevel.models.ListModel.Swap;
import simulizer.ui.windows.HighLevelVisualisation;

public class ListVisualiser extends DataStructureVisualiser {
	private Canvas canvas = new Canvas();
	private long[] list;
	private ListModel model;

	private final Queue<Action> actionQueue = new LinkedList<>();
	private boolean animating = false;

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

	private int FRAME_RATE = 10;
	private AnimationTimer timer = new AnimationTimer() {
		long lastTime = -1;

		@Override
		public void handle(long now) {
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
	private double w;
	private double h;

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
		gc.setTextAlign(TextAlignment.CENTER);

		repaint();
	}

	@Override
	public void update(Observable o, Object obj) {
		super.update(o, obj);
		if (obj != null) {
			actionQueue.add((Action) obj);
			this.list = model.getList();
			runAnimations();
		}
	}

	private void runAnimations() {
		if (animating) return;
		else {
			Action action = actionQueue.poll();

			if (action instanceof Swap) {
				Swap swap = (Swap) action;

				animatedLeftIndex = swap.a;
				animatedRightIndex = swap.b;

				// This is correct
				animatedLeftLabel = "" + list[animatedRightIndex];
				animatedRightLabel = "" + list[animatedLeftIndex];

				double startXLeft = getX(animatedLeftIndex) / w;
				double startXRight = getX(animatedRightIndex) / w;

				double startY = y0 / h;
				double upY = 0.8 * (y0 - rectLength) / h;

			// @formatter:off
			Timeline timeline = new Timeline(
				new KeyFrame(Duration.seconds(0),
					new KeyValue(animatedLeftX, startXLeft),
					new KeyValue(animatedLeftY, startY),
					new KeyValue(animatedRightX, startXRight),
					new KeyValue(animatedRightY, startY)
				),
				new KeyFrame(Duration.seconds(0.5),
					new KeyValue(animatedLeftX, startXLeft),
					new KeyValue(animatedLeftY, startY),
					new KeyValue(animatedRightX, startXRight),
					new KeyValue(animatedRightY, upY)
				),
				new KeyFrame(Duration.seconds(0.8),
					new KeyValue(animatedLeftX, startXRight),
					new KeyValue(animatedLeftY, startY),
					new KeyValue(animatedRightX, startXLeft),
					new KeyValue(animatedRightY, upY)
				),
				new KeyFrame(Duration.seconds(1.3),
					e -> {
						// Apply Update
						list = swap.list;

						animating = false;
						repaint();

						synchronized (actionQueue) {
							if (actionQueue.isEmpty()) {
								timer.stop();
							} else runAnimations();
						}
					},
					new KeyValue(animatedLeftX, startXRight),
					new KeyValue(animatedLeftY, startY),
					new KeyValue(animatedRightX, startXLeft),
					new KeyValue(animatedRightY, startY)
				)
			);
			// @formatter:on

				timeline.setCycleCount(1);
				timeline.setRate(actionQueue.size() + 1); // TODO: Be more accurate

				timer.start();
				timeline.play();

				animating = true;
			} else if (action instanceof Marker) {
				// Marker Action
				Marker marker = (Marker) action;
				// TODO: Marker animation
			} else if (action instanceof Emphasise) {
				// Emphasise action
				Emphasise emphasise = (Emphasise) action;
				// TODO: Emphasise animation
			} else {
				// List changed
				list = action.list;
				repaint();
			}
		}
	}

	@Override
	public void repaint() {
		if (model.size() == 0) return;

		GraphicsContext gc = canvas.getGraphicsContext2D();
		this.w = canvas.getWidth();
		this.h = canvas.getHeight();

		gc.clearRect(0, 0, w, h);

		calculateDimensions(gc);

		drawList(gc);
		if (animating) {
			drawTextBox(gc, animatedLeftX.doubleValue() * w, animatedLeftY.doubleValue() * h, animatedLeftLabel);
			drawTextBox(gc, animatedRightX.doubleValue() * w, animatedRightY.doubleValue() * h, animatedRightLabel);
		}
	}

	private void drawList(GraphicsContext gc) {
		for (int i = 0; i < list.length; ++i) {
			if (animating && (i == animatedLeftIndex || i == animatedRightIndex)) continue;
			else drawTextBox(gc, i, list[i] + "");
		}
	}

	private void drawTextBox(GraphicsContext gc, int i, String text) {
		drawTextBox(gc, getX(i), y0, text);
	}

	private void drawTextBox(GraphicsContext gc, double x, double y, String text) {
		drawBorderedRectangle(gc, Color.SKYBLUE, x, y, rectLength, rectLength);

		gc.setFont(new Font("Arial", 55));
		gc.setFill(Color.BLACK);
		gc.beginPath();
		gc.fillText(text, x + rectLength / 2, y + rectLength / 2, rectLength);
		gc.closePath();
	}

	private double getX(int rectIndex) {
		return x0 + rectIndex * rectLength;
	}

	private void calculateDimensions(GraphicsContext gc) {
		double rectCalc = w - 2 * XPAD;

		rectLength = rectCalc < h - 2 * YPAD ? rectCalc : h - 2 * YPAD;
		this.rectLength = rectCalc / model.size();

		this.x0 = (w - model.size() * rectLength) / 2;
		this.y0 = h / 2 - rectLength / 2;
	}

	private void drawBorderedRectangle(GraphicsContext gc, Color fill, double x, double y, double w, double h) {
		gc.beginPath();
		gc.setFill(fill);
		gc.fillRect(x, y, w, h);
		gc.strokeRect(x, y, w, h);
		gc.closePath();
	}

	@Override
	public String getName() {
		return "List";
	}

}
