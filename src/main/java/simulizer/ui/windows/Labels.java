package simulizer.ui.windows;

import java.util.Scanner;

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
import simulizer.ui.components.TemporaryObserver;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;

/**
 * Shows the current labels in the code editor.
 *
 * @author Kelsey McKenna
 *
 */
public class Labels extends InternalWindow implements TemporaryObserver {
	private TableView<Label> table = new TableView<Label>();
	private AceEditor editor;

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
					editor.gotoLine(label.getLine());
					editor.findNext(label.label); // Now highlight the label
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
			editor = (AceEditor) getWindowManager().getWorkspace().findInternalWindow(WindowEnum.ACE_EDITOR);
			if (editor != null) editor.addObserver(this);
		}
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
			final String text = editor.getText();
			if (text == null) return;

			Thread labelGetting = new Thread(() -> getLabels(labels, text));

			try {
				labelGetting.start();
				labelGetting.join();
				Platform.runLater(() -> table.setItems(labels));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Populates the given list of labels with the list of labes in the given text
	 *
	 * @param list
	 *            the list to populate
	 * @param text
	 *            the text to analyse
	 */
	private static void getLabels(ObservableList<Label> answer, String text) {
		Scanner reader = new Scanner(text);

		for (int line = 1; reader.hasNext(); ++line) {
			String s = reader.nextLine().trim();
			int indexOfColon = s.indexOf(":");

			if (indexOfColon >= 1) {
				boolean isLabel = true;
				for (int i = 0; i < indexOfColon; ++i) {
					String c = "" + s.charAt(i);
					if (" #".contains(c)) {
						isLabel = false;
						break;
					}
				}

				if (isLabel) answer.add(new Label(s.substring(0, indexOfColon), line));
			}
		}

		reader.close();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void ready() {
		TableColumn<Label, String> label = new TableColumn<Label, String>("Label");
		label.setCellValueFactory(new PropertyValueFactory<Label, String>("label"));

		TableColumn<Label, Integer> line = new TableColumn<Label, Integer>("Line");
		line.setCellValueFactory(new PropertyValueFactory<Label, Integer>("line"));

		refreshData();
		table.getColumns().addAll(label, line);
		table.setEditable(false);

		BorderPane pane = new BorderPane();
		setContentPane(pane);
		pane.setCenter(table);

		Button btnNext = new ActionButton("Next", (editor, s) -> editor.findNext(s));
		Button btnPrevious = new ActionButton("Previous", (editor, s) -> editor.findPrevious(s));
		Button btnAll = new ActionButton("Select All", (editor, s) -> editor.findAll(s));

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
					if (selectedLabel != null) action.run(editor, selectedLabel.getLabel());
				}
			});
		}
	}

	private static interface Action {
		public void run(AceEditor editor, String s);
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