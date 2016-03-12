package simulizer.utils;

import java.util.List;

/**
 * Utilities for common string operations
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
}
