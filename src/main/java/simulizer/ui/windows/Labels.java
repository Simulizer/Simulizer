package simulizer.ui.windows;

import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;

/**
 * Shows the current labels in the code editor.
 *
 * @author Kelsey McKenna
 *
 */
public class Labels extends InternalWindow {
	private TableView<Label> table = new TableView<Label>();

	public Labels() {
		widthProperty().addListener((o, old, newValue) -> {
			int numColumns = table.getColumns().size();
			for (TableColumn<Label, ?> column : table.getColumns()) {
				column.setPrefWidth(getWidth() / numColumns);
			}
		});

		table.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			int line = table.getSelectionModel().getSelectedItem().getLine();
			((AceEditor) getWindowManager().getWorkspace().findInternalWindow(WindowEnum.ACE_EDITOR)).gotoLine(line);;
		});
	}

	public void refreshData() {
		AceEditor editor = (AceEditor) getWindowManager().getWorkspace().findInternalWindow(WindowEnum.ACE_EDITOR);

		ObservableList<Label> labels = FXCollections.observableArrayList();
		populateLabels(labels, editor.getText());
		table.setItems(labels);
	}

	private void populateLabels(ObservableList<Label> list, String text) {
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

				if (isLabel) {
					list.add(new Label(s.substring(0, indexOfColon), line));
				}
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

		getContentPane().getChildren().add(table);
		super.ready();
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
