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

	public void setMember(String s, Object o) {
		UIUtils.assertFXThread();
		obj.setMember(s, o);
	}
	public synchronized Object call(String s, Object... args) {
		UIUtils.assertFXThread();
		return obj.call(s, args);
	}
}
