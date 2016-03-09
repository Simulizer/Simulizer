package simulizer.ui.windows;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Pair;
import simulizer.simulation.cpu.user_interaction.IOStream;
import simulizer.ui.interfaces.InternalWindow;

public class Logger extends InternalWindow implements Observer {
	private ScheduledExecutorService flush = Executors.newSingleThreadScheduledExecutor();
	private static final long BUFFER_TIME = 20;
	private volatile boolean callUpdate = true;

	private TextField input = new TextField();
	private Button submit;

	private CountDownLatch cdl = new CountDownLatch(1);
	private String lastInput = "";

	private TabPane tabPane;
	private boolean[] ioChanged;
	private StringBuilder[] logs;
	private TextArea[] outputs;

	private boolean emphasise = true;

	public Logger() {
		setTitle("Program I/O");

		ioChanged = new boolean[IOStream.values().length];
		logs = new StringBuilder[IOStream.values().length];
		outputs = new TextArea[IOStream.values().length];
		GridPane pane = new GridPane();

		tabPane = new TabPane();
		tabPane.setCursor(Cursor.DEFAULT);
		for (int i = 0; i < IOStream.values().length; i++) {
			Tab tab = new Tab();
			tab.setText(IOStream.values()[i].toString());
			tab.setClosable(false);

			TextArea output = new TextArea();
			output.setEditable(false);
			output.textProperty().addListener((e) -> output.setScrollTop(Double.MAX_VALUE));
			tab.setContent(output);

			ioChanged[i] = false;
			outputs[i] = output;
			logs[i] = new StringBuilder();

			tabPane.getTabs().add(tab);
		}
		tabPane.getSelectionModel().selectedItemProperty().addListener(e -> tabPane.getSelectionModel().getSelectedItem().setGraphic(null));
		GridPane.setHgrow(tabPane, Priority.ALWAYS);
		GridPane.setVgrow(tabPane, Priority.ALWAYS);
		pane.add(tabPane, 0, 0, 2, 1);

		// Input TextField
		GridPane.setHgrow(input, Priority.ALWAYS);
		pane.add(input, 0, 1);

		// Submit button
		submit = new Button();
		submit.setText("Enter");
		submit.setOnAction((e) -> submitText());
		submit.setDisable(true);
		addEventHandler(KeyEvent.ANY, (e) -> {
			if (input.isFocused() && e.getCode() == KeyCode.ENTER && !submit.isDisable())
				submitText();
		});
		input.focusedProperty().addListener((e) -> {
			if (!input.isFocused())
				requestFocus();
		});
		pane.add(submit, 1, 1);

		widthProperty().addListener((e) -> pane.setPrefWidth(getContentPane().getWidth()));
		heightProperty().addListener((e) -> pane.setPrefHeight(getContentPane().getHeight()));

		getContentPane().getChildren().add(pane);
	}

	private void submitText() {
		lastInput = input.getText();
		if (!lastInput.equals("")) {
			input.setText("");
			logs[tabPane.getSelectionModel().getSelectedIndex()].append(lastInput + "\n");
			callUpdate = true;
			cdl.countDown();
		}
	}

	@Override
	public void ready() {
		getWindowManager().getIO().addObserver(this);
		emphasise = (boolean) getWindowManager().getSettings().get("logger.emphasise");

		flush.scheduleAtFixedRate(() -> {
			if (callUpdate) {
				Platform.runLater(() -> {
					synchronized (logs) {
						callUpdate = false;
						for (int i = 0; i < outputs.length; i++) {
							Tab t = tabPane.getTabs().get(i);
							if (!t.isSelected() && ioChanged[i])
								t.setGraphic(new ImageView("file:notify.png"));
							outputs[i].setText(logs[i].toString());
						}
					}
				});
			}
		} , 0, BUFFER_TIME, TimeUnit.MILLISECONDS);

		super.ready();
	}

	@Override
	public void close() {
		super.close();
		getWindowManager().getIO().deleteObserver(this);
		flush.shutdown();
	}

	public void clear() {
		lastInput = "";
		for (int i = 0; i < outputs.length; i++)
			outputs[i].setText("");
	}

	public String nextMessage() {
		try {
			if (emphasise)
				Platform.runLater(this::emphasise);
			submit.setDisable(false);
			cdl = new CountDownLatch(1);
			cdl.await();
			submit.setDisable(true);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return lastInput;
	}

	public void cancelNextMessage() {
		cdl.countDown();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void update(Observable o, Object message) {
		synchronized (logs) {
			if (!callUpdate)
				callUpdate = true;
			Pair<IOStream, String> pair = (Pair<IOStream, String>) message;
			ioChanged[pair.getKey().getID()] = true;
			logs[pair.getKey().getID()].append(pair.getValue());
		}

	}
}
