package simulizer.ui.windows;

import org.fxmisc.richtext.CodeArea;
import simulizer.ui.interfaces.InternalWindow;

public class Logger extends InternalWindow {

	public Logger() {
		CodeArea codeArea = new CodeArea();
		codeArea.replaceText(0, 0, "Some Error Message:");
		codeArea.setEditable(false);
		getContentPane().getChildren().add(codeArea);
	}

}
