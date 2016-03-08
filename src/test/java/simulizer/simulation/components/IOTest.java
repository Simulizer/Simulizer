package simulizer.simulation.components;

import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.simulation.cpu.user_interaction.IOStream;

/**this class is used as a really naive way of taking I/O to and from the simulation
 * it is used only for testing purposes and will not be used in the final system at all
 * @author Charlie Street
 *
 */
public class IOTest implements IO {

	public String scanner = "";//used for taking output/input
	
	@Override
	public String readString() {
		String scanned = scanner;
		scanner = "";
		return scanned;
	}

	@Override
	public int readInt() {
		int scanned = Integer.parseInt(scanner);
		scanner = "";
		return scanned;
	}

	@Override
	public char readChar() {
		char scanned = scanner.charAt(0);
		scanner = "";
		return scanned;
	}

	@Override
	public void printString(IOStream stream, String str) {
		scanner = str;
		
	}

	@Override
	public void printInt(IOStream stream, int num) {
		scanner = num+"";
		
	}

	@Override
	public void printChar(IOStream stream, char letter) {
		scanner = letter+"";
		
	}

	@Override
	public void cancelRead() {
		// TODO Cancel Read
	}

}
