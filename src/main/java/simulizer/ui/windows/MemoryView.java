package simulizer.ui.windows;

import java.io.IOException;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
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

	public MemoryView() {
		setTitle("Memory View");
	}

	@Override
	public void ready() {
		super.ready();

		// This cannot be in the constructor, otherwise a stackoverflow error
		try {
			BorderPane pane = (BorderPane) FXMLLoader.load(getClass().getResource("/fxml/MemoryVisualiser.fxml"));
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
		});
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
		dialog.setResultConverter(dialogButton -> {
			String address = fldAddress.getText();
			String length = fldLength.getText();

			if (dialogButton == btnOK && !address.isEmpty() && !length.isEmpty())
				return new Pair<>(address, Integer.valueOf(length));
			else
				return null;
		});

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
