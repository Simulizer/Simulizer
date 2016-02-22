package simulizer.ui.windows;

import java.util.concurrent.CountDownLatch;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.ui.interfaces.InternalWindow;

public class Logger extends InternalWindow implements IO {

	private TextArea output = new TextArea();
	private TextField input = new TextField();
	private String lastInput = "";
	private CountDownLatch cdl = new CountDownLatch(1);

	public Logger() {
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

		// Enter Button
		Button submit = new Button();
		submit.setText("Enter");
		submit.setOnAction((e) -> {
			lastInput = input.getText();
			input.setText("");
			output.appendText(lastInput + "\n");
			cdl.countDown();
		});
		pane.add(submit, 1, 1);

		widthProperty().addListener((e) -> pane.setPrefWidth(getContentPane().getWidth()));
		heightProperty().addListener((e) -> pane.setPrefHeight(getContentPane().getHeight()));

		getContentPane().getChildren().add(pane);
	}

	@Override
	public String readString() {
		try {
			cdl = new CountDownLatch(1);
			cdl.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return lastInput;
	}

	@Override
	public int readInt() {
		try {
			cdl = new CountDownLatch(1);
			cdl.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return Integer.parseInt(lastInput);
	}

	@Override
	public char readChar() {
		try {
			cdl = new CountDownLatch(1);
			cdl.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return lastInput.charAt(0);
	}

	@Override
	public void printString(String str) {
		output.appendText(str);
	}

	@Override
	public void printInt(int num) {
		output.appendText("" + num);
	}

	@Override
	public void printChar(char letter) {
		output.appendText("" + letter);
	}

	public void clear() {
		lastInput = "";
		output.setText("");
	}

}
