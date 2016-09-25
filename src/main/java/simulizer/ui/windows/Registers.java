package simulizer.ui.windows;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import simulizer.assembler.representation.Register;
import simulizer.simulation.cpu.CPUChangedListener;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.simulation.messages.PipelineStateMessage;
import simulizer.simulation.messages.RegisterChangedMessage;
import simulizer.simulation.messages.SimulationListener;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.utils.ThreadUtils;
import simulizer.utils.UIUtils;

/**
 * Provides a visual representation of what each Register contains
 * 
 * @author Michael
 *
 */
public class Registers extends InternalWindow implements CPUChangedListener {
	private final TableView<Data> table = new TableView<>();
	private CPU cpu;
	private RegisterListener listener = new RegisterListener();
	private ValueType valueType = ValueType.UNSIGNED;
	private TableColumn<Data, String> valueCol;
	private Timer refreshTimer;

	private final Set<Integer> changedRegisters = new HashSet<>();

	public Registers() {
		getEventManager().addPropertyListener(widthProperty(), (o, old, newValue) -> {
			int numColumns = table.getColumns().size();
			double width = getContentPane().getWidth();
			for (TableColumn<Data, ?> column : table.getColumns())
				column.setPrefWidth(width / numColumns);
		});
		table.setCursor(Cursor.DEFAULT);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	/**
	 * Refreshes the table data
	 */
	private void refreshTable() {
		synchronized (table) {
			ObservableList<Data> data = FXCollections.observableArrayList();
			for (Register r : Register.values())
				data.add(createData(r));
			// Adding special registers
			data.add(new Data(9997, "pc"));
			data.add(new Data(9998, "hi"));
			data.add(new Data(9999, "lo"));
			try {
				ThreadUtils.platformRunAndWait(() -> table.setItems(data));
			} catch (Throwable e) {
				UIUtils.showExceptionDialog(e);
			}
		}
	}

	private void refreshRegisters() {
		FilteredList<Data> changed;
		synchronized (table) {
			synchronized (changedRegisters) {
				if (changedRegisters.size() == 0)
					return;
				changed = table.getItems().filtered(d -> changedRegisters.contains(d.id));
				changedRegisters.clear();
			}
			changed.forEach(Data::refresh);
		}
	}

	private Data createData(Register r) {
		return new Data(r.getID(), "$" + r.getName());
	}

	@Override
	@SuppressWarnings("unchecked")
	public void ready() {
		// Add Listeners
		getWindowManager().addCPUChangedListener(this);
		cpu = getWindowManager().getCPU();
		cpu.registerListener(listener);

		// Create Name column
		TableColumn<Data, String> registerName = new TableColumn<>("Name");
		registerName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		// Create Number column
		TableColumn<Data, String> registerID = new TableColumn<>("ID");
		registerID.setCellValueFactory(new PropertyValueFactory<>("id"));

		// Create value column
		valueCol = new TableColumn<>(valueType.toString());
		valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));

		// Right Click on value column menu
		ContextMenu menu = new ContextMenu();
		ToggleGroup toggleGroup = new ToggleGroup();
		for (ValueType type : ValueType.values()) {
			RadioMenuItem item = new RadioMenuItem(type.toString());
			item.setSelected(type.equals(valueType));
			item.setOnAction(e -> {
				valueType = type;
				valueCol.setText(type.toString());
				refreshTable();
			});
			item.setToggleGroup(toggleGroup);
			menu.getItems().add(item);
		}
		valueCol.setContextMenu(menu);

		refreshTable();
		table.getColumns().addAll(registerName, registerID, valueCol);
		table.setEditable(false);

		// Refresh registers regularly
		refreshTimer = new Timer(true);
		refreshTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				refreshRegisters();
			}

		}, 0, 30);

		getContentPane().getChildren().add(table);
		super.ready();
	}

	@Override
	public void setToDefaultDimensions() {
		setNormalisedDimentions(0.5, 0.0, 0.5, 0.5);
	}

	@Override
	public void close() {
		cpu.unregisterListener(listener);
		getWindowManager().removeCPUChangedListener(this);
		refreshTimer.cancel();
		super.close();
	}

	/**
	 * Data is a row in the table
	 * 
	 * @author Michael
	 *
	 */
	@SuppressWarnings({ "WeakerAccess", "unused" })
	public class Data {
		private Register reg;
		private final int id;
		private final String name;
		private byte[] contents;
		private SimpleStringProperty value = new SimpleStringProperty();

		public Data(final int id, final String name) {
			this.id = id;
			this.name = name;
			try {
				reg = Register.fromID(id);
			} catch (Exception e) {
				reg = null;
			}
			refresh();
		}

		public void refresh() {
			if (reg != null) // Normal Register
				contents = cpu.getRegister(reg).getBytes();
			else if (id == 9997 && cpu.getProgramCounter() != null) { // pc Register
				contents = ByteBuffer.allocate(4).putInt(cpu.getProgramCounter().getValue()).array();
			} else if (id == 9998) // hi Register
				contents = cpu.getHi().getBytes();
			else if (id == 9999) // lo Register
				contents = cpu.getLo().getBytes();

			value.set(getValue());
		}

		/**
		 * @return register id
		 */
		public String getId() {
			if (id < 9000)
				return "" + id;
			else
				return "";
		}

		/**
		 * @return register name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the register value
		 */
		public String getValue() {
			String output = "EMPTY";
			if (contents != null) {
				switch (valueType) {
					case HEX:
						output = Long.toHexString(DataConverter.decodeAsUnsigned(contents));
						while (output.length() < 8)
							output = "0" + output;
						output = "0x" + output;
						break;

					case SIGNED:
						output = "" + DataConverter.decodeAsSigned(contents);
						break;

					case UNSIGNED:
						output = "" + DataConverter.decodeAsUnsigned(contents);
						break;
				}
			}
			return output;
		}

		public SimpleStringProperty valueProperty() {
			return value;
		}

	}

	/**
	 * Listens for Register Changed events
	 * 
	 * @author Michael
	 *
	 */
	private class RegisterListener extends SimulationListener {
		@Override
		public void processRegisterChangedMessage(RegisterChangedMessage m) {
			synchronized (changedRegisters) {
				if (m.registerChanged == null) {
					// Updating hi/lo
					changedRegisters.add(9998);
					changedRegisters.add(9999);
				} else
					changedRegisters.add(m.registerChanged.getID());
			}
		}

		@Override
		public void processPipelineStateMessage(PipelineStateMessage m) {
			synchronized (changedRegisters) {
				// Updating pc
				changedRegisters.add(9997);
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

	/**
	 * Enum for the different ways the data can be interpreted.
	 * 
	 * @author Michael
	 *
	 */
	private enum ValueType {
		UNSIGNED("Unsigned"), SIGNED("Signed"), HEX("Hexadecimal");

		private final String colName;

		ValueType(String colName) {
			this.colName = colName;
		}

		@Override
		public String toString() {
			return colName;
		}
	}

}
