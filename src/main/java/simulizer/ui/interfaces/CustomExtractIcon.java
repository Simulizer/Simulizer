package simulizer.ui.interfaces;

import javafx.scene.image.Image;
import javafx.scene.layout.*;
import jfxtras.scene.control.window.Window;
import jfxtras.scene.control.window.WindowIcon;
import simulizer.utils.FileUtils;

/**
 * @author mbway
 */
public class CustomExtractIcon extends WindowIcon {

	public CustomExtractIcon(final InternalWindow w) {
		getStyleClass().setAll("custom-close-icon");
		BackgroundImage img = new BackgroundImage(
				new Image(FileUtils.getResourceToExternalForm("/img/extract.png"), 15, 15, false, true),
        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
          BackgroundSize.DEFAULT);
		setBackground(new Background(img));
		setOnAction((e) -> w.toggleWindowExtracted());
	}
}
