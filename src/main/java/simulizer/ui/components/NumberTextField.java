package simulizer.ui.components;

import javafx.scene.control.TextField;

/**
 * Represents a text field that only takes digits as input.
 * This can be useful for situations where the user is only allowed to
 * enter a natural number.
 *
 * @author Kelsey McKenna
 *         with thanks from http://stackoverflow.com/a/18959399
 *
 */
public class NumberTextField extends TextField {
	@Override
	public void replaceText(int start, int end, String text) {
		if (validate(text)) {
			super.replaceText(start, end, text);
		}
	}

	@Override
	public void replaceSelection(String text) {
		if (validate(text)) {
			super.replaceSelection(text);
		}
	}

	/**
	 * @param text
	 *            the text to be validated
	 * @return true if and only if the text consists of 0 or more digits.
	 */
	private boolean validate(String text) {
		return text.matches("[0-9]*");
	}
}
