package simulizer.ui.windows;

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
	private TableView<Data> table = new TableView<Data>();
	private CPU cpu;
	private RegisterListener listener = new RegisterListener();
	private ValueType valueType = ValueType.UNSIGNED;
	private TableColumn<Data, String> valueCol;

	public Registers() {
		widthProperty().addListener((o, old, newValue) -> {
			int numColumns = table.getColumns().size();
			for (TableColumn<Data, ?> column : table.getColumns())
				column.setPrefWidth(getWidth() / numColumns);
		});
		table.setCursor(Cursor.DEFAULT);
	}

	/**
	 * Refreshes the table data
	 */
	public void refreshData() {
		synchronized (table) {
			ObservableList<Data> data = FXCollections.observableArrayList();
			for (Register r : Register.values())
				data.add(createData(r));
			try {
				ThreadUtils.platformRunAndWait(() -> table.setItems(data));
			} catch (Throwable e) {
				UIUtils.showExceptionDialog(e);
			}
		}
	}

	private Data createData(Register r) {
		byte[] contents = getWindowManager().getCPU().getRegisters()[r.getID()].getWord();
		return new Data(r.getID(), r.getName(), contents);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void ready() {
		// Add Listeners
		getWindowManager().addCPUChangedListener(this);
		getWindowManager().getCPU().registerListener(listener);

		// Create Register column
		TableColumn<Data, String> register = new TableColumn<>("Register");
		register.setCellValueFactory(new PropertyValueFactory<>("name"));

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
				refreshData();
			});
			item.setToggleGroup(toggleGroup);
			menu.getItems().add(item);
		}
		valueCol.setContextMenu(menu);

		refreshData();
		table.getColumns().addAll(register, valueCol);
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
	public class Data {
		private final int id;
		private final byte[] contents;
		private final String name;

		public Data(int id, String name, byte[] contents) {
			this.id = id;
			this.name = name;
			this.contents = contents;
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
			try {
				synchronized (table) {
					FilteredList<Data> items = table.getItems().filtered(d -> d.id != m.registerChanged.getID());
					ObservableList<Data> list = FXCollections.observableArrayList(items);
					list.add(createData(m.registerChanged));
					list.sort((a, b) -> a.id - b.id);
					ThreadUtils.platformRunAndWait(() -> table.setItems(list));
				}
			} catch (Throwable e) {
				UIUtils.showExceptionDialog(e);
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

		private ValueType(String colName) {
			this.colName = colName;
		}

		@Override
		public String toString() {
			return colName;
		}
	}

}
