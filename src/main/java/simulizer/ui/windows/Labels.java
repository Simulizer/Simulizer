package simulizer.ui.windows;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.shape.SVGPath;
import simulizer.ui.components.CurrentFile;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.utils.TemporaryObserver;
import simulizer.utils.ThreadUtils;
import simulizer.utils.UIUtils;

/**
 * Shows the current labels in the code editor along with their corresponding line numbers.
 *
 * @author Kelsey McKenna
 * @author mbway
 *
 */
public class Labels extends InternalWindow {
	private TableView<Label> table = new TableView<>();
    private volatile int programTextHash; // hash of the file content that the labels window refers to
	private final ScheduledExecutorService programTextPolling;

	public Labels() {
		// Jump to the label on click
		table.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			Label label = table.getSelectionModel().getSelectedItem();
			if (label != null) {
				Editor editor = Editor.getEditor();
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

		programTextPolling = Executors.newSingleThreadScheduledExecutor(
				new ThreadUtils.NamedThreadFactory("Labels-Polling"));
        programTextPolling.scheduleAtFixedRate(this::refreshData, 0, 1, TimeUnit.SECONDS);
	}

	@Override
	public void close() {
		programTextPolling.shutdownNow();
		super.close();
	}

	@Override
	public void setToDefaultDimensions() {
		setNormalisedDimentions(0.8, 0.5, 0.2, 0.5);
	}

	/**
	 * Refreshes the data in the table
	 */
	private void refreshData() {
		ObservableList<Label> labels = FXCollections.observableArrayList();

        final String text = CurrentFile.getCurrentText();
        if (text == null || text.length() == 0) return;

		int textHash = text.hashCode();

		if(textHash == programTextHash)
			return;

		programTextHash = textHash;

		System.out.println("updating labels " + System.currentTimeMillis());

		getLabels(labels, text);
        Platform.runLater(() -> table.setItems(labels));
	}

	/**
	 * Populates the given list of labels with the list of labes in the given text
	 *
	 * @param text
	 *            the text to analyse
	 */
	private static void getLabels(ObservableList<Label> answer, String text) {
		Pattern r = Pattern.compile("^\\s*\\b[a-zA-Z0-9_]*\\s*[:]");

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

						if (indexOfComment < 0 || matcher.start() < indexOfComment) answer.add(new Label(label, lineNum));

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
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // columns auto-fit

		BorderPane pane = new BorderPane();
		setContentPane(pane);
		pane.setCenter(table);
		pane.setCursor(Cursor.DEFAULT);

		Button btnNext = new ActionButton("", Editor::findNextRegex);
		Button btnPrevious = new ActionButton("", Editor::findPreviousRegex);

		btnPrevious.setGraphic(UIUtils.getLeftArrow());
		btnNext.setGraphic(UIUtils.getRightArrow());

		Button btnAll = new ActionButton("Select All", Editor::findAllRegex);

		HBox buttonContainer = new HBox();
		buttonContainer.getChildren().addAll(btnPrevious, btnNext, btnAll);

		pane.setBottom(buttonContainer);
		super.ready();
	}

	/**
	 * Represents a generic action button for triggering searches for other occurrences of labels.
	 *
	 * @author Kelsey McKenna
	 *
	 */
	private class ActionButton extends Button {
		ActionButton(String text, Action action) {
			super(text);

			setMaxWidth(Double.MAX_VALUE);
			HBox.setHgrow(this, Priority.ALWAYS);

			setOnAction(e -> {
                Editor editor = Editor.getEditor();
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

	/**
	 * Represents a label in the code. It records the label name and its line number.
	 *
	 * @author Kelsey McKenna
	 *
	 */
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
