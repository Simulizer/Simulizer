package simulizer.ui.components;

import java.util.Map;

import javafx.application.Platform;
import simulizer.assembler.representation.Address;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.listeners.AnnotationMessage;
import simulizer.simulation.listeners.ExecuteStatementMessage;
import simulizer.simulation.listeners.SimulationListener;
import simulizer.simulation.listeners.SimulationMessage;
import simulizer.ui.WindowManager;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.windows.Editor;

/**
 * Listen to messages from the simulation which concern the UI
 */
public class UISimulationListener extends SimulationListener {
	public WindowManager wm;

	public UISimulationListener(WindowManager wm) {
		this.wm = wm;
	}

	@Override public void processSimulationMessage(SimulationMessage m) {
		switch(m.detail) {
			case SIMULATION_STARTED: {
				wm.getAnnotationManager().onStartProgram(wm.getCPU());

				wm.getWorkspace().openEditorWithCallback((editor) -> {
					System.out.println("Simulation Started - running '" +
							editor.getCurrentFile().getName() + "'" +
							(editor.hasOutstandingChanges() ? " with outstanding changes" : "")
					);

					editor.executeMode();
				});
			} break;
			case SIMULATION_INTERRUPTED: {
				System.out.println("Simulation Interrupted");
			} break;
			case SIMULATION_STOPPED: {
				System.out.println("Simulation Stopped");

				//TODO: check if the application is closing because this sometimes causes "not a JavaFX thread" exception
				wm.getAnnotationManager().onEndProgram();

				System.out.println("Total annotations fired: " + count);

				wm.getWorkspace().openEditorWithCallback(Editor::editMode);
			} break;
			default:break;
		}
	}

	int count = 0;
	@Override public void processAnnotationMessage(AnnotationMessage m) {
		count++;
		wm.getAnnotationManager().processAnnotationMessage(m);
	}

	@Override
	public void processExecuteStatementMessage(ExecuteStatementMessage m) {
		// TODO clean up the simulation so less things become null
		if (wm.getCPU() != null) {
			CPU cpu = wm.getCPU();
			if (cpu.getProgram() != null) {
				Map<Address, Integer> lineNums = cpu.getProgram().lineNumbers;
				if (lineNums.containsKey(m.statementAddress)) {
					int lineNum = cpu.getProgram().lineNumbers.get(m.statementAddress);

					wm.getWorkspace().openEditorWithCallback((editor) ->
							editor.highlightPipeline(-1, -1, lineNum));
				}
			}
		}
	}
}
