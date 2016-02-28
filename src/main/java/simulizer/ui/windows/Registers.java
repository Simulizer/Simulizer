package simulizer.ui.windows;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import simulizer.assembler.representation.Register;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.simulation.listeners.Message;
import simulizer.simulation.listeners.SimulationListener;
import simulizer.ui.interfaces.InternalWindow;

public class Registers extends InternalWindow {
	private TableView<Data> table = new TableView<Data>();
	private CPU cpu;

	public Registers() {
		widthProperty().addListener((o, old, newValue) -> {
			int numColumns = table.getColumns().size();
			for (TableColumn<Data, ?> column : table.getColumns()) {
				column.setPrefWidth(getWidth() / numColumns);
			}
		});
		table.setCursor(Cursor.DEFAULT);
	}

	public void refreshData() {
		// Create Listener for Register Changes
		CPU cpu = getWindowManager().getCPU();
		if (cpu != null && this.cpu != cpu) {
			this.cpu = cpu;
			cpu.registerListener(new RegisterListener());
		}

		ObservableList<Data> data = FXCollections.observableArrayList();
		int i = 0;
		for (Register r : Register.values()) {
			String hex = "", unsigned = "", signed = "";
			try {
				byte[] contents = getWindowManager().getRegisters()[i].getWord();
				unsigned = "" + DataConverter.decodeAsUnsigned(contents);
				signed = "" + DataConverter.decodeAsSigned(contents);
				hex = Long.toHexString(DataConverter.decodeAsUnsigned(contents));
				while (hex.length() < 8)
					hex = "0" + hex;
				hex = "0x" + hex;
			} catch (NullPointerException e) {
				hex = "EMPTY";
				unsigned = "EMPTY";
				signed = "EMPTY";
			}
			data.add(new Data(r.getName(), hex, unsigned, signed));
			i++;
		}
		table.setItems(data);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void ready() {
		TableColumn<Data, String> register = new TableColumn<Data, String>("Register");
		register.setCellValueFactory(new PropertyValueFactory<Data, String>("name"));

		TableColumn<Data, String> hex = new TableColumn<Data, String>("Hexadecimal");
		hex.setCellValueFactory(new PropertyValueFactory<Data, String>("hex"));

		TableColumn<Data, String> unsigned = new TableColumn<Data, String>("Unsigned");
		unsigned.setCellValueFactory(new PropertyValueFactory<Data, String>("unsigned"));

		TableColumn<Data, String> signed = new TableColumn<Data, String>("Signed");
		signed.setCellValueFactory(new PropertyValueFactory<Data, String>("signed"));

		refreshData();
		table.getColumns().addAll(register, hex, unsigned, signed);
		table.setEditable(false);

		getContentPane().getChildren().add(table);
		super.ready();
	}

	public static class Data {

		private final String name, hex, unsigned, signed;

		public Data(String name, String hex, String unsigned, String signed) {
			this.name = name;
			this.hex = hex;
			this.unsigned = unsigned;
			this.signed = signed;
		}

		public String getName() {
			return name;
		}

		public String getHex() {
			return hex;
		}

		public String getUnsigned() {
			return unsigned;
		}

		public String getSigned() {
			return signed;
		}

	}

	private class RegisterListener extends SimulationListener {
		@Override
		public void processMessage(Message m) {
			refreshData();
		}
	}

}
