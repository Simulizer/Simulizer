package simulizer.annotations;

/**
 * A collection of methods for controlling high level visualisations from annotations
 */
@SuppressWarnings("unused")
public class VisualisationBridge {
	public void load(String visualisationName) {
		switch(visualisationName) {
			case "tower-of-hanoi":
				break;
			default:
				throw new IllegalArgumentException();
		}
	}
}
