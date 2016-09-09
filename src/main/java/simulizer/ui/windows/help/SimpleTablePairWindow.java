package simulizer.ui.windows.help;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import simulizer.ui.interfaces.InternalWindow;

/**
 * Creates a simple help window. This window contains a table with two columns
 * 
 * @author Michael
 *
 */
class SimpleTablePairWindow extends InternalWindow {

	private TableView<Data> table = new TableView<>();

	@SuppressWarnings("unchecked")
	SimpleTablePairWindow(String columnName1, String columnName2) {
		TableColumn<Data, String> col1 = new TableColumn<>(columnName1);
		col1.setCellValueFactory(new PropertyValueFactory<>("col1"));

		TableColumn<Data, String> col2 = new TableColumn<>(columnName2);
		col2.setCellValueFactory(new PropertyValueFactory<>("col2"));

		table.getColumns().addAll(col1, col2);
		table.setEditable(false);
		table.setCursor(Cursor.DEFAULT);
		getContentPane().getChildren().add(table);
	}

	/**
	 * @param data
	 *            the data for the table
	 */
	public void setData(String[][] data) {
		ObservableList<Data> tableData = FXCollections.observableArrayList();
		for(String[] row : data) {
			tableData.add(new Data(row[0], row[1]));
		}
		table.setItems(tableData);
	}

	// requirements for observable:
	// class public. public 'get' methods corresponding to the value passed to the PropertyValueFactory
	@SuppressWarnings("unused")
	public static class Data {
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
	}
}
