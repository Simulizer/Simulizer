package simulizer;

/**
 * Created by matthew on 06/09/16.
 */
class CmdMode {

    public static CommandLineArguments.CmdModeArgs args;

    public static void start(String[] rawArgs, CommandLineArguments parsedArgs) {
        args = parsedArgs.cmdMode;
    }
}
