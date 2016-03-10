package simulizer.utils;

import java.awt.Font;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Utilities for UI related utilities
 */
public class UIUtils {

	/**
	 * A form of assertion to make sure that something is called from the javaFX thread
	 */
	public static void assertFXThread() {
		if(!Platform.isFxApplicationThread()) {
			throw new IllegalStateException("Not on FX application thread; currentThread = " + Thread.currentThread().getName());
		}
	}

	public static void showErrorDialog(String title, String message) {
		showErrorDialog(title, title, message);
	}

	public static void showErrorDialog(String title, String header, String message) {
		System.err.println(title + "\n\t" + header + "\n\t" + message);

		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(message);
		alert.show();
	}

	public static void showExceptionDialog(Throwable e) {
		showExceptionDialog(Thread.currentThread(), e);
	}

	/**
	 * Display the stack trace of an exception.
	 * Inspired by: http://code.makery.ch/blog/javafx-dialogs-official/
	 * @param e the exception to display
	 */
	public static void showExceptionDialog(Thread where, Throwable e) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			pw.write("Thread:  " + where.getName() + "\n");
			pw.write("At:      " + LocalDateTime.now().toString().replace("T", " ") + "\n");
			pw.write("Cause:   " + e.getCause() + "\n");
			pw.write("Message: " + e.getMessage() + "\n");
			pw.write("\n");
			e.printStackTrace(pw);
			final String exceptionText = sw.toString();

			// in case JavaFX is very broken
			System.err.print(exceptionText);

			// write to a file log
			BufferedWriter log = null;
			try {
				log = new BufferedWriter(new FileWriter("./exceptions.log", true));
				log.write(exceptionText + "\n\n\n");
				log.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				if(log != null) {
					try {
						log.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}

			Platform.runLater(() -> {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Exception");
				alert.setHeaderText("Something went wrong with Simulizer.");
				alert.setContentText("The details of the problem are below.\nPlease contact the developers with this information:");
				alert.getDialogPane().setPrefSize(720, 480);

				Label label = new Label("Stacktrace:");

				TextArea stacktrace = new TextArea(exceptionText);
				stacktrace.setEditable(false);
				stacktrace.setWrapText(true);
				stacktrace.setFont(javafx.scene.text.Font.font(Font.MONOSPACED));

				stacktrace.setMaxWidth(Double.MAX_VALUE);
				stacktrace.setMaxHeight(Double.MAX_VALUE);
				GridPane.setVgrow(stacktrace, Priority.ALWAYS);
				GridPane.setHgrow(stacktrace, Priority.ALWAYS);

				GridPane expContent = new GridPane();
				expContent.setMaxWidth(Double.MAX_VALUE);
				expContent.setHgap(4);
				expContent.add(label, 0, 0);
				expContent.add(stacktrace, 0, 1);

				alert.getDialogPane().setExpandableContent(expContent);
				alert.getDialogPane().expandedProperty().set(true); // set expanded by default

				alert.showAndWait();
			});
		} catch(Throwable t) {
			System.err.println("Failed to handle exception with another exception:");
			t.printStackTrace(System.err);
		}
	}

	/**
	 * get user confirmation using an OK / Cancel dialog
	 */
	public static boolean confirm(String header, String message) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText(header);
		alert.setContentText(message);

		Optional<ButtonType> result = alert.showAndWait();
		return result.get() == ButtonType.OK;
	}

	public static ButtonType confirmYesNoCancel(String header, String message) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText(header);
		alert.setContentText(message);

		alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

		Optional<ButtonType> res = alert.showAndWait();
		if(res.isPresent()) {
			return res.get();
		} else {
			return ButtonType.CANCEL;
		}
	}

	public static File saveFileSelector(String title, Stage parent, File directory, FileChooser.ExtensionFilter... filter) {
		final FileChooser fc = new FileChooser();
		fc.setInitialDirectory(directory);
		fc.setTitle(title);
		fc.getExtensionFilters().addAll(filter);
		return fc.showSaveDialog(parent);
	}

	public static File openFileSelector(String title, Stage parent, File directory, FileChooser.ExtensionFilter... filter) {
		// Set the file chooser to open at the user's last directory
		final FileChooser fc = new FileChooser();
		fc.setInitialDirectory(directory);
		fc.setTitle(title);
		fc.getExtensionFilters().addAll(filter);
		return fc.showOpenDialog(parent);
	}

	public static void promptSaveAs(Stage parent, Consumer<File> callback) {
		File file = saveFileSelector("Save an assembly file", parent, new File("code"), new FileChooser.ExtensionFilter("Assembly files *.s", "*.s"));
		if(file != null) {
			if (!file.getName().endsWith(".s"))
				file = new File(file.getAbsolutePath() + ".s");
			
			callback.accept(file);
		}
	}
}

