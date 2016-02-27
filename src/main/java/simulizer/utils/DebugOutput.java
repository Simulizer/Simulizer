package simulizer.utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * debugging tool to dump a string to a file
 */
public class DebugOutput {

	public static void dumpToFile(String str, String filename) {
		try {
			PrintWriter out = new PrintWriter(filename);
			out.print(str);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void dumpToFile(String str) {
		dumpToFile(str, "debug-out.txt");
	}
}
