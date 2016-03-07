package simulizer.ui.windows;

import java.io.IOException;
import java.util.Optional;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import simulizer.ui.interfaces.InternalWindow;

public class MemoryView extends InternalWindow {
	@FXML
	private Label lblScale;
	@FXML
	private Rectangle byteRectangle;
	@FXML
	private Slider sldrByteSize;
	@FXML
	private Button btnLoadRange;

	@FXML
	private ScrollPane paneStack;
	@FXML
	private Canvas canvasStack;

	private Color[] colorScheme = { Color.rgb(255, 38, 34), Color.rgb(15, 154, 255), Color.rgb(204, 204, 33) };

	public MemoryView() {
		setTitle("Memory View");
	}

	@Override
	public void ready() {
		super.ready();

		try {
			// This cannot be in the constructor, otherwise a stackoverflow error
			BorderPane pane = (BorderPane) FXMLLoader.load(getClass().getResource("/fxml/MemoryView.fxml"));
			getContentPane().getChildren().add(pane);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize() {
		double v = 10;
		sldrByteSize.setValue(v);
		byteRectangle.setHeight(Math.max(v / 100. * sldrByteSize.getHeight(), 50));

		sldrByteSize.valueProperty().addListener((o, old, n) -> {
			double h = n.doubleValue() / 100. * sldrByteSize.getHeight();
			byteRectangle.setHeight(h);
			drawTest();
		});

		// TODO replace 40 with the width of the scroll bar (which should be set in CSS)
		// TODO make scroll bar always ON in scroll panes (to make calculations easier)
		canvasStack.widthProperty().bind(paneStack.widthProperty().subtract(20));
		canvasStack.widthProperty().addListener(o -> drawTest());
		Platform.runLater(() -> drawTest());
	}

	private void drawTest() {
		final String HIGH = "HIGH (0xFFF)";
		final String LOW = "LOW (0xFFA)";

		double numBytesToDraw = 3;
		final double byteSize = byteRectangle.getHeight();

		GraphicsContext gc = canvasStack.getGraphicsContext2D();
		final double width = canvasStack.getWidth();
		final double height = numBytesToDraw * (byteSize + 2) + 100;
		canvasStack.setHeight(height);
		gc.clearRect(0, 0, width, height);

		final double x0 = 0.1 * width;
		final double y0 = 30;
		final double blockWidth = 0.8 * width;
		final double delimiterX0 = 0.8 * x0;
		double yTracker = y0;

		// Write HIGH (0xFFF)
		gc.setFill(Color.BLACK);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		gc.fillText(HIGH, width / 2, yTracker);

		yTracker += 15;

		// Draw top delimiter
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(2);
		gc.strokeLine(delimiterX0, yTracker, width - delimiterX0, yTracker);

		++yTracker; // Move down one pixel

		double[] numBytes = { 1, 2 }; // Draw a 1-byte block and then a 2-byte block
		for (int i = 0; i < numBytes.length; ++i) {
			gc.setFill(colorScheme[i]);
			gc.fillRect(x0, yTracker, blockWidth, numBytes[i] * byteSize);
			yTracker += numBytes[i] * byteSize;

			// Draw a bottom line for all blocks except the bottom one
			if (i < numBytes.length - 1) {
				++yTracker;
				gc.strokeLine(x0 + 1, yTracker, x0 + blockWidth - 1, yTracker);
				++yTracker;
			}
		}

		// Draw the bottom delimiter
		yTracker += 1;
		gc.strokeLine(delimiterX0, yTracker, width - delimiterX0, yTracker);
		yTracker += 1;

		yTracker += 10;
		gc.setFill(Color.BLACK);
		gc.fillText(LOW, width / 2, yTracker);
	}

	@FXML
	public void loadRange() {
		// Thanks to http://code.makery.ch/blog/javafx-dialogs-official/

		// TODO figure out how to set the owner of the dialog
		// so it works on dual monitors

		// Set up the dialog
		Dialog<Pair<String, Integer>> dialog = new Dialog<>();
		dialog.setTitle("Load Arbitrary Range");
		dialog.setHeaderText("Header text");
		dialog.setContentText("Content text");

		// Add OK and cancel buttons
		ButtonType btnOK = new ButtonType("OK", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(btnOK, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		// Add the labels and fields
		TextField fldAddress = new TextField();
		fldAddress.setPromptText("0xFFA");
		NumberTextField fldLength = new NumberTextField();
		fldLength.setPromptText("10");

		grid.add(new Label("Address:"), 0, 0);
		grid.add(fldAddress, 1, 0);
		grid.add(new Label("Length:"), 0, 1);
		grid.add(fldLength, 1, 1);

		// Add the grid to the dialog
		dialog.getDialogPane().setContent(grid);

		// Extract the data and return it when the user presses OK
		dialog.setResultConverter(dialogButton -> {
			String address = fldAddress.getText();
			String length = fldLength.getText();

			if (dialogButton == btnOK && !address.isEmpty() && !length.isEmpty()) return new Pair<>(address, Integer.valueOf(length));
			else return null;
		});

		// Get the result
		Optional<Pair<String, Integer>> result = dialog.showAndWait();
		result.ifPresent(e -> {
			String address = e.getKey();
			Integer length = e.getValue();
			System.out.printf("Load Range: address=%s, length=%d%n", address, length);
		});
	}

	// Thanks to http://stackoverflow.com/a/18959399
	private class NumberTextField extends TextField {
		@Override
		public void replaceText(int start, int end, String text) {
			if (validate(text)) {
				super.replaceText(start, end, text);
			}
		}

		@Override
		public void replaceSelection(String text) {
			if (validate(text)) {
				super.replaceSelection(text);
			}
		}

		private boolean validate(String text) {
			return text.matches("[0-9]*");
		}
	}
}
