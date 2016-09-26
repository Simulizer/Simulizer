package simulizer.ui.windows.help;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import simulizer.ui.components.SearchBox;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.Searchable;

/**
 * Creates a simple help window. This window contains a table with two columns
 * 
 * @author Michael
 *
 */
class SimpleTablePairWindow extends InternalWindow {

	private TableView<Data> table = new TableView<>();
	private SearchBox<Data> searchBox;

	@SuppressWarnings("unchecked")
	SimpleTablePairWindow(String columnName1, String columnName2, double weight) {
		TableColumn<Data, String> col1 = new TableColumn<>(columnName1);
		col1.setCellValueFactory(new PropertyValueFactory<>("col1"));

		TableColumn<Data, String> col2 = new TableColumn<>(columnName2);
		col2.setCellValueFactory(new PropertyValueFactory<>("col2"));

		table.getColumns().addAll(col1, col2);
		table.setEditable(false);
		table.setCursor(Cursor.DEFAULT);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		searchBox = new SearchBox<Data>(table);

		VBox vbox = new VBox();
		VBox.setVgrow(table, Priority.ALWAYS);
		vbox.getChildren().addAll(searchBox, table);
		getContentPane().getChildren().addAll(vbox);

		// Autoresize columns
		table.widthProperty().addListener((o, old, newValue) -> {
			col1.setPrefWidth(weight * table.getWidth());
			col2.setPrefWidth((1 - weight) * table.getWidth());
		});
	}

	/**
	 * @param data
	 *            the data for the table
	 */
	public void setData(String[][] data) {
		ObservableList<Data> tableData = FXCollections.observableArrayList();
		for (String[] row : data) {
			tableData.add(new Data(row[0], row[1]));
		}
		searchBox.setItems(tableData);
	}

	// requirements for observable:
	// class public. public 'get' methods corresponding to the value passed to the PropertyValueFactory
	@SuppressWarnings("unused")
	public static class Data implements Searchable {
		private final String col1, col2;

		Data(String col1, String col2) {
			this.col1 = col1;
			this.col2 = col2;
		}

		public String getCol1() {
			return col1;
		}

		public String getCol2() {
			return col2;
		}

		@Override
		public float matchesSearchTerm(String searchTerm) {
			float col1Val = col1.contains(searchTerm) ? (float) searchTerm.length() / col1.length() : 0;
			float col2Val = col2.contains(searchTerm) ? (float) searchTerm.length() / col2.length() : 0;
			return 10 * col1Val + col2Val;
		}
	}
}
