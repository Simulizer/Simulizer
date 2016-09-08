package simulizer.ui.windows.help;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import simulizer.assembler.representation.Register;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.windows.Registers;

/**
 * Register Reference
 * 
 * @author Michael
 * @author mbway
 *
 */
public class RegisterReference extends InternalWindow {

	private TableView<Data> table = new TableView<>();

	// requirements for observable:
	// class public. public 'get' methods corresponding to the value passed to the PropertyValueFactory
	public class Data {
		private String mnemonic;
		private String numeric;
		private String description;

		Data(String mnemonic, String numeric, String description) {
			this.mnemonic = mnemonic;
			this.numeric = numeric;
			this.description = description;
		}

		public String getMnemonic() { return mnemonic; }
		public String getNumeric() { return numeric; }
		public String getDescription() { return description; }
	}

	@SuppressWarnings("unchecked")
	public RegisterReference() {
		TableColumn idCol = new TableColumn("ID");

		TableColumn<Data, String> idCol1 = new TableColumn("Mnemonic");
		idCol1.setCellValueFactory(new PropertyValueFactory<>("mnemonic"));
		idCol1.setPrefWidth(100);

		TableColumn<Data, String> idCol2 = new TableColumn("Numeric");
		idCol2.setCellValueFactory(new PropertyValueFactory<>("numeric"));
		idCol2.setPrefWidth(100);

		idCol.getColumns().addAll(idCol1, idCol2);

		TableColumn<Data, String> descCol = new TableColumn("Description");
		descCol.setCellValueFactory(new PropertyValueFactory<>("description"));


		ObservableList<Data> data = FXCollections.observableArrayList();

		for(Register register : Register.values()) {
			data.add(new Data("$" + register.getName(), "$" + register.getID(), register.getDescription()));
		}

		table.setItems(data);

		table.getColumns().addAll(idCol, descCol);
		table.setEditable(false);
		table.setCursor(Cursor.DEFAULT);
		getContentPane().getChildren().add(table);
	}
}
