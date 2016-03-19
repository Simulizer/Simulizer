package simulizer.ui.components;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import simulizer.simulation.cpu.CPUChangedListener;
import simulizer.simulation.messages.SimulationListener;
import simulizer.simulation.messages.SimulationMessage;
import simulizer.ui.WindowManager;
import simulizer.utils.FileUtils;

/**
 * holds data for the buttons and other controls placed on the menu bar
 * @author mbway
 */
public class MenuBarControls {
	private class ButtonCPUListener extends SimulationListener {
		@Override public void processSimulationMessage(SimulationMessage m) {
			switch(m.detail) {
				case SIMULATION_STARTED:
				case SIMULATION_RESUMED:
				case SIMULATION_STOPPED:
				case SIMULATION_PAUSED: {
					Platform.runLater(() -> {
						updatePlayButton();
						updateResumeSingleButton();
						updateStopButton();
					});
				} break;
				default:break;
			}
		}
	}

	private class ButtonCPUChangedListener implements CPUChangedListener {
		@Override public void cpuChanged(simulizer.simulation.cpu.components.CPU newCPU) {
			cpu = newCPU;
			cpu.registerListener(listener);
		}
	}

	private simulizer.simulation.cpu.components.CPU cpu;
	private final ButtonCPUListener listener;
	private final WindowManager wm;

	private final Button playPauseButton;
	private final ImageView playResting;
	private final ImageView playHover;
	private final ImageView pauseResting;
	private final ImageView pauseHover;

	private final Button resumeSingleButton;
	private final ImageView resumeSingleResting;
	private final ImageView resumeSingleHover;

	private final Button stopButton;
	private final ImageView stopResting;
	private final ImageView stopHover;

	private final Slider clockSpeedSlider;
	private final int sliderMax = 200; // start from 0
	private final double lgClockSpeedMin = Math.log(0.2);
	private final double lgClockSpeedMax = Math.log(2000);
	private long lastClockSpeedSetTime;
	private final Label clockSpeedLabel;


	public MenuBarControls(MainMenuBar menu, WindowManager wm) {
		this.wm = wm;
		cpu = wm.getCPU();
		listener = new ButtonCPUListener();
		ButtonCPUChangedListener changedListener = new ButtonCPUChangedListener();
		wm.addCPUChangedListener(changedListener);
		cpu.registerListener(listener);


		playResting = new ImageView(new Image(FileUtils.getResourcePath("/img/play.png")));
		playHover = new ImageView(new Image(FileUtils.getResourcePath("/img/play-hover.png")));

		pauseResting = new ImageView(new Image(FileUtils.getResourcePath("/img/pause.png")));
		pauseHover = new ImageView(new Image(FileUtils.getResourcePath("/img/pause-hover.png")));

		resumeSingleResting = new ImageView(new Image(FileUtils.getResourcePath("/img/resume-single.png")));
		resumeSingleHover = new ImageView(new Image(FileUtils.getResourcePath("/img/resume-single-hover.png")));

		stopResting = new ImageView(new Image(FileUtils.getResourcePath("/img/stop.png")));
		stopHover = new ImageView(new Image(FileUtils.getResourcePath("/img/stop-hover.png")));



		Menu spacer = new Menu();
		spacer.setDisable(true);
		spacer.setStyle("-fx-padding: 0px 0px 0px 50px");

		playPauseButton = new Button();
		playPauseButton.setOnAction((e) -> playClicked());
		updatePlayButton();
		Menu pausePlayMenu = createButtonMenu(playPauseButton);

		resumeSingleButton = new Button();
		resumeSingleButton.setOnAction((e) -> resumeSingleClicked());
		updateResumeSingleButton();
		Menu resumeSingleMenu = createButtonMenu(resumeSingleButton);

		stopButton = new Button();
		stopButton.setOnAction((e) -> stopClicked());
		updateStopButton();
		Menu stopMenu = createButtonMenu(stopButton);


		Menu clockSpeedLabelMenu = new Menu();
		clockSpeedLabel = new Label();
		clockSpeedLabelMenu.setGraphic(clockSpeedLabel);
		clockSpeedLabelMenu.setStyle("-fx-padding:4px 5px 0px 0px;-fx-background-color:null;");
		clockSpeedLabel.setStyle("-fx-text-fill: black;");


		lastClockSpeedSetTime = 0;
		Menu clockSpeedSliderMenu = new Menu();
		clockSpeedSlider = new Slider();
		clockSpeedSlider.setMin(0);
		clockSpeedSlider.setMax(sliderMax);
		clockSpeedSlider.setPrefWidth(250);
		clockSpeedSlider.valueProperty().addListener((a, b, c) -> updateClockSpeed(false));
		clockSpeedSlider.valueChangingProperty().addListener((a, b, c) -> updateClockSpeed(true));
		// one notch per scroll, regardless of user scroll acceleration or other settings
		clockSpeedSlider.setOnScroll((e) ->
				clockSpeedSlider.setValue(clockSpeedSlider.getValue()+Math.signum(e.getDeltaY())));
		setSliderToMatch(cpu.getCycleFreq());
		clockSpeedSliderMenu.setGraphic(clockSpeedSlider);
		clockSpeedSliderMenu.setStyle("-fx-padding:4px 5px 0px 0px;-fx-background-color:null;");

		menu.getMenus().addAll(spacer, pausePlayMenu, resumeSingleMenu, stopMenu, clockSpeedSliderMenu, clockSpeedLabelMenu);
	}

