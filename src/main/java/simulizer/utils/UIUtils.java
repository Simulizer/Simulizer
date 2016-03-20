package simulizer.utils;

import java.awt.Desktop;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import simulizer.Simulizer;

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


	// from http://stackoverflow.com/a/27983567
	// it is OK if the icon is null, will reset to default icon
	public static void setDialogBoxIcon(Dialog<?> dialog) {
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(Simulizer.getIcon());
	}

	public static void showErrorDialog(String title, String message) {
		showErrorDialog(title, title, message);
	}

	public static void showErrorDialog(String title, String header, String message) {
		System.err.println(title + "\n\t" + header + "\n\t" + message);

		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setResizable(true);
			alert.initOwner(Simulizer.getPrimaryStage());
			alert.setTitle(title);
			setDialogBoxIcon(alert);
			alert.setHeaderText(header);
			alert.setContentText(message);
			alert.show();
		});
	}

	public static void showInfoDialog(String title, String message) {
		showInfoDialog(title, title, message);
	}

	public static void showInfoDialog(String title, String header, String message) {
		System.out.println(title + "\n\t" + header + "\n\t" + message);

		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setResizable(true);
			alert.initOwner(Simulizer.getPrimaryStage());
			alert.setTitle(title);
			setDialogBoxIcon(alert);
			alert.setHeaderText(header);
			alert.setContentText(message);
			alert.show();
		});
	}

	public static void showExceptionDialog(Throwable e) {
		showExceptionDialog(Thread.currentThread(), e);
	}

	private static final LongAdder simultaneousExceptionDialogs = new LongAdder();

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
			Throwable cause = e.getCause();
			pw.write("Cause:   " + cause + "\n");
			if(cause != null) {
				pw.write("  Cause is another exception, see trace below the first.\n");
			}
			pw.write("Message: \"" + e.getMessage() + "\"\n");
			pw.write("\n");
			e.printStackTrace(pw);

			if(cause != null) {
				pw.write("\n\n--  Cause  --\n");
				cause.printStackTrace(pw);
			}
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

			simultaneousExceptionDialogs.increment();
			if(simultaneousExceptionDialogs.intValue() > 5) {
				System.err.println("more exceptions follow but there are now too many to display");
				System.exit(1);
			}
			Platform.runLater(() -> {
				try {

					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setResizable(true);
					alert.initOwner(Simulizer.getPrimaryStage());
					alert.setTitle("Exception");
					setDialogBoxIcon(alert);
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

					alert.setOnCloseRequest((event) -> simultaneousExceptionDialogs.decrement());
					alert.showAndWait();
				} catch(Throwable t) {
					System.err.println("Failed to show exception dialog with another exception:");
					t.printStackTrace(System.err);
				}
			});
		} catch(Throwable t) {
			System.err.println("Failed to show exception dialog with another exception:");
			t.printStackTrace(System.err);
		}
	}

	public static void openTextInputDialog(String title, String header, String message, String defaultText, Consumer<String> callback) {
		TextInputDialog dialog = new TextInputDialog(defaultText);
		dialog.initOwner(Simulizer.getPrimaryStage());
		setDialogBoxIcon(dialog);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(message);

		dialog.showAndWait().ifPresent(callback);
	}
	public static void openIntInputDialog(String title, String header, String message, int defaultVal, Consumer<Integer> callback) {
		TextInputDialog dialog = new TextInputDialog(""+defaultVal);
		dialog.initOwner(Simulizer.getPrimaryStage());
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(message);

		dialog.showAndWait().ifPresent((text) -> {
			int val = defaultVal;
			try {
				val = Integer.parseInt(text);
			} catch(NumberFormatException ignored) {
			}

			callback.accept(val);
		});
	}
	public static void openDoubleInputDialog(String title, String header, String message, double defaultVal, Consumer<Double> callback) {
		TextInputDialog dialog = new TextInputDialog(""+defaultVal);
		dialog.initOwner(Simulizer.getPrimaryStage());
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(message);

		dialog.showAndWait().ifPresent((text) -> {
			double val = defaultVal;
			try {
				val = Double.parseDouble(text);
			} catch(NumberFormatException ignored) {
			}

			callback.accept(val);
		});
	}

	/**
	 * get user confirmation using an OK / Cancel dialog
	 */
	public static boolean confirm(String header, String message) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setResizable(true);
		alert.initOwner(Simulizer.getPrimaryStage());
		alert.setTitle("Confirmation");
		setDialogBoxIcon(alert);
		alert.setHeaderText(header);
		alert.setContentText(message);

		Optional<ButtonType> result = alert.showAndWait();
		return result.get() == ButtonType.OK;
	}

	public static ButtonType confirmYesNoCancel(String header, String message) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setResizable(true);
		alert.initOwner(Simulizer.getPrimaryStage());
		alert.setTitle("Confirmation");
		setDialogBoxIcon(alert);
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

	/**
	 * Attempt to open a URL in the browser
	 * @param url the url to open
	 * @return whether the call succeeded
	 */
	public static boolean openURL(String url) {
		System.out.println("Opening URL: " + url);

		if(!Desktop.isDesktopSupported() ||
		   !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			return false;
		}

		try {
			URI uri = (new URL(url)).toURI();
			AtomicBoolean status = new AtomicBoolean(true);
			Thread browseThread = new Thread(() -> {
				try {
					Desktop.getDesktop().browse(uri);
				} catch (IOException e) {
					status.set(false);
				}
			}, "OpenURL-Thread");
			browseThread.setDaemon(true);
			browseThread.start();

			browseThread.join(1000); // if timeout: Interrupted exception => false

			return status.get();

		} catch (IOException e) {
			return false;
		} catch (URISyntaxException e) {
			return false;
		} catch (InterruptedException e) {
			return false;
		}
	}
}

