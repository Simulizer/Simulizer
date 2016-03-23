package simulizer.simulation.cpu.user_interaction;

import simulizer.utils.StringUtils;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * I/O for the simulation which uses buffers to capture the output or provide input
 *
 * read operations will fail if the object is not given enough input
 *
 * @author mbway
 */
public class BufferIO implements IO {

	public Scanner input;
	private Map<IOStream, StringWriter> outputs;

	public BufferIO() {
		input = null;
		outputs = new HashMap<>(); // created as needed
	}

	public BufferIO(String input) {
		this();
		feedInput(input);
	}

	public void feedInput(String input) {
		this.input = new Scanner(new StringReader(input));
	}

	public String getOutput(IOStream stream) {
		if(outputs.containsKey(stream)) {
			return outputs.get(stream).toString();
		} else {
			return "";
		}
	}

	@Override public String readString(IOStream stream) throws NoSuchElementException {
		return input.nextLine();
	}

	@Override public int readInt(IOStream stream) throws NoSuchElementException {
		return input.nextInt();
	}

	@Override public char readChar(IOStream stream) throws NoSuchElementException {
		return StringUtils.nextChar(input);
	}

	@Override public void printString(IOStream stream, String str) {
		if(outputs.containsKey(stream)) {
			outputs.get(stream).write(str);
		} else {
			StringWriter out = new StringWriter();
			outputs.put(stream, out);
			out.write(str);
		}
	}

	@Override public void printInt(IOStream stream, int num) {
		printString(stream, Integer.toString(num));
	}

	@Override public void printChar(IOStream stream, char letter) {
		printString(stream, Character.toString(letter));
	}

	@Override public void cancelRead() {
		// Shouldn't need to do anything since read operations do not wait for the user.
		// They either have input or throw an exception immediately.
	}
}