	private Menu createButtonMenu(Button b) {
		Menu menu = new Menu();
		menu.setGraphic(b);
		// top right bottom left
		menu.setStyle("-fx-padding:4px 5px 0px 0px;-fx-background-color:null;");

		b.setAlignment(Pos.CENTER);
		b.setContentDisplay(ContentDisplay.CENTER);

		b.setStyle("-fx-background-color:transparent;-fx-padding:0px;-fx-background-radius:6em;");

		return menu;
	}

	private void updatePlayButton() {
		if(cpu != null) {
			if(!cpu.isRunning() || cpu.isPaused()) {
				playPauseButton.setGraphic(playResting);
				if (cpu.isPaused())
					playPauseButton.setTooltip(new Tooltip("Resume Simulation (F6)"));
				else
					playPauseButton.setTooltip(new Tooltip("Run Simulation (F5)"));
				playPauseButton.setOnMouseEntered((e) -> playPauseButton.setGraphic(playHover));
				playPauseButton.setOnMouseExited((e) -> playPauseButton.setGraphic(playResting));
			} else {
				playPauseButton.setGraphic(pauseResting);
				playPauseButton.setTooltip(new Tooltip("Pause Simulation (F6)"));
				playPauseButton.setOnMouseEntered((e) -> playPauseButton.setGraphic(pauseHover));
				playPauseButton.setOnMouseExited((e) -> playPauseButton.setGraphic(pauseResting));
			}
		}
	}

	private void updateResumeSingleButton() {
		if(cpu != null) {
			if (cpu.isRunning()) {
				resumeSingleButton.setDisable(!cpu.isPaused());
				resumeSingleButton.setGraphic(resumeSingleResting);
				resumeSingleButton.setTooltip(new Tooltip("Single Step (F7)"));
				resumeSingleButton.setOnMouseEntered((e) -> resumeSingleButton.setGraphic(resumeSingleHover));
				resumeSingleButton.setOnMouseExited((e) -> resumeSingleButton.setGraphic(resumeSingleResting));
			} else {
				resumeSingleButton.setGraphic(null);
				resumeSingleButton.setTooltip(null);
				resumeSingleButton.setOnMouseEntered(null);
				resumeSingleButton.setOnMouseExited(null);
			}
		}
	}

	private void updateStopButton() {
		if(cpu != null) {
			if (cpu.isRunning()) {
				stopButton.setGraphic(stopResting);
				stopButton.setTooltip(new Tooltip("End Simulation (F8)"));
				stopButton.setOnMouseEntered((e) -> stopButton.setGraphic(stopHover));
				stopButton.setOnMouseExited((e) -> stopButton.setGraphic(stopResting));
			} else {
				stopButton.setGraphic(null);
				stopButton.setTooltip(null);
				stopButton.setOnMouseEntered(null);
				stopButton.setOnMouseExited(null);
			}
		}
	}

	private void playClicked() {
		if(cpu != null) {
			if (cpu.isRunning()) {
				if (!cpu.getClock().isRunning())
					cpu.resume();
				else if (cpu.getClock().isRunning())
					cpu.pause();
			} else {
				AssemblingDialog.showAssemblingDialog(wm);
				wm.assembleAndRun();
			}
		}
	}

	private void resumeSingleClicked() {
		if(cpu != null && cpu.isPaused())
			cpu.resumeForOneCycle();
	}

	private void stopClicked() {
		if(cpu != null) {
			if (cpu.isRunning()) {
				cpu.stopRunning();
			}
		}
	}

	/**
     * @param stopped if the user has stopped at the current value, make sure to
     *                set even if the clock speed has been set recently
	 * logarithmic slider algorithm from: http://stackoverflow.com/a/846249/1066911
	 */
	private void updateClockSpeed(boolean stopped) {
		long currentTime = System.currentTimeMillis();
		if(stopped || currentTime - lastClockSpeedSetTime > 50) {
			double val = clockSpeedSlider.getValue();
			double scale = (lgClockSpeedMax-lgClockSpeedMin) / sliderMax;
			val = Math.exp(lgClockSpeedMin + scale * val);
			clockSpeedLabel.setText("" + Math.round((val*10))/10.0 + " Hz");
			cpu.setCycleFreq(val);
			lastClockSpeedSetTime = currentTime;
		}
	}
	public void setSliderToMatch(double cyclesPerSecond) {
		double scale = (lgClockSpeedMax-lgClockSpeedMin) / sliderMax;
		double sliderVal = (Math.log(cyclesPerSecond) - lgClockSpeedMin) / scale;
		clockSpeedSlider.setValue(sliderVal);
		clockSpeedLabel.setText("" + Math.round(cyclesPerSecond*10)/10.0 + " Hz");
	}
}
