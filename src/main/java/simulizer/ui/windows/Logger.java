package simulizer.ui.windows;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import simulizer.ui.interfaces.InternalWindow;

public class Logger extends InternalWindow implements Observer {
	private static final long BUFFER_TIME = 20;

	private TextArea output = new TextArea();
	private TextField input = new TextField();
	private Button submit;

	private CountDownLatch cdl = new CountDownLatch(1);
	private String lastInput = "", buffer = "";
	private long lastUpdate = 0;
	private volatile boolean callUpdate = true;

	private boolean emphasise = true;

	public Logger() {
		setTitle("Program I/O");

		GridPane pane = new GridPane();

		// Output Console
		output.setEditable(false);
		output.textProperty().addListener((e) -> output.setScrollTop(Double.MAX_VALUE));
		GridPane.setHgrow(output, Priority.ALWAYS);
		GridPane.setVgrow(output, Priority.ALWAYS);
		pane.add(output, 0, 0, 2, 1);

		// Input TextField
		GridPane.setHgrow(input, Priority.ALWAYS);
		pane.add(input, 0, 1);

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
			output.appendText(lastInput + "\n");
			cdl.countDown();
		}
	}

	@Override
	public void ready() {
		getWindowManager().getIO().addObserver(this);
		emphasise = (boolean) getWindowManager().getSettings().get("logger.emphasise");
		super.ready();
	}

	@Override
	public void close() {
		super.close();
		getWindowManager().getIO().deleteObserver(this);
	}

	public void clear() {
		lastInput = "";
		output.setText("");
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
	public void update(Observable o, Object message) {
		synchronized (buffer) {
			if (!callUpdate && (buffer == ""))
				callUpdate = true;
			buffer += (String) message;
		}

		if (callUpdate && System.currentTimeMillis() - lastUpdate > BUFFER_TIME) {
			Platform.runLater(() -> {
				synchronized (buffer) {
					callUpdate = false;
					output.appendText(buffer);
					buffer = "";
					lastUpdate = System.currentTimeMillis();
				}
			});
		}
	}
}
