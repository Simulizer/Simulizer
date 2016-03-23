package simulizer.simulation.spim_compliance;

import category.SpimTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import simulizer.simulation.cpu.user_interaction.IOStream;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.utils.FileUtils;
import simulizer.utils.runner.SimulizerRunner;
import simulizer.utils.runner.SpimRunner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * test that Simulizer's memory access resembles SPIM correctly
 * @author mbway
 */
@Category({SpimTests.class})
public class MemoryTests {

	public static String getTestFile(String name) {
		return FileUtils.getResourceContent("/simulizer/simulation/spim_compliance/" + name);
	}

	public static class Outputs {
		public SimulizerRunner simulizer;
		public SpimRunner spim;

		public String program;
		public String simulizerOutput;
		public String spimOutput;

		public Outputs(String filename) {
			program = getTestFile(filename);

			spim = new SpimRunner();
			spimOutput = spim.run(program, "");

			simulizer = new SimulizerRunner(false); // non-pipelined
			simulizerOutput = simulizer.run(program, "");

			testGood();
		}

		public void testGood() {
			assertTrue(simulizer.problemLogger.getProblems().isEmpty());
			assertTrue(simulizer.io.getOutput(IOStream.ERROR).isEmpty());
		}

	}

	@Test
	public void testMemory() {
		//TODO: test whether load and store happens 'upwards' or 'downwards' from the given address
		//TODO: test endianness of spim and mars
		//TODO: test the behaviour of out of bounds reads and writes
	}

	@Test
	public void testStack() {

		//TODO: test that reads and writes are consistent with the behaviour of the heap

		// test that the stack stores 'upwards' ie storing 2 bytes at address 100 (decimal)
		// implies 'store at 100 and 101' and NOT 'store at 100 and 99'
		{
			Outputs outputs = new Outputs("stack-stores-above.s");
			/*
			assertEquals(0xADL,
					DataConverter.decodeAsUnsigned(
					DataConverter.truncate(
							Long.parseLong(outputs.spimOutput),
							1
					)));
			assertEquals(outputs.spimOutput, outputs.simulizerOutput);
			*/
		}
	}

	@Test
	public void testHeap() {
		//TODO: test that reads and writes are consistent with the behaviour of the stack
		//TOOD: test that the break address returned by sbrk is the lowest address of the newly allocated block
	}
}
