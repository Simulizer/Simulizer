package simulizer.ui.windows.help;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.windows.Registers.Data;

public class SimpleTablePairWindow extends InternalWindow {

	private TableView<Data> table = new TableView<Data>();

	@SuppressWarnings("unchecked")
	public SimpleTablePairWindow(String columnName1, String columnName2) {
		TableColumn<Data, String> col1 = new TableColumn<Data, String>(columnName1);
		col1.setCellValueFactory(new PropertyValueFactory<Data, String>("col1"));

		TableColumn<Data, String> col2 = new TableColumn<Data, String>(columnName2);
		col2.setCellValueFactory(new PropertyValueFactory<Data, String>("col2"));

		table.getColumns().addAll(col1, col2);
		table.setEditable(false);
		getContentPane().getChildren().add(table);
	}
	
	public void setData(String[][] data){
		ObservableList<Data> tableData = FXCollections.observableArrayList();
		for (int i = 0; i < data.length; i++) {
			String[] row = data[i];
			tableData.add(new Data(row[0], row[1]));
		}
		table.setItems(tableData);
	}

	public class Data {
		private final String col1, col2;

		public Data(String col1, String col2) {
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
