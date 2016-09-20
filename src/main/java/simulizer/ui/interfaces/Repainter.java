package simulizer.ui.interfaces;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import simulizer.utils.UIUtils;

/**
 * Calls repaint in the FX thread to get x FPS
 * 
 * @author Michael
 *
 */
public class Repainter {
	private static AnimationTimer timer;
	private static List<Repaintable> elements = new ArrayList<Repaintable>();
	private static int FRAME_RATE = 45;

	// Static class
	private Repainter() {
	}

	public synchronized static void add(Repaintable r) {
		if (elements.size() == 0) {
			timer = new AnimationTimer() {
				long lastTime = -1;

				@Override
				public void handle(long now) {
					UIUtils.assertFXThread();
					if (lastTime == -1 || now - lastTime > 1e9 / FRAME_RATE) {
						lastTime = now;
						for (Repaintable r : elements)
							r.repaint();
					}
				}
			};
			timer.start();
		}
		elements.add(r);
	}

	public synchronized static void remove(Repaintable r) {
		elements.remove(r);
		if (elements.size() == 0)
			timer.stop();
	}

	public interface Repaintable {
		void repaint();
	}
}
