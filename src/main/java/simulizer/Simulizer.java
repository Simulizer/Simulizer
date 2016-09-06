package simulizer;

public class Simulizer {
	public static final String VERSION = "0.3 (beta)";
	public static final String REPO = "https://github.com/mbway/Simulizer";

    public static void main(String[] args) {
        CommandLineArguments parsedArgs = CommandLineArguments.parse(args);

        if(parsedArgs == null) { // some error or user entered --help
            return;
        }

		if(parsedArgs.mode == CommandLineArguments.Mode.CMD_MODE) {
			CmdMode.start(args, parsedArgs);
		} else {
		    GuiMode.start(args, parsedArgs);
		}
	}

}
