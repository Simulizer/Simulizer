package simulizer.ui.components;

import java.util.Map;

import javafx.application.Platform;
import simulizer.Simulizer;
import simulizer.assembler.representation.Address;
import simulizer.assembler.representation.Program;
import simulizer.lowlevel.models.PipelineHistoryModel;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.messages.AnnotationMessage;
import simulizer.simulation.messages.PipelineStateMessage;
import simulizer.simulation.messages.SimulationListener;
import simulizer.simulation.messages.SimulationMessage;
import simulizer.ui.WindowManager;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.windows.Editor;
import simulizer.ui.windows.PipelineView;

/**
 * Listen to messages from the simulation which concern the UI
 */
public class UISimulationListener extends SimulationListener {
	public WindowManager wm;
	long startTime;

	public UISimulationListener(WindowManager wm) {
		this.wm = wm;
	}

	@Override
	public void processSimulationMessage(SimulationMessage m) {
		switch (m.detail) {
			case SIMULATION_STARTED: {
				startTime = System.currentTimeMillis();

				wm.getAnnotationManager().onStartProgram(wm.getCPU());

				Platform.runLater(() -> wm.getPrimaryStage().setTitle("Simulizer v" + Simulizer.VERSION + " - Simulation Running"));

				wm.getWorkspace().openEditorWithCallback((editor) -> {
					System.out.println("Simulation Started - running '" + editor.getBackingFilename() + "'"
						+ (editor.hasOutstandingChanges() ? " with outstanding changes" : "")
						+ (wm.getCPU().isPipelined() ? " (Pipelined CPU)" : " (Non-Pipelined CPU)"));

					editor.executeMode();
				});

				// Clear the pipeline model when a new simulation starts
				PipelineView pipelineView = (PipelineView) wm.getWorkspace().findInternalWindow(WindowEnum.PIPELINE_VIEW);
				if (pipelineView != null) {
					PipelineHistoryModel pipelineModel = pipelineView.getModel();
					if (pipelineModel != null) pipelineModel.clear();
				}
			}
				break;
			case SIMULATION_INTERRUPTED: {
				System.out.println("Simulation Interrupted");
			}
				break;
			case SIMULATION_STOPPED: {
				System.out.println("Simulation Stopped");
				Platform.runLater(() -> wm.getPrimaryStage().setTitle("Simulizer v" + Simulizer.VERSION));

				// TODO: check if the application is closing because this sometimes causes "not a JavaFX thread" exception
				wm.getAnnotationManager().onEndProgram();

				System.out.println("Total annotations fired: " + count);
				long duration = System.currentTimeMillis() - startTime;
				long ticks = wm.getCPU().getClock().getTicks();
				System.out.println("Total time: " + (duration / 1000.0) + " seconds");
				System.out.println("Total ticks: " + ticks);
				if (ticks != 0) { // this is actually possible (.text;main:nop)
					System.out.println("Average time per tick: " + (duration / ticks) + " ms");
				}

				final Editor e = (Editor) wm.getWorkspace().findInternalWindow(WindowEnum.EDITOR);
				if (e != null) {
					Platform.runLater(e::editMode);
				}
			}
				break;
			default:
				break;
		}
	}

	int count = 0;

	@Override
	public void processAnnotationMessage(AnnotationMessage m) {
		// the annotations should all be completed before moving on to the next cycle
		haltSimulation();
		count++;
		wm.getAnnotationManager().processAnnotationMessage(m);
		releaseSimulation();
	}

	private void highlightAddresses(Address fetch, Address decode, Address execute) {
		final Editor e = (Editor) wm.getWorkspace().findInternalWindow(WindowEnum.EDITOR);

		if (e != null) {
			CPU cpu = wm.getCPU();

			if (cpu != null) {
				Program p = cpu.getProgram();

				if (p != null) {
					Map<Address, Integer> lineNums = p.lineNumbers;

					int fetchL = lineNums.getOrDefault(fetch, -1);
					int decodeL = lineNums.getOrDefault(decode, -1);
					int executeL = lineNums.getOrDefault(execute, -1);

					Platform.runLater(() -> e.highlightPipeline(fetchL, decodeL, executeL));
				}
			}
		}
	}

	@Override
	public void processPipelineStateMessage(PipelineStateMessage m) {
		highlightAddresses(m.getFetched(), m.getDecoded(), m.getExecuted());

		// Update the pipeline model
		// TODO have the model in a central place so that the view
		// can sync up with it when it opens.
		PipelineView pipelineView = (PipelineView) wm.getWorkspace().findInternalWindow(WindowEnum.PIPELINE_VIEW);
		if (pipelineView != null && wm.getCPU().isPipelined()) {
			pipelineView.getModel().processPipelineStateMessage(m);
		}
	}
}
