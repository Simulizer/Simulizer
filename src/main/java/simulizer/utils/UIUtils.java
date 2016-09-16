package simulizer.utils;

import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import simulizer.GuiMode;
import simulizer.Simulizer;

/**
 * Utilities for UI related utilities
 *
 * @author mbway
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

	public static <V> V runLaterWithResult(Callable<V> c) throws Exception {
        if(Platform.isFxApplicationThread()) {
            return c.call();
        } else {
            FutureTask<V> f = new FutureTask<>(c);
            Platform.runLater(f);
            return f.get();
        }
	}

	/**
	 * path extracted by saving an SVG image (after Path>"Object to path" in Inkspace) and examining the path:d attribute
	 * @return an SVG left arrow suitable for a button graphic
	 */
	public static SVGPath getLeftArrow() {
		SVGPath left = new SVGPath();
		left.setContent("m 32.779871,87.970679 8.179517,-3.271553 8.179518,-3.271553 0,6.543106 -10e-7,6.543108 -8.179517,-3.271554 z");
		return left;
	}

	/**
	 * path extracted by saving an SVG image (after Path>"Object to path" in Inkspace) and examining the path:d attribute
	 * @return an SVG right arrow suitable for a button graphic
	 */
	public static SVGPath getRightArrow() {
		SVGPath right = new SVGPath();
		right.setContent("m 49.138906,87.970679 -8.179517,-3.271553 -8.179518,-3.271553 0,6.543106 10e-7,6.543108 8.179517,-3.271554 z");
        return right;
	}

	/**
	 * set the icon of a dialog box to the Simulizer logo
	 * @param dialog the dialog box to set
	 */
	// from http://stackoverflow.com/a/27983567
	// it is OK if the icon is null, will reset to default icon
	public static void setDialogBoxIcon(Dialog<?> dialog) {
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(GuiMode.getIcon());
	}

	/**
	 * show a dialog box detailing a non-critical error
	 */
	public static void showErrorDialog(String title, String message) {
		showErrorDialog(title, title, message);
	}

	/**
	 * show a dialog box detailing a non-critical error
	 * @param title the title for the dialog box
	 * @param header the header for the dialog box
	 * @param message the message to display in the dialog box
	 */
	public static void showErrorDialog(String title, String header, String message) {
		System.err.println(title + "\n\t" + header + "\n\t" + message);

		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setResizable(true);
            Stage parent = GuiMode.getPrimaryStage(); // owner is null if JavaFX not fully loaded yet
			if(parent != null && parent.getOwner() != null) {
				alert.initOwner(parent.getOwner());
			}
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

	/**
	 * show a dialog box providing some information to the user
	 * @param title the title for the dialog box
	 * @param header the header for the dialog box
	 * @param message the message to display in the dialog box
	 */
	private static void showInfoDialog(String title, String header, String message) {
		System.out.println(title + "\n\t" + header + "\n\t" + message);

		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setResizable(true);
			Stage parent = GuiMode.getPrimaryStage(); // owner is null if JavaFX not fully loaded yet
			if(parent != null && parent.getOwner() != null) {
				alert.initOwner(parent.getOwner());
			}
			alert.setTitle(title);
			setDialogBoxIcon(alert);
			alert.setHeaderText(header);
			alert.setContentText(message);
			alert.show();
		});
	}


	/**
	 * show a dialog box showing the stack trace of an exception.
     * this method should be used instead of printStackTrace as it provides more
     * information to the user and also logs the exception to a file
	 */
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
		assert(Simulizer.hasGUI());

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
			Writer log = null;
			try {
                log = FileUtils.getUTF8FileAppendWriter("./exceptions.log");
				log.write(exceptionText + "\n\n\n");
				log.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
                FileUtils.quietClose(log);
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
					Stage parent = GuiMode.getPrimaryStage(); // owner is null if JavaFX not fully loaded yet
					if(parent != null && parent.getOwner() != null) {
						alert.initOwner(parent.getOwner());
					}
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

	/**
	 * show a dialog box to input some text
	 */
	public static void openTextInputDialog(String title, String header, String message, String defaultText, Consumer<String> callback) {
		TextInputDialog dialog = new TextInputDialog(defaultText);
		Stage parent = GuiMode.getPrimaryStage(); // owner is null if JavaFX not fully loaded yet
		if(parent != null && parent.getOwner() != null) {
			dialog.initOwner(parent.getOwner());
		}
		setDialogBoxIcon(dialog);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(message);

		dialog.showAndWait().ifPresent(callback);
	}

	/**
	 * show a dialog box to input an integer
	 */
	public static void openIntInputDialog(String title, String header, String message, int defaultVal, Consumer<Integer> callback) {
		TextInputDialog dialog = new TextInputDialog(""+defaultVal);
		Stage parent = GuiMode.getPrimaryStage(); // owner is null if JavaFX not fully loaded yet
		if(parent != null && parent.getOwner() != null) {
			dialog.initOwner(parent.getOwner());
		}
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

	/**
	 * show a dialog box to input a double
	 */
	public static void openDoubleInputDialog(String title, String header, String message, double defaultVal, Consumer<Double> callback) {
		TextInputDialog dialog = new TextInputDialog(""+defaultVal);
		Stage parent = GuiMode.getPrimaryStage(); // owner is null if JavaFX not fully loaded yet
		if(parent != null && parent.getOwner() != null) {
			dialog.initOwner(parent.getOwner());
		}
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
		Stage parent = GuiMode.getPrimaryStage(); // owner is null if JavaFX not fully loaded yet
		if(parent != null && parent.getOwner() != null) {
			alert.initOwner(parent.getOwner());
		}
		alert.setTitle("Confirmation");
		setDialogBoxIcon(alert);
		alert.setHeaderText(header);
		alert.setContentText(message);

		Optional<ButtonType> result = alert.showAndWait();
		return result.isPresent() && result.get() == ButtonType.OK;
	}

	// from http://stackoverflow.com/a/37610648
	private static class FixedOrderButtonDialog extends DialogPane {
		@Override
		protected Node createButtonBar() {
			ButtonBar node = (ButtonBar) super.createButtonBar();
			node.setButtonOrder(ButtonBar.BUTTON_ORDER_NONE);
			return node;
		}
	}

	/**
	 * show a dialog box which asks the user to confirm yes or no
	 */
	public static ButtonType confirmYesNoCancel(String header, String message) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setDialogPane(new FixedOrderButtonDialog());
		alert.setResizable(true);
		Stage parent = GuiMode.getPrimaryStage(); // owner is null if JavaFX not fully loaded yet
		if(parent != null && parent.getOwner() != null) {
			alert.initOwner(parent.getOwner());
		}
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

	/**
	 * show the save dialog box
	 */
	public static File saveFileSelector(String title, Stage parent, File directory, FileChooser.ExtensionFilter... filter) {
		final FileChooser fc = new FileChooser();
		fc.setInitialDirectory(directory);
		fc.setTitle(title);
		fc.getExtensionFilters().addAll(filter);
		return fc.showSaveDialog(parent);
	}

	/**
	 * show the load dialog box
	 */
	public static File openFileSelector(String title, Stage parent, File directory, FileChooser.ExtensionFilter... filter) {
		// Set the file chooser to open at the user's last directory
		final FileChooser fc = new FileChooser();
		fc.setInitialDirectory(directory);
		fc.setTitle(title);
		fc.getExtensionFilters().addAll(filter);
		return fc.showOpenDialog(parent);
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

