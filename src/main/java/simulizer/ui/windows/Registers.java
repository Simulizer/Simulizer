package simulizer.ui.windows;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import simulizer.assembler.representation.Register;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.simulation.data.representation.Word;
import simulizer.ui.interfaces.InternalWindow;

public class Registers extends InternalWindow {
	private TableView<Data> table = new TableView<Data>();

	public void refreshData() {
		ObservableList<Data> data = FXCollections.observableArrayList();
		int i = 0;
		for (Register r : Register.values()) {
			String value = "";
			try {
				Word[] registers = getWindowManager().getRegisters();
				value = Long.toHexString(DataConverter.decodeAsUnsigned(registers[i].getWord()));
				while (value.length() < 8)
					value = "0" + value;
				value = "0x" + value;
			} catch (NullPointerException e) {
				value = "EMPTY";
			}
			data.add(new Data(r.getName(), value));
			i++;
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
