package simulizer.utils;

import java.util.Arrays;

/**
 * Created by matthew on 13/09/16.
 * 
 * @author mbway
 */
public class CircularLongBuffer {
	private final long[] elements;
	private int cursor;

	public CircularLongBuffer(int size) {
		elements = new long[size];
		cursor = 0;
	}

	public void add(long val) {
		if (val < 0)
			System.out.println("TRIED TO ADD NEGATIVE NUMBER: " + val);
		elements[cursor] = val;
		if (cursor >= elements.length - 1)
			cursor = 0;
		else
			++cursor;
	}

	public long mean() {
		long total = 0;
		for (long element : elements)
			total += element;
		return total / elements.length;
	}

	public void clear() {
		Arrays.fill(elements, 0);
	}
}
