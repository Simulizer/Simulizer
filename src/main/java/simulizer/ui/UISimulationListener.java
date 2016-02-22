package simulizer.ui;

import simulizer.assembler.representation.Address;
import simulizer.assembler.representation.Instruction;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.listeners.ExecuteStatementMessage;
import simulizer.simulation.listeners.SimulationListener;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.windows.CodeEditor;

import java.util.Map;

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
		//TODO clean up the simulation so less things become null
		if(wm.getCPU() != null) {
			CPU cpu = wm.getCPU();
			if(cpu.getProgram() != null) {
				if(cpu.getProgram().lineNumbers != null) {
					Map<Address, Integer> lineNums = cpu.getProgram().lineNumbers;
					if(lineNums.containsKey(m.statementAddress)) {
						int lineNum = cpu.getProgram().lineNumbers.get(m.statementAddress);
						code.highlightCurrentLine(lineNum-1);
					}
				}
			}
		}
	}
}
