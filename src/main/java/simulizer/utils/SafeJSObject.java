package simulizer.utils;

import netscape.javascript.JSObject;

/**
 * A wrapper around the JavaFX web engine Javascript object, to add checking
 * which averts segfaults caused by a JavaFX bug, and throws an exception instead.
 *
 * @author mbway
 */
public class SafeJSObject {
	private JSObject obj;

	public SafeJSObject(JSObject obj) {
		this.obj = obj;
	}

	/**
	 * set a member of the javascript object (like assigning to a Map<String, Object>)
	 * @param s the key / member name
	 * @param o the value
	 */
	public void setMember(String s, Object o) {
		UIUtils.assertFXThread();
		obj.setMember(s, o);
	}

	/**
	 * call a method of the javascript object
	 * @param s the key / name of the method
	 * @param args the arguments to pass to the method (can be nothing)
	 * @return the return value from the method call
	 */
	public synchronized Object call(String s, Object... args) {
		UIUtils.assertFXThread();
		return obj.call(s, args);
	}
}
