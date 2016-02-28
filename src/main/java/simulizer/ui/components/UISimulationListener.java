package simulizer.ui.components;

import java.util.Map;

import javafx.application.Platform;
import simulizer.assembler.representation.Address;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.listeners.AnnotationMessage;
import simulizer.simulation.listeners.ExecuteStatementMessage;
import simulizer.simulation.listeners.SimulationListener;
import simulizer.ui.WindowManager;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.windows.AceEditor;

import javax.script.ScriptException;

/**
 * Listen to messages from the simulation which concern the UI
 */
public class UISimulationListener extends SimulationListener {
	public WindowManager wm;

	public UISimulationListener(WindowManager wm) {
		this.wm = wm;
	}

	@Override public void processAnnotationMessage(AnnotationMessage m) {
		wm.getHLVisManager().processAnnotation(m.annotation);
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
					// editor.setReadOnly(true);
					Platform.runLater(() -> {
						AceEditor editor = (AceEditor) wm.getWorkspace().findInternalWindow(WindowEnum.ACE_EDITOR);
						if (editor != null)
							editor.gotoLine(lineNum-1);
					});
				}
			}
		}
	}
}
