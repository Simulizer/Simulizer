package simulizer.ui.windows;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import simulizer.assembler.representation.Register;
import simulizer.simulation.cpu.CPUChangedListener;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.simulation.listeners.RegisterChangedMessage;
import simulizer.simulation.listeners.SimulationListener;
import simulizer.ui.interfaces.InternalWindow;

/**
 * Provides a visual representation of what each Register contains
 * 
 * @author Michael
 *
 */
public class Registers extends InternalWindow implements CPUChangedListener {
	private TableView<Data> table = new TableView<Data>();
	private CPU cpu;
	private RegisterListener listener = new RegisterListener();

	public Registers() {
		widthProperty().addListener((o, old, newValue) -> {
			int numColumns = table.getColumns().size();
			for (TableColumn<Data, ?> column : table.getColumns()) {
				column.setPrefWidth(getWidth() / numColumns);
			}
		});
		table.setCursor(Cursor.DEFAULT);
	}

	/**
	 * Refreshes the table data
	 */
	public void refreshData() {
		ObservableList<Data> data = FXCollections.observableArrayList();
		int i = 0;
		for (Register r : Register.values()) {
			String hex = "", unsigned = "", signed = "";
			try {
				byte[] contents = getWindowManager().getCPU().getRegisters()[i].getWord();
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
		Platform.runLater(() -> table.setItems(data));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void ready() {
		getWindowManager().addCPUChangedListener(this);

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

	@Override
	public void close() {
		getWindowManager().removeCPUChangedListener(this);
		super.close();
	}

	/**
	 * Data is a row in the table
	 * 
	 * @author Michael
	 *
	 */
	public static class Data {

		private final String name, hex, unsigned, signed;

		public Data(String name, String hex, String unsigned, String signed) {
			this.name = name;
			this.hex = hex;
			this.unsigned = unsigned;
			this.signed = signed;
		}

		/**
		 * @return register name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return hex value
		 */
		public String getHex() {
			return hex;
		}

		/**
		 * @return unsigned value
		 */
		public String getUnsigned() {
			return unsigned;
		}

		/**
		 * @return signed value
		 */
		public String getSigned() {
			return signed;
		}

	}

	private class RegisterListener extends SimulationListener {
		@Override
		public void processRegisterChangedMessage(RegisterChangedMessage m) {
			// TODO: Be less wasteful, only update changed register
			refreshData();
		}
	}

	@Override
	public void cpuChanged(CPU cpu) {
		if (this.cpu != null)
			cpu.unregisterListener(listener);
		this.cpu = cpu;
		if (cpu != null)
			cpu.registerListener(listener);
	}

}
