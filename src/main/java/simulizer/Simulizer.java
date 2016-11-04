package simulizer;

import javafx.scene.image.Image;
import simulizer.utils.FileUtils;
import simulizer.utils.UIUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Simulizer {
	private static Image icon = null;
	public static CommandLineArguments.Mode mode; // CMD_MODE || GUI_MODE

    public static void main(String[] args) {
		String jarPath = FileUtils.getJarPath();
		if(jarPath != null && jarPath.contains(".jar")) { // false if running from gradle or IDE
			// should be /some/path/Simulizer/lib/Simulizer-VER.jar
			//
			// first parent() to get lib/, second to get Simulizer/
			String simulizerRoot = Paths.get(jarPath).getParent().getParent().toString();
			System.out.println("moving current working directory to " + simulizerRoot);
			FileUtils.setCWD(simulizerRoot);
		}


		CommandLineArguments parsedArgs = CommandLineArguments.parse(args);

        if(parsedArgs == null) { // some error or user entered --help
            return;
        }

        mode = parsedArgs.mode;

		if(parsedArgs.mode == CommandLineArguments.Mode.CMD_MODE) {
			CmdMode.start(args, parsedArgs);
		} else {
		    GuiMode.start(args, parsedArgs);
		}
	}

    /**
     * @return whether Simulizer is running in GUI mode
     */
    public static boolean hasGUI() {
		return mode == CommandLineArguments.Mode.GUI_MODE;
	}

    /**
     * handle exceptions differently depending on whether running in GUI or CMD mode.
     * in UI-only areas of the code-base, just call UIUtils directly.
     */
    public static void handleException(Exception e) {
		if(mode == CommandLineArguments.Mode.CMD_MODE) {
		    e.printStackTrace();
		} else if(mode == CommandLineArguments.Mode.GUI_MODE) {
			UIUtils.showExceptionDialog(e);
		}
	}
   
	/**
	 * @return Simulizer icon image
	 */
	public static Image getIcon() {
		if (icon == null) {
			icon = new Image(FileUtils.getResourcePath("/img/logo.png"));
		}
		return icon;
	}
}
