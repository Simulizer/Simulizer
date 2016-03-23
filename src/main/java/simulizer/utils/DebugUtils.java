package simulizer.utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * debugging tool to dump a string to a file
 *
 * @author mbway
 */
public class DebugUtils {

	public static void dumpToFile(String str, String filename) {
		try {
			PrintWriter out = new PrintWriter(filename);
			out.print(str);
			out.close();
		} catch (FileNotFoundException e) {
			UIUtils.showExceptionDialog(e);
		}
	}

	public static void dumpToFile(String str) {
		dumpToFile(str, "debug-out.txt");
	}

	public static class Timer {
		public String name;
		public long start;
		public long duration;

		public Timer(String name) {
			this.name = name;
			start = System.currentTimeMillis();
			duration = -1;
		}

		public long stop() {
			duration = System.currentTimeMillis() - start;
			return duration;
		}
		public void stopAndPrint() {
			System.out.println(name + ": " + (stop() / 1000.0) + " seconds");
		}
	}
}
