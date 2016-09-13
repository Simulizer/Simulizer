package simulizer.utils;

import simulizer.Simulizer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * debugging tool to dump a string to a file
 *
 * @author mbway
 */
public class DebugUtils {

	private static void dumpToFile(String str, String filename) {
		try (Writer w = FileUtils.getUTF8FileAppendWriter(filename)){
			w.write(str);
		} catch (IOException e) {
			Simulizer.handleException(e);
		}
	}

	public static void dumpToFile(String str) {
		dumpToFile(str, "debug-out.txt");
	}

	/**
	 * A simple profiling class to measure the time taken to do some processing (like a stopwatch)
	 */
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
