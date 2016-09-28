package simulizer.utils;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities for common string operations
 *
 * @author mbway
 */
public class StringUtils {
	/**
	 * joins a list of elements eg [a, b, c] into a string of the form:
	 * "a", "b", "c"
	 *
	 * if the list is empty: returns "\"\"" (that is, a string containing two " characters, not an empty string)
	 */
	public static <E extends CharSequence> String joinList(List<E> items) {
		return "\"" + String.join("\", \"", items) + "\"";
	}

	/**
	 * Wrap a string (in a naive way) to fit into the specified number of columns
	 * wrapToWidth("hello!", 3)
	 * "hel-\nlo!"
	 */
	public static String wrapToWidth(String input, int numCols) {
		StringBuilder sb = new StringBuilder();
		int lastIndex = 0;
		int index = 0;

		while(index < input.length()) {
			index += numCols - 1;
			if(index < input.length()) {
				sb.append(input.substring(lastIndex, index)).append("-\n");
			} else {
				sb.append(input.substring(lastIndex)); // up until the end of the string
			}
			lastIndex = index;
		}
		return sb.toString();
	}

	/**
	 * from http://stackoverflow.com/a/537185
	 * @param text the text to insert into
	 * @param insert the text to insert into the first argument
	 * @param period the number of characters between each insertion
	 * @return 'text' with 'insert' inserted every 'period' characters
	 */
	public static String insert(String text, String insert, int period) {
		Pattern p = Pattern.compile("(.{" + period + "})", Pattern.DOTALL);
		Matcher m = p.matcher(text);
		return m.replaceAll("$1" + insert);
	}

	/**
	 * read exactly one character from the scanner
	 * from: http://stackoverflow.com/a/13942707/1066911
	 */
	public static char nextChar(Scanner s) {
		return s.findInLine(".").charAt(0);
	}
}
