package simulizer.ui.layout;

import java.util.Iterator;

public class Layout implements Iterable<WindowLocation> {

	private final WindowLocation[] l;
	private int index = 0;

	public Layout(WindowLocation[] l) {
		this.l = l;
	}

	@Override
	public Iterator<WindowLocation> iterator() {
		index = 0;
		return new Iterator<WindowLocation>() {

			@Override
			public boolean hasNext() {
				return index < l.length;
			}

			@Override
			public WindowLocation next() {
				WindowLocation out = l[index];
				index++;
				return out;
			}

		};
	}

}
