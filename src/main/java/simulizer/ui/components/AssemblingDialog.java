package simulizer.ui.components;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import simulizer.Simulizer;
import simulizer.simulation.messages.SimulationListener;
import simulizer.simulation.messages.SimulationMessage;
import simulizer.ui.WindowManager;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.windows.Editor;
import simulizer.utils.ThreadUtils;
import simulizer.utils.UIUtils;

public class AssemblingDialog extends Alert {
	private String contentText = "Your program is being assembled, please wait ";
	private final ScheduledExecutorService executor;
	private final ScheduledFuture<?> updateTask;
	private WindowManager wm;

	private static AssemblingDialog assemblingDialog = null;
	public static void showAssemblingDialog(WindowManager wm) {
		assemblingDialog = new AssemblingDialog(wm);
	}
	public static void closeAssemblingDialog() {
		if (assemblingDialog != null) {
			assemblingDialog.closeDown();
			assemblingDialog = null;
		}
	}

	private AssemblingDialog(WindowManager wm) {
		super(AlertType.INFORMATION);
		initOwner(wm.getPrimaryStage());
		UIUtils.setDialogBoxIcon(this);

		this.wm = wm;

		setTitle("Assembling");
		setHeaderText("Assembling");

		setContentText(contentText);

		Platform.runLater(() -> {
			show();
			getDialogPane().setCursor(Cursor.WAIT);
		});

		wm.getCPU().registerListener(new AssemblingFinishedListener());

		executor = Executors.newSingleThreadScheduledExecutor(
				new ThreadUtils.NamedThreadFactory("Assembling-Dialog"));
		updateTask = executor.scheduleAtFixedRate(() ->
				Platform.runLater(() -> setContentText(getNext(getContentText())))
		, 0, 500, TimeUnit.MILLISECONDS);
	}

	private String getNext(String current) {
		int count = 0;
		for (int i = 0; i < current.length(); ++i)
			if (current.charAt(i) == '.') ++count;

		int newNum = (count + 1) % 4;
		String svar = contentText;

		for (int i = 0; i < newNum; ++i)
			svar += ".";

		return svar;
	}

	private void closeDown() {
		updateTask.cancel(true);
		executor.shutdownNow();
		Platform.runLater(() -> {
			close();

			// to fix an annoyance with focus not returning properly and
			// when it does, having the wrong cursor
			wm.getPrimaryStage().requestFocus();
			Editor e = (Editor)wm.getWorkspace().findInternalWindow(WindowEnum.EDITOR);
			if(e != null) {
				e.requestFocus();
				e.setCursor(Cursor.DEFAULT);
			}
		});
	}

	private class AssemblingFinishedListener extends SimulationListener {
		@Override
		public void processSimulationMessage(SimulationMessage m) {
			if(m.detail == SimulationMessage.Detail.PROGRAM_LOADED) {
				closeDown();
				wm.getCPU().unregisterListener(this);
			}
		}
	}
}
