package simulizer.ui.windows;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import simulizer.assembler.representation.Address;
import simulizer.assembler.representation.Statement;
import simulizer.lowlevel.models.PipelineHistoryModel;
import simulizer.simulation.cpu.components.CPU;
import simulizer.ui.components.NumberTextField;
import simulizer.ui.interfaces.InternalWindow;

public class PipelineView extends InternalWindow implements Observer {
	// Graphical things
	private Canvas canvas = new Canvas();
	private Pane canvasPane = new Pane();
	private BorderPane borderPane = new BorderPane();

	private FlowPane buttonPane = new FlowPane();
	private Button leftButton = new Button("<");
	private Button rightButton = new Button(">");
	private Label cycleInputLabel = new Label("Go to:");
	private TextField cycleInput = new NumberTextField();
	private CheckBox followCheckBox = new CheckBox("Follow");

	private final String DEFAULT_INSTR =
		// @formatter:off
		  "       Statement:\n"
		+ "         Address:\n"
		+ "Instruction type:\n"
		+ "     Line number:";
		// @formatter:on
	private Label instructionInfoLabel = new Label(DEFAULT_INSTR);

	// Model
	private PipelineHistoryModel model = new PipelineHistoryModel();
	// Canvas has a maximum size, so don't draw more than it!
	private int numColumnsToDraw;
	private int startCycle = 0;
	private boolean snapToEnd;

	private boolean isPipelined;

	// Dimensions used for calculations
	private double rectWidth;
	private double cycleWidth;
	private double rectGap;

	// The width and the height of the canvas
	// saved for reuse throughout calculations
	// after resize
	private double w;
	private double x0;
	private double h;
	private double realH;
	private double realW;

