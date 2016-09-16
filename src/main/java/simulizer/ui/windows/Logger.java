package simulizer.ui.windows;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
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
import javafx.scene.text.Font;
import simulizer.simulation.cpu.user_interaction.IOStream;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.utils.FileUtils;
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
	private static final long BUFFER_TIME = 20; // milliseconds
	private volatile boolean callUpdate = true;

	private TextField input = new TextField();
	private Button submit;

	private final Semaphore inputWait;
	private String lastInput = "";
	private volatile boolean lastInputCancelled = false;

	private TabPane tabPane;
	private boolean[] ioChanged;
	private TextArea[] outputs;

	private boolean emphasise = true;
	private final ImageView notifyIcon = new ImageView(new Image(FileUtils.getResourcePath("/img/notify.png")));

	public Logger() {
		ioChanged = new boolean[IOStream.values().length];
		outputs = new TextArea[IOStream.values().length];
		GridPane pane = new GridPane();

		inputWait = new Semaphore(0, true);

		tabPane = new TabPane();
		tabPane.setCursor(Cursor.DEFAULT);
		for (int i = 0; i < IOStream.values().length; i++) {
			Tab tab = new Tab();
			tab.setText(IOStream.values()[i].toString());
			tab.setClosable(false);

			TextArea output = new TextArea();
			output.setEditable(false);
			output.textProperty().addListener((e) -> output.setScrollTop(Double.MAX_VALUE));
			output.setOnMouseClicked((e) -> input.requestFocus());
			tab.setContent(output);

			ioChanged[i] = false;
			outputs[i] = output;

			tabPane.getTabs().add(tab);
		}
		tabPane.getSelectionModel().selectedItemProperty().addListener(e -> {
			tabPane.getSelectionModel().getSelectedItem().setGraphic(null);
			ioChanged[tabPane.getSelectionModel().getSelectedIndex()] = false;
		});
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
		getEventManager().addEventHandler(KeyEvent.ANY, (e) -> {
			if (input.isFocused() && e.getCode() == KeyCode.ENTER)
				submitText();
		});
		input.focusedProperty().addListener((e) -> {
			if (!input.isFocused())
				requestFocus();
		});
		pane.add(submit, 1, 1);

		getContentPane().widthProperty().addListener(e -> pane.setPrefWidth(getContentPane().getWidth()));
		getContentPane().heightProperty().addListener(e -> pane.setPrefHeight(getContentPane().getHeight()));

		input.getStyleClass().add("logger-input");
		submit.getStyleClass().add("logger-submit");

		getContentPane().getChildren().add(pane);
	}

	private void submitText() {
		if (!submit.isDisable()) {
			lastInput = input.getText();
			if (!lastInput.equals("")) {
				input.setText("");
				inputWait.release();
			}
		} else {
			// @formatter:off
			String code = input.getText().trim().toLowerCase();
			if (code.equals("i code better when i'm drunk")  || 
				code.equals("i code better when im drunk")   ||
				code.equals("i code better when i am drunk")) {
				getWindowManager().motionBlur();
				input.setText("");
			}
			// @formatter:on
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
		int fontSize = (int) getWindowManager().getSettings().get("logger.font-size");

		flush.scheduleAtFixedRate(() -> {
			if (callUpdate) {
				Platform.runLater(() -> {
					synchronized (ioChanged) {
						callUpdate = false;
						for (IOStream i : IOStream.values()) {
							String log = getWindowManager().getIO().getLog(i);
							Tab t = tabPane.getTabs().get(i.getID());
							if (!t.isSelected() && ioChanged[i.getID()] && !log.equals(""))
								t.setGraphic(notifyIcon);
							outputs[i.getID()].setText(log);
							outputs[i.getID()].setFont(new Font(fontSize));
							ioChanged[i.getID()] = false;
						}
					}
				});
			}
		}, 0, BUFFER_TIME, TimeUnit.MILLISECONDS);

		super.ready();
	}

	@Override
	public void close() {
		super.close();
		getWindowManager().getIO().deleteObserver(this);
		flush.shutdown();
	}

	/**
	 * Clears all the logs
	 */
	public void clear() {
		lastInput = "";
		for (TextArea output : outputs)
			output.setText("");
		for (int i = 0; i < ioChanged.length; i++)
			ioChanged[i] = false;
	}

	/**
	 * @return the user entered string (or null if cancelled)
	 */
	public String nextMessage(IOStream stream) {
		try {
			synchronized (ioChanged) {
				ioChanged[stream.getID()] = true;
			}
			if (emphasise)
				Platform.runLater(() -> {
					// if already focused. Display a more subtle emphasis
					if (input.isFocused())
						emphasise(1.0); // scale factor 1 => no scale
					else {
						emphasise(1.1);
						input.requestFocus();
					}
				});
			submit.setDisable(false);
			lastInputCancelled = false;
			inputWait.acquire(); // released once cancelled or text received
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

	/**
	 * Cancels the request for a message
	 */
	public synchronized void cancelNextMessage() {
		lastInputCancelled = true;
		if (inputWait.hasQueuedThreads())
			inputWait.release();
	}

	@Override
	public void update(Observable o, Object iostream) {
		synchronized (ioChanged) {
			if (!callUpdate)
				callUpdate = true;
			ioChanged[((IOStream) iostream).getID()] = true;
		}

	}
}
