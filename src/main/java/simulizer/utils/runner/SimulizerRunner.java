package simulizer.utils.runner;

import simulizer.assembler.Assembler;
import simulizer.assembler.extractor.problem.StoreProblemLogger;
import simulizer.assembler.representation.Program;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.cpu.components.CPUPipeline;
import simulizer.simulation.cpu.user_interaction.BufferIO;
import simulizer.simulation.cpu.user_interaction.IOStream;

/**
 * execute Simulizer with a given program and catch the output for analysis
 * @author mbway
 */
public class SimulizerRunner {

	public StoreProblemLogger problemLogger;
	public CPU cpu;
	public BufferIO io;

	public SimulizerRunner(boolean pipelined) {
		problemLogger = new StoreProblemLogger();
		io = new BufferIO();
		cpu = pipelined ? new CPUPipeline(io) : new CPU(io);
	}

	/**
	 * Run the given program in Simulizer with the given input and return the captured output
	 * @param program the program source code
	 * @param input the input to the program
	 * @return the captured output
	 */
	public String run(String program, String input) {
		io.feedInput(input);

		Program p = Assembler.assemble(program, problemLogger, true);

		if(!problemLogger.getProblems().isEmpty()) {
			return null;
		}

		cpu.loadProgram(p);

		cpu.runProgram();

		cpu.shutdown();

		return io.getOutput(IOStream.STANDARD);
	}
}
