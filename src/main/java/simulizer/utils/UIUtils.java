package simulizer.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Utilities for UI related utilities
 */
public class UIUtils {

	public static void showErrorDialog(String title, String message) {
		showErrorDialog(title, title, message);
	}

	public static void showErrorDialog(String title, String header, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(message);
		alert.show();
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
			callback.accept(file);
		}
	}
}

