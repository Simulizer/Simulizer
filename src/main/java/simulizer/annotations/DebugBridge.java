package simulizer.annotations;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import simulizer.simulation.cpu.user_interaction.IO;

/**
 * A collection of methods for debugging, accessible from annotations
 */
@SuppressWarnings("unused")
public class DebugBridge {
	IO io = null;

	public void log(String string) {
		if(io != null) {
			io.printString(string + "\n");
		} else {
			System.out.println("js: " + string);
		}
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
