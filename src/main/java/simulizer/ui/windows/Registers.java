package simulizer.ui.windows;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Cursor;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import simulizer.assembler.representation.Register;
import simulizer.simulation.cpu.CPUChangedListener;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.simulation.messages.RegisterChangedMessage;
import simulizer.simulation.messages.SimulationListener;
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
		for (Register r : Register.values())
			data.add(createData(r));
		Platform.runLater(() -> table.setItems(data));
	}

	private Data createData(Register r) {
		String hex, unsigned, signed;
		try {
			byte[] contents = getWindowManager().getCPU().getRegisters()[r.getID()].getWord();
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
		return new Data(r.getID(), r.getName(), hex, unsigned, signed);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void ready() {
		getWindowManager().addCPUChangedListener(this);
		getWindowManager().getCPU().registerListener(listener);

		TableColumn<Data, String> register = new TableColumn<>("Register");
		register.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<Data, String> hex = new TableColumn<>("Hexadecimal");
		hex.setCellValueFactory(new PropertyValueFactory<>("hex"));

		TableColumn<Data, String> unsigned = new TableColumn<>("Unsigned");
		unsigned.setCellValueFactory(new PropertyValueFactory<>("unsigned"));

		TableColumn<Data, String> signed = new TableColumn<>("Signed");
		signed.setCellValueFactory(new PropertyValueFactory<>("signed"));

		refreshData();
		table.getColumns().addAll(register, hex, unsigned, signed);
		table.setEditable(false);

		getContentPane().getChildren().add(table);
		super.ready();
	}

	@Override
	public void setToDefaultDimensions() {
		setNormalisedDimentions(0.5, 0.0, 0.5, 0.5);
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
		private final int id;
		private final String name, hex, unsigned, signed;

		public Data(int id, String name, String hex, String unsigned, String signed) {
			this.id = id;
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
			try {
				FilteredList<Data> items = table.getItems().filtered(d -> d.id != m.registerChanged.getID());
				ObservableList<Data> list = FXCollections.observableArrayList(items);
				list.add(createData(m.registerChanged));
				list.sort((a, b) -> a.id - b.id);
				Platform.runLater(() -> table.setItems(list));
			} catch (Exception e) {
				e.printStackTrace();
			}
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
