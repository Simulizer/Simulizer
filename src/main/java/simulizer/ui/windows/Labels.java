package simulizer.ui.windows;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.utils.TemporaryObserver;
import simulizer.utils.UIUtils;

/**
 * Shows the current labels in the code editor.
 *
 * @author Kelsey McKenna
 *
 */
public class Labels extends InternalWindow implements TemporaryObserver {
	private TableView<Label> table = new TableView<>();
	private Editor editor;

	public Labels() {
		widthProperty().addListener((o, old, newValue) -> {
			int numColumns = table.getColumns().size();
			for (TableColumn<Label, ?> column : table.getColumns()) {
				column.setPrefWidth(getWidth() / numColumns);
			}
		});

		// Jump to the label on click
		table.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			Label label = table.getSelectionModel().getSelectedItem();
			if (label != null) {
				refreshEditor();
				if (editor != null) {
					Platform.runLater(() -> {
						editor.gotoLine(label.getLine() - 1);
						editor.findNext(label.label); // Now highlight the label
					});
				}
			}
		});

		// Fix table cursor
		table.setCursor(Cursor.DEFAULT);
	}

	/**
	 * If the current editor is null, then it tries to get hold of the current editor, and then tries to
	 * add this as an observer.
	 */
	private void refreshEditor() {
		if (editor == null) {
			editor = (Editor) getWindowManager().getWorkspace().findInternalWindow(WindowEnum.EDITOR);
			if (editor != null) editor.addObserver(this);
		}
	}

	@Override
	public void setToDefaultDimensions() {
		setNormalisedDimentions(0.8, 0.5, 0.2, 0.5);
	}

	@Override
	public void update() {
		refreshData();
	}

	@Override
	public void stopObserving() {
		editor = null;
		table.setItems(FXCollections.observableArrayList());
	}

	/**
	 * Refreshes the data in the table
	 */
	public void refreshData() {
		ObservableList<Label> labels = FXCollections.observableArrayList();

		refreshEditor();
		if (editor != null) {
			final String text = Editor.getText();
			if (text == null) return;

			Thread labelGetting = new Thread(() -> getLabels(labels, text));

			try {
				labelGetting.start();
				labelGetting.join();
				Platform.runLater(() -> table.setItems(labels));
			} catch (InterruptedException e) {
				UIUtils.showExceptionDialog(e);
			}
		}
	}

	/**
	 * Populates the given list of labels with the list of labes in the given text
	 *
	 * @param text
	 *            the text to analyse
	 */
	private static void getLabels(ObservableList<Label> answer, String text) {
		Pattern r = Pattern.compile("\\s*\\b[a-zA-Z0-9_]*\\s*[:]");

		// For finding other occurrences of labels: [^#]*\bLABEL\b

		try (Scanner reader = new Scanner(text)) {
			for (int lineNum = 1; reader.hasNext(); ++lineNum) {
				String line = reader.nextLine();

				while (!line.isEmpty()) {
					int indexOfComment = line.indexOf("#");
					Matcher matcher = r.matcher(line);

					if (matcher.find()) {
						String label = matcher.group(0);
						assert (label != null);
						label = label.trim().substring(0, label.trim().length() - 1); // cut off the colon

						if (indexOfComment < 0 || matcher.start() < indexOfComment)
							answer.add(new Label(label, lineNum));

						line = line.substring(matcher.end());
					} else break;
				}
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void ready() {
		TableColumn<Label, String> label = new TableColumn<>("Label");
		label.setCellValueFactory(new PropertyValueFactory<>("label"));

		TableColumn<Label, Integer> line = new TableColumn<>("Line");
		line.setCellValueFactory(new PropertyValueFactory<>("line"));

		refreshData();
		table.getColumns().addAll(label, line);
		table.setEditable(false);

		BorderPane pane = new BorderPane();
		setContentPane(pane);
		pane.setCenter(table);
		pane.setCursor(Cursor.DEFAULT);

		Button btnNext = new ActionButton("Next", Editor::findNextRegex);
		Button btnPrevious = new ActionButton("Previous", Editor::findPreviousRegex);
		Button btnAll = new ActionButton("Select All", Editor::findAllRegex);

		HBox buttonContainer = new HBox();
		buttonContainer.getChildren().addAll(btnPrevious, btnNext, btnAll);

		pane.setBottom(buttonContainer);
		super.ready();
	}

	private class ActionButton extends Button {
		public ActionButton(String text, Action action) {
			super(text);

			setMaxWidth(Double.MAX_VALUE);
			HBox.setHgrow(this, Priority.ALWAYS);

			setOnAction(e -> {
				refreshEditor();

				if (editor != null) {
					Label selectedLabel = table.getSelectionModel().getSelectedItem();
					if (selectedLabel != null) action.run(editor, "[^#]*\\b" + selectedLabel.getLabel() + "\\b");
				}
			});
		}
	}

	private interface Action {
		void run(Editor editor, String s);
	}

	public static class Label {
		private String label;
		private int line;

		public Label(String label, int line) {
			this.label = label;
			this.line = line;
		}

		public String getLabel() {
			return label;
		}

		public Integer getLine() {
			return line;
		}

		@Override
		public String toString() {
			return String.format("(%s,%d)", label, line);
		}
	}

}
