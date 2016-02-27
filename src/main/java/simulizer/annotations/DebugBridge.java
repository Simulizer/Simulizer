package simulizer.annotations;

import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * A collection of methods for debugging, accessible from annotations
 */
@SuppressWarnings("unused")
public class DebugBridge {
	public void debugPrint(String string) {
		System.out.println(string);
	}

	public void alert(String msg) {
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("JavaScript Alert");
			alert.setHeaderText("JavaScript Alert");
			alert.setContentText(msg);
			alert.show();
		});
	}
}
