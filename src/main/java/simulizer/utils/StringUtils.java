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
}
