package simulizer.annotations;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.simulation.cpu.user_interaction.IOStream;
import simulizer.ui.WindowManager;
import simulizer.utils.UIUtils;

/**
 * A collection of methods for debugging, accessible from annotations
 *
 * @author mbway
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class DebugBridge {
	// package-visible Attributes not visible from JavaScript
	// set package-visible attributes using BridgeFactory
	WindowManager wm = null;
	IO io = null;

	public void log(String string) {
		if(io != null) {
			io.printString(IOStream.DEBUG, "(js) " + string + "\n");
		} else {
			System.out.println("(js) " + string);
		}
	}

	public void assertTrue(boolean condition) {
		if(!condition)
			throw new AssertionError();
	}

	public void alert(String msg) {
		Platform.runLater(() -> UIUtils.showInfoDialog("Javascript Alert", msg));
	}

	public CPU getCPU() {
		return wm.getCPU();
	}
}
