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
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Pair;
import simulizer.simulation.cpu.user_interaction.IOStream;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.utils.ThreadUtils;
import simulizer.utils.UIUtils;

/**
 * Provides Input/Output for SIMP programs and output for javascript debug/error messages
 * 
 * @author Michael
 *
 */
public class Logger extends InternalWindow implements Observer {
	private ScheduledExecutorService flush = Executors.newSingleThreadScheduledExecutor(new ThreadUtils.NamedThreadFactory("Logger"));
	private static final long BUFFER_TIME = 20;
	private volatile boolean callUpdate = true;

	private TextField input = new TextField();
	private Button submit;

	private CountDownLatch cdl = new CountDownLatch(1);
	private String lastInput = "";
	private boolean lastInputCancelled = false;

	private TabPane tabPane;
	private boolean[] ioChanged;
	private final StringBuilder[] logs;
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
			if (input.isFocused() && e.getCode() == KeyCode.ENTER)
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
		if (!submit.isDisable()) {
			lastInput = input.getText();
			if (!lastInput.equals("")) {
				input.setText("");
				logs[tabPane.getSelectionModel().getSelectedIndex()].append(lastInput).append("\n");
				callUpdate = true;
				cdl.countDown();
			}
		} else {
			if (input.getText().toLowerCase().equals("i code better when i'm drunk"))
				getWindowManager().motionBlur();
		}
	}


	@Override
	public void setToDefaultDimensions() {
		setNormalisedDimentions(0.8, 0.5, 0.2, 0.5);
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
		for (StringBuilder log : logs)
			log.setLength(0);
		for (TextArea output : outputs)
			output.setText("");
	}

	/**
	 * @return the user entered string (or null if cancelled)
	 */
	public String nextMessage() {
		try {
			if (emphasise)
				Platform.runLater(this::emphasise);
			submit.setDisable(false);
			synchronized (this) { // cannot create new latch and count down at the same time
				cdl = new CountDownLatch(1);
				lastInputCancelled = false;
			}
			cdl.await();
			submit.setDisable(true);
		} catch (InterruptedException e) {
			UIUtils.showExceptionDialog(e);
		}

		if (lastInputCancelled) {
			return null;
		} else {
			return lastInput;
		}
	}

	public synchronized void cancelNextMessage() {
		cdl.countDown();
		lastInputCancelled = true;
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
