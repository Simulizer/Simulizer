package simulizer.utils;

import java.util.Arrays;

/**
 * Created by matthew on 13/09/16.
 * @author mbway
 */
public class CircularIntBuffer {
    private final int[] elements;
    private int cursor;

    public CircularIntBuffer(int size) {
        elements = new int[size];
        cursor = 0;
    }
    public void add(int val) {
        elements[cursor] = val;
        if(cursor >= elements.length-1)
            cursor = 0;
        else
            ++cursor;
    }
    public int mean() {
        long total = 0;
        for(int element : elements) total += element;
        return (int) (total / elements.length);
    }
    public void clear() {
        Arrays.fill(elements, 0);
    }
}