	public PipelineView() {
		setTitle("Pipeline");

		// Configure the components
		followCheckBox.setSelected(true);
		followCheckBox.setOnAction(e -> repaint());

		leftButton.setOnAction(e -> {
			setStartCycle(startCycle - 1);
			repaint();
		});
		rightButton.setOnAction(e -> {
			setStartCycle(startCycle + 1);
			repaint();
		});
		leftButton.setCursor(Cursor.DEFAULT);
		rightButton.setCursor(Cursor.DEFAULT);

		cycleInput.setPrefColumnCount(5);
		cycleInput.setPromptText("Cycle no.");
		cycleInput.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			if (e.getCode() == KeyCode.ENTER && !cycleInput.getText().isEmpty()) {
				setStartCycle(Integer.valueOf(cycleInput.getText()));
				repaint();
				Platform.runLater(() -> cycleInput.setText(""));
			}
		});

		canvasPane.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> canvasPane.requestFocus());
		canvasPane.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			KeyCode code = e.getCode();

			if (code == KeyCode.LEFT || code == KeyCode.KP_LEFT) {
				setStartCycle(startCycle - 1);
				repaint();
			} else if (code == KeyCode.RIGHT || code == KeyCode.KP_RIGHT) {
				setStartCycle(startCycle + 1);
				repaint();
			}

			e.consume(); // the right arrow key seems to want to transfer the focus
			Platform.runLater(() -> canvasPane.requestFocus());
		});

		instructionInfoLabel.setStyle("-fx-font-family: monospace");
		instructionInfoLabel.setPadding(new Insets(20, 0, 15, 50));

		canvasPane.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
			double eventX = e.getX(), eventY = e.getY();
			Address address = getAddressAtPoint(eventX, eventY);

			String newText = address == null ? DEFAULT_INSTR : getInfo(address);
			Platform.runLater(() -> instructionInfoLabel.setText(newText));
		});

		// Now add everything
		canvasPane.getChildren().add(canvas);
		buttonPane.getChildren().addAll(followCheckBox, leftButton, rightButton, cycleInputLabel, cycleInput, instructionInfoLabel);

		borderPane.setCenter(canvasPane);
		borderPane.setBottom(buttonPane);

		getContentPane().getChildren().add(borderPane);

		// These things should be the same throughout, so just set them now
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(2);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		gc.setFont(new Font("Monospace", 15));

		// The canvas controls its own width, but it needs to know
		// the height of the scroll pane
		canvas.heightProperty().bind(canvasPane.heightProperty());
		canvas.heightProperty().addListener(e -> repaint());
		canvas.widthProperty().bind(canvasPane.widthProperty());
		canvas.widthProperty().addListener(e -> repaint());

		model.addObserver(this);
	}

	/**
	 * Calculates the address shown at the point (x,y) on the screen.
	 * Returns null if there is no address at the given point.
	 *
	 * @param x
	 *            the x coordinate of the point on the screen
	 * @param y
	 *            the y coordinate of the point on the screen
	 * @return the address at (x,y) on the screen; null if there is no address at this point
	 */
	private Address getAddressAtPoint(double x, double y) {
		List<PipelineHistoryModel.PipelineState> history = model.getHistory();

		// Loop over each column
		for (int col = 0, cycle = startCycle; col < numColumnsToDraw; ++col, ++cycle) {
			PipelineHistoryModel.PipelineState state = history.get(cycle);

			double xLeft = x0 + (col + 0.5) * cycleWidth - rectWidth / 2;
			// If the x coordinate isn't right, then skip to the next iteration
			if (x < xLeft || x > xLeft + rectWidth) continue;

			double yTop = rectGap / 2;

			List<Address> before = state.before;
			for (Address addr : before) {
				if (y >= yTop && y < yTop + rectWidth) return addr;
				else yTop += rectGap + rectWidth;
			}

			List<Address> pipeline = Arrays.asList(state.fetched, state.decoded, state.executed);
			for (Address stage : pipeline) {
				if (y >= yTop && y < yTop + rectWidth) return stage;
				else yTop += rectGap + rectWidth;
			}

			List<Address> after = state.after;
			for (Address addr : after) {
				if (y >= yTop && y < yTop + rectWidth) return addr;
				else yTop += rectGap + rectWidth;
			}
		}

		return null;
	}

	public PipelineHistoryModel getModel() {
		return model;
	}

	/**
	 * Sets the leftmost cycle on the screen.
	 * If the input is < 0, then it is snapped to 0.
	 * If the input is off the right end, it is snapped to the rightmost cycle.
	 * Otherwise it is set as the input.
	 *
	 * @param startCycle
	 *            the new value for start cycle
	 */
	private void setStartCycle(final int startCycle) {
		if (startCycle <= 0) this.startCycle = 0;
		else if (startCycle >= model.size()) this.startCycle = model.size() - 1;
		else this.startCycle = startCycle;
	}

	private void calculateParameters() {
		x0 = 0.15 * realW;

		rectWidth = 2. / 21 * h;
		cycleWidth = 3. / 2 * rectWidth;
		// TODO figure out a proper calculation
		rectGap = (h - 8 * rectWidth) / 15;

		setStartCycle(startCycle); // will reset startCycle to 0 if model has been reset

		numColumnsToDraw = Math.min(model.size() - startCycle, (int) (w / cycleWidth));
		assert (numColumnsToDraw >= 0);

		if (snapToEnd) setStartCycle(model.size() - numColumnsToDraw);
	}

	/**
	 * Draws the dividers between the different contents of the
	 * pipeline between cycles
	 */
	private void drawDividers(GraphicsContext gc) {
		// Draw the vertical dividers
		gc.setStroke(Color.GRAY);

		for (int i = 0; i <= numColumnsToDraw; ++i) {
			double x = x0 + i * cycleWidth;
			gc.strokeLine(x, 0, x, h);
		}

		if (numColumnsToDraw > 0) {
			double xEnd = x0 + numColumnsToDraw * cycleWidth;
			// Draw the horizontal dividers
			gc.setStroke(Color.BLACK);
			double y0 = h / 3;
			double y1 = 2 * h / 3;
			gc.strokeLine(0, y0, xEnd, y0);
			gc.strokeLine(0, y1, xEnd, y1);
		}
	}

	private void drawExplainers(GraphicsContext gc) {
		gc.setTextAlign(TextAlignment.LEFT);

		double x = 0.05 * x0;
		double maxWidth = 0.90 * x0;
		double y = h / 3 + rectGap / 2 + rectWidth / 2;
		gc.fillText("Stage 1: Fetch", x, y, maxWidth);

		y += rectGap + rectWidth;
		gc.fillText("Stage 2: Decode", x, y, maxWidth);

		y += rectGap + rectWidth;
		gc.fillText("Stage 3: Execute", x, y, maxWidth);

		gc.setTextAlign(TextAlignment.CENTER);
	}

	@Override
	public void update(Observable o, Object pipelineState) {
		this.isPipelined = getWindowManager().getCPU().isPipelined();

		this.snapToEnd = followCheckBox.isSelected();
		repaint();
		// Should only snap to end when an update has been fire
		// i.e. not when user is scrolling around
		this.snapToEnd = false;
	}

	private void drawAddresses(GraphicsContext gc) {
		// First need to draw the rectangles in the column
		// We know the length of the rectangles and the gaps
		// between them. Their position is just
		// half way between the cycle boundaries then - rectWidth/2.
		gc.setFill(Color.SKYBLUE);

		List<PipelineHistoryModel.PipelineState> history = model.getHistory();

		// Draw the addresses before the pipeline
		for (int col = 0, cycle = startCycle; col < numColumnsToDraw; ++col, ++cycle) {
			List<Address> before = history.get(cycle).before;

			double yTracker = rectGap / 2;
			double yCenter = yTracker + rectWidth / 2;

			double xCenter = x0 + (col + 0.5) * cycleWidth;
			double xLeft = xCenter - rectWidth / 2;

			for (Address addr : before) {
				gc.setFill(getColor(addr));
				drawBorderedRectangle(gc, xLeft, yTracker, rectWidth, rectWidth);

				gc.setFill(Color.BLACK);
				gc.fillText(getShortName(addr), xCenter, yCenter);

				yTracker += rectGap + rectWidth;
				yCenter += rectGap + rectWidth;
			}
		}

		// Draw the addresses in the pipeline
		for (int col = 0, cycle = startCycle; col < numColumnsToDraw; ++col, ++cycle) {
			PipelineHistoryModel.PipelineState state = history.get(cycle);

			double yTracker = h / 3 + rectGap / 2; // starts at top of first rect to draw
			double yCenter = yTracker + rectWidth / 2;

			double xCenter = x0 + (col + 0.5) * cycleWidth;
			double xLeft = xCenter - rectWidth / 2;

			Address[] parts = { state.fetched, state.decoded, state.executed };

			for (int a = 0; a < 3; ++a) {
				gc.setFill(getColor(parts[a]));
				if (parts[a] == null) drawBorderedCircle(gc, xLeft, yTracker, rectWidth, rectWidth);
				else {
					drawBorderedRectangle(gc, xLeft, yTracker, rectWidth, rectWidth);
					gc.setFill(Color.BLACK);
					gc.fillText(getShortName(parts[a]), xCenter, yCenter);
				}

				yTracker += rectGap + rectWidth;
				yCenter += rectGap + rectWidth;
			}

			gc.setFill(Color.BLACK);
			gc.fillText("" + cycle, xCenter, 0.975 * realH);
		}

		// Draw the addresses after the pipeline
		for (int col = 0, cycle = startCycle; col < numColumnsToDraw; ++col, ++cycle) {
			List<Address> after = history.get(cycle).after;

			double yTracker = 2 * h / 3 + rectGap / 2;
			double yCenter = yTracker + rectWidth / 2;

			double xCenter = x0 + (col + 0.5) * cycleWidth;
			double xLeft = xCenter - rectWidth / 2;

			for (Address addr : after) {
				gc.setFill(getColor(addr));
				drawBorderedRectangle(gc, xLeft, yTracker, rectWidth, rectWidth);

				gc.setFill(Color.BLACK);
				gc.fillText(getShortName(addr), xCenter, yCenter);

				yTracker += rectGap + rectWidth;
				yCenter += rectGap + rectWidth;
			}
		}
	}

	@Override
	public void ready() {
		super.ready();
		repaint();
	}

	public void repaint() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		this.realW = canvas.getWidth();
		this.realH = canvas.getHeight();
		this.w = 0.85 * realW;
		this.h = 0.95 * realH;

		gc.clearRect(0, 0, realW, realH);

		if (isPipelined) {
			calculateParameters();
			drawDividers(gc);
			drawExplainers(gc);
			drawAddresses(gc);
		} else {
			gc.fillText("Check the CPU is running in pipelined mode to view this window", realW / 2, realH / 2);
		}
	}

	// ***Utilities***

	private void drawBorderedRectangle(GraphicsContext gc, double x, double y, double w, double h) {
		gc.fillRect(x, y, w, h);
		gc.strokeRect(x, y, w, h);
	}

	private void drawBorderedCircle(GraphicsContext gc, double x, double y, double w, double h) {
		gc.fillOval(x, y, w, h);
		gc.strokeOval(x, y, w, h);
	}

	// Thanks to http://stackoverflow.com/a/4129754
	private static Color getColor(Address address) {
		if (address == null) return Color.RED.brighter();

		int hex = address.getValue();
		// int r = (hex & 0xFF0000) >> 16;
		// int g = (hex & 0xFF00) >> 8;
		// int b = (hex & 0xFF);

		Color colors[] = { Color.ORANGE, Color.AQUA, Color.LAWNGREEN, Color.CORNFLOWERBLUE };
		return colors[(hex % 16) / 4];
	}

	private static String getShortName(Address address) {
		String hex = address.toString();
		return hex.substring(Math.max(0, hex.length() - 3));
	}

	private String getInfo(Address address) {
		// TODO: make popup monospaced and line the fields up

		CPU cpu = getWindowManager().getCPU();
		Map<Address, Statement> textSegment = cpu.getProgram().textSegment;

		if (textSegment.containsKey(address)) {
			Statement s = textSegment.get(address);
			int lineNum = cpu.getProgram().lineNumbers.getOrDefault(address, -1);

			// @formatter:off
			// TODO: (matt) working on it...
			return String.format(
				  "       Statement: %s%n"
				+ "         Address: %s%n"
				+ "Instruction type: %s%n"
				+ "     Line number: %d",
					s.toString(), address.toString(), s.getInstruction(), lineNum);
			// @formatter:on
		} else {
			return "" + "Address: " + address + "\n" + "Does not point to an instruction";
		}
	}

}
