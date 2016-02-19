package simulizer.ui;

import simulizer.simulation.listeners.ExecuteStatementMessage;
import simulizer.simulation.listeners.SimulationListener;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.windows.CodeEditor;

/**
 * Listen to messages from the simulation which concern the UI
 */
public class UISimulationListener extends SimulationListener {
	public WindowManager wm;

	public UISimulationListener(WindowManager wm) {
		this.wm = wm;
	}

	@Override public void processExecuteStatementMessage(ExecuteStatementMessage m) {
		CodeEditor code = (CodeEditor) wm.findInternalWindow(WindowEnum.CODE_EDITOR);
		int lineNum = wm.getCPU().getProgram().lineNumbers.get(m.statementAddress);
		code.highlightCurrentLine(lineNum);
	}
}
