package simulizer;

import org.antlr.v4.runtime.misc.Utils;
import simulizer.assembler.Assembler;
import simulizer.assembler.extractor.problem.StoreProblemLogger;
import simulizer.assembler.representation.Program;
import simulizer.cmd.CmdIO;
import simulizer.cmd.CmdSimulationListener;
import simulizer.simulation.cpu.CPUChangedListener;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.cpu.user_interaction.ConsoleIO;
import simulizer.simulation.exceptions.ExecuteException;
import simulizer.utils.FileUtils;

import java.io.IOException;

import static simulizer.GuiMode.settings;

/**
 * Created by matthew on 06/09/16.
 */
class CmdMode {
    //TODO: make frequency specifiable (is this even useful?)

    public static CommandLineArguments.CmdModeArgs args;

    public static CmdIO io;
    public static CPU cpu;
    public static CmdSimulationListener simListener;

    public static void start(String[] rawArgs, CommandLineArguments parsedArgs) {
        args = parsedArgs.cmdMode;

        // >1 case already checked by arguments parser
        if(args.files.size() == 0) {
            System.err.println("no file specified");
            return;
        }

        io = new CmdIO(args.showDebugStream);

        cpu = new CPU(io); // not pipelined

        simListener = new CmdSimulationListener();

        cpu.registerListener(simListener);
        cpu.setCycleFreq(0); //Hz

        String programText = FileUtils.getFileContent(args.files.get(0));
        assembleAndRun(programText, args.permissive);
    }

    private static void assembleAndRun(String programText, boolean permissive) {
        StoreProblemLogger log = new StoreProblemLogger();
        final Program p = Assembler.assemble(programText, log, permissive);
        if(p == null) {
            int size = log.getProblems().size();
            System.err.println("Could Not Run. The Program Contains " + (size == 1 ? "An Error!" : size + " Errors!"));
        } else {
            cpu.loadProgram(p);

            try {
                cpu.runProgram();
            } catch (Exception e) {
                System.err.println("Exception: " + e.getMessage());
            }
        }
    }
}
