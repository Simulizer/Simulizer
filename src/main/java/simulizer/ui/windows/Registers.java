package simulizer.ui.windows;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import simulizer.assembler.representation.Register;
import simulizer.ui.interfaces.InternalWindow;

public class Registers extends InternalWindow {
	private TableView<Data> table = new TableView<Data>();

	@SuppressWarnings("unchecked")
	public Registers() {
		TableColumn<Data, String> register = new TableColumn<Data, String>("Register");
		register.setCellValueFactory(new PropertyValueFactory<Data, String>("name"));
		TableColumn<Data, String> value = new TableColumn<Data, String>("Value");
		value.setCellValueFactory(new PropertyValueFactory<Data, String>("value"));
		init();
		table.getColumns().addAll(register, value);
		table.setEditable(false);
		getContentPane().getChildren().add(table);
	}

	private void init() {
		ObservableList<Data> data = FXCollections.observableArrayList();
		for (Register r : Register.values()) {
			data.add(new Data(r.getName(), "00000000"));
		}
		table.setItems(data);
	}

	public static class Data {
		private final SimpleStringProperty name;
		private final SimpleStringProperty value;

		public Data(String name, String value) {
			this.name = new SimpleStringProperty(name);
			this.value = new SimpleStringProperty(value);
		}
		public String getName() {
			return name.get();
		}

		public String getValue() {
			return value.get();
		}

	}

}
