package simulizer;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by matthew on 06/09/16.
 */
class CommandLineArguments {

    @Parameters(separators = "=", commandDescription = "Start Simulizer in Command Line mode")
    static class CmdModeArgs {
        @Parameter(names = {"-i", "--interactive"})
        public boolean interactive = false;

        @Parameter(names = {"-p", "--permissive"}, description = "configures the assembler to permit harmless problems (eg assembler directive in the wrong place)")
        boolean permissive = false;

        //TODO: maybe add a flag to allow out of bounds reads/writes?

        @Parameter(names = {"--show-debug-stream"}, description = "show the output that would get written to the debug tab in GUI mode")
        boolean showDebugStream = false;

        // TODO: not implemented
        //@Parameter(names = {"--entry-point"}, description = "start execution at a given label")
        //String entryPoint = "main";

        //TODO: not implemented
        //@Parameter(names = {"--output-errors"}, description = "file to output encountered parse errors")
        //public String errorOutputPath;

        @Parameter(description = "<file to run>")
        List<String> files = new ArrayList<>();
    }


    @Parameters(separators = "=", commandDescription = "Start Simulizer in GUI mode")
    public static class GuiModeArgs {
        @Parameter(names = {"-s", "--settings"}, description = "Specify an alternative settings file")
        String settingsPath = "settings.json";

        @Parameter(names = {"-l", "--layout"}, description = "Specify a layout name to use at startup (see also: settings file)")
        public String layout = "default";

        @Parameter(names = {"--no-splash"}, description = "Disable the splash screen (see also: settings file)")
        boolean noSplash = false;

        @Parameter(names = {"-f", "--fullscreen"}, description = "Start in full screen")
        public boolean fullscreen = false;

        @Parameter(names = {"--pipelined"}, description = "Enable processor pipelining (see also: settings file)")
        public boolean pipelined = false;

        @Parameter(description = "<file to run>")
        public List<String> files = new ArrayList<>();
    }
    enum Mode {
        CMD_MODE,
        GUI_MODE
    }

    Mode mode;
    CmdModeArgs cmdMode;
    GuiModeArgs guiMode;

    @Parameter(names = { "-h", "--help" }, description = "Display this message")
    private boolean help = false;


    static CommandLineArguments parse(String[] args) {
        CommandLineArguments main = new CommandLineArguments();

        JCommander jc;
        if(specifiesMode(args)) {
            jc = new JCommander(main);
            main.cmdMode = new CmdModeArgs();
            jc.addCommand("cmd", main.cmdMode);

            main.guiMode = new GuiModeArgs();
            jc.addCommand("gui", main.guiMode);
        } else {
            main.guiMode = new GuiModeArgs();
            jc = new JCommander(main.guiMode);
        }


        try {
            jc.parse(args);
        } catch(ParameterException e) {
            System.err.println("Invalid Arguments: " + e.getMessage() + "\n");
            printUsage(jc);
            return null;
        }

        if(main.help) {
            printUsage(jc);
            return null;
        }

        String command = jc.getParsedCommand();
        if(command == null || command.equals("gui")) {
            main.mode = Mode.GUI_MODE;
            if(main.guiMode.files.size() > 1) {
                System.err.println("Invalid File Arguments: " + Arrays.toString(main.guiMode.files.toArray()) + " must only specify one file to open");
                printUsage(jc);
                return null;
            }
        } else if(command.equals("cmd")) {
            main.mode = Mode.CMD_MODE;
            if(main.cmdMode.files.size() > 1) {
                System.err.println("Invalid File Arguments: " + Arrays.toString(main.guiMode.files.toArray()) + " must only specify one file to open");
                printUsage(jc);
                return null;
            }
        } else {
            throw new RuntimeException("invalid commands");
        }

        return main;
    }

    private static boolean specifiesMode(String[] args) {
        for(String arg : args) {
            if(arg.equals("gui") || arg.equals("cmd")) {
                return true;
            }
        }
        return false;
    }

    private static void printUsage(JCommander jc) {
        System.out.println("Example Usage: java -jar simulizer.jar gui --fullscreen --layout \"High Level\" --pipelined my_file.s\n");
        jc.usage();
        jc.usage("cmd");
        jc.usage("gui");
    }

}
