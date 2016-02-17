package simulizer.ui.windows;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import simulizer.assembler.representation.Register;
import simulizer.simulation.data.representation.Word;
import simulizer.ui.interfaces.InternalWindow;

public class Registers extends InternalWindow {
	private TableView<Data> table = new TableView<Data>();

	public void refreshData() {
		ObservableList<Data> data = FXCollections.observableArrayList();
		for (Register r : Register.values()) {
			String value = "";
			try {
				Word[] registers = getWindowManager().getRegisters();
				for (byte b : registers[0].getWord()) {
					value += b;
				}
			} catch (NullPointerException e) {
				value = "EMPTY";
			}
			data.add(new Data(r.getName(), value));
		}
		table.setItems(data);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void ready() {
		TableColumn<Data, String> register = new TableColumn<Data, String>("Register");
		register.setCellValueFactory(new PropertyValueFactory<Data, String>("name"));
		TableColumn<Data, String> value = new TableColumn<Data, String>("Value");
		value.setCellValueFactory(new PropertyValueFactory<Data, String>("value"));
		refreshData();
		table.getColumns().addAll(register, value);
		table.setEditable(false);
		getContentPane().getChildren().add(table);
		super.ready();
	}

	public static class Data {

		private String name;
		private String value;

		public Data(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

	}

}
