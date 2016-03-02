package simulizer.ui.components.cpu;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.List;

public class Wire extends Group {

	public enum Type {
		HORIZONTAL, VERTICAL, CUSTOM
	}

	Polyline line;
	Polyline arrowHead;
	Type type;
	Path path;
	int time;
	Double progressed = 0.0;
	boolean from;
	boolean animating;
	boolean reverse;
	List<PathTransition> transitions;

	public Wire(Polyline line, Polyline arrowHead, Type type) {
		this.line = line;
		this.arrowHead = arrowHead;
		this.type = type;
		this.animating = false;
		this.reverse = false;
		this.transitions = new LinkedList<PathTransition>();
		this.path = new Path();
	}

	public void reanimateData() {
		setUpAnimationPath();
		if (!animating)
			return;

		for(PathTransition p : transitions){
			p.stop();
			p.setPath(path);
			p.playFrom(new Duration(progressed));
		}

		synchronized(progressed){
			progressed++;
		}
	}

	public void setUpAnimationPath() {
		if(line.getPoints().size() < 2) return;
		path.getElements().clear();

		path.getElements().add(new MoveTo(line.getPoints().get(0), line.getPoints().get(1)));

		for (int i = 2; i < line.getPoints().size(); i += 2) {
			double x = line.getPoints().get(i);
			double y = line.getPoints().get(i + 1);
			path.getElements().add(new LineTo(x, y));
		}

		if (reverse) {
			path.getElements().clear();

			path.getElements().add(new MoveTo(line.getPoints().get(line.getPoints().size() - 2), line.getPoints().get(line.getPoints().size() - 1)));

			for (int i = line.getPoints().size() - 3; i > 0; i -= 2) {
				double y = line.getPoints().get(i);
				double x = line.getPoints().get(i - 1);
				path.getElements().add(new LineTo(x, y));
			}

		}

	}

	public void animateData(int animTime) {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				animating = true;
				PathTransition pathTransition = new PathTransition();
				time = animTime;

				transitions.add(pathTransition);

				pathTransition.setDuration(Duration.millis(time));
				Circle data = new Circle(0, 0, 7.5);
				data.getStyleClass().addAll("cpu-data");
				pathTransition.setNode(data);
				pathTransition.setPath(path);
				pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
				pathTransition.setCycleCount(1);
				pathTransition.setAutoReverse(false);

				getChildren().add(data);
				pathTransition.play();

				pathTransition.setOnFinished(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent event) {
						getChildren().remove(data);
						transitions.remove(pathTransition);
					}
				});

				synchronized(progressed){
					progressed++;
				}
			}
		});
	}

	public void closeThread() {

	}
}
