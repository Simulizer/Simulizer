package simulizer.ui.interfaces;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import jfxtras.labs.scene.control.window.CloseIcon;
import jfxtras.labs.scene.control.window.MinimizeIcon;
import jfxtras.labs.scene.control.window.Window;
import simulizer.ui.GridBounds;
import simulizer.ui.theme.Theme;

public abstract class InternalWindow extends Window implements Observer {

	private GridBounds grid;
	private ChangeListener<Number> resizeEvent = new ChangeListener<Number>() {
		// Thanks to: http://stackoverflow.com/questions/10773000/how-to-listen-for-resize-events-in-javafx#answer-25812859
		final Timer timer = new Timer();
		TimerTask task = null;
		final long delayTime = 200; // Delay before resize to grid

		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if (task != null) task.cancel();
			task = new TimerTask() {
				@Override
				public void run() {
					double[] coords = { getLayoutX(), getLayoutY() - 25, getLayoutX() + getWidth(), getLayoutY() + getHeight() - 25 };
					coords = grid.moveToGrid(coords);
					if (coords[2] != getLayoutX() + getWidth()) {
						System.out.println("Changed Width to: " + (coords[2] - coords[0]));
						setPrefWidth(coords[2] - coords[0]);
					}
					if (coords[3] != getLayoutY() + getHeight() - 25) {
						System.out.println("Changed Height to: " + (coords[3] - coords[1] + 25));
						setPrefHeight(coords[3] - coords[1]);
					}
					if (coords[0] != getLayoutX()) {
						System.out.println("Changed X to: " + coords[0]);
						setLayoutX(coords[0]);
					}
					if (coords[1] != getLayoutY() - 25) {
						System.out.println("Changed Y to: " + (coords[1] + 25));
						setLayoutY(coords[1] + 25);
					}
					task.cancel();
				}
			};
			timer.schedule(task, delayTime);
		}

	};

	public InternalWindow() {
		// Sets to default title
		setTitle(WindowEnum.toEnum(this).toString());

		// Adds minimise icon
		MinimizeIcon minimize = new MinimizeIcon(this);
		minimize.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
			if (getHeight() > 30) setMinHeight(0.0); // Minimising
			else setMinHeight(getMinimalHeight()); // Maximising
		});
		getRightIcons().add(minimize);

		// Adds close icon
		CloseIcon close = new CloseIcon(this);
		getRightIcons().add(close);

		// Stops window covering MenuBar
		layoutYProperty().addListener((observableValue, oldY, newY) -> {
			if (newY.doubleValue() <= 25) {
				setLayoutY(25);
			}
		});

		// Adds a small window border
		setPadding(new Insets(0, 2, 2, 2));
	}

	public void setBounds(double locX, double locY, double sizeX, double sizeY) {
		setLayoutX(locX);
		setLayoutY(locY);
		setPrefSize(sizeX, sizeY);
	}

	public double[] getBounds() {
		return new double[] { getLayoutX(), getLayoutY(), getBoundsInLocal().getWidth(), getBoundsInLocal().getHeight() };
	}

	protected double getMinimalHeight() {
		return 0.0;
	}

	public void setTheme(Theme theme) {
		getStylesheets().clear();
		getStylesheets().add(theme.getStyleSheet("window.css"));
	}

	public void setGridBounds(GridBounds grid) {
		this.grid = grid;
		grid.addObserver(this);

		// Listens for Resize/Move Events
		widthProperty().addListener(resizeEvent);
		heightProperty().addListener(resizeEvent);
		layoutXProperty().addListener(resizeEvent);
		layoutYProperty().addListener(resizeEvent);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// Main Window Resized
		System.out.println("Main Window Resized");
	}
}
