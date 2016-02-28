package simulizer.utils;

import javafx.scene.control.Alert;

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
}
