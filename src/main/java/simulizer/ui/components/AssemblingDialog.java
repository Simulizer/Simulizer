package simulizer.ui.components;

import java.time.Duration;

import org.reactfx.util.FxTimer;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.messages.Message;
import simulizer.simulation.messages.SimulationListener;
import simulizer.simulation.messages.SimulationMessage;

public class AssemblingDialog extends Alert {
	private String contentText = "Your program is being assembled, please wait ";

	public AssemblingDialog(CPU cpu) {
		super(AlertType.INFORMATION);

		setTitle("Information Dialog");
		setHeaderText("Assembling");

		setContentText(contentText);

		Platform.runLater(this::show);

		cpu.registerListener(new AssemblingFinishedListener(cpu));

		FxTimer.runPeriodically(Duration.ofMillis(500), () ->
				setContentText(getNext(getContentText())));
	}

	public String getNext(String current) {
		int count = 0;
		for (int i = 0; i < current.length(); ++i)
			if (current.charAt(i) == '.') ++count;

		int newNum = (count + 1) % 4;
		String svar = contentText;

		for (int i = 0; i < newNum; ++i)
			svar += ".";

		return svar;
	}

	public void closeDown() {
		Platform.runLater(() -> ((Button) getDialogPane().lookupButton(ButtonType.OK)).fire());
	}

	private class AssemblingFinishedListener extends SimulationListener {
		CPU cpu;
		public AssemblingFinishedListener(CPU cpu) {
			this.cpu = cpu;
		}

		@Override
		public void processSimulationMessage(SimulationMessage m) {
			if(m.detail == SimulationMessage.Detail.PROGRAM_LOADED) {
				closeDown();
				cpu.unregisterListener(this);
			}
		}
	}
}
