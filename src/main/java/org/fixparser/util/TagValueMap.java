package org.fixparser.util;

import org.fixparser.constant.ExceptionMessages;

import java.awt.*;
import java.util.Arrays;

public class TagValueMap{

    private int[] tags;
    private byte[][] values;
    private int size;
    private static final int LOAD_FACTOR = 2;

    public TagValueMap(int capacity) {
        this.tags = new int[capacity];
        this.values = new byte[capacity][];
    }

    public int getSize() {
        return size;
    }

    public void put(int key, byte[] value) {
        if (size == tags.length) {  // This is expensive and we should never reach this case. Putting here just in case
            int newCapacity = tags.length * LOAD_FACTOR;
            tags = Arrays.copyOf(tags, newCapacity);
            values = Arrays.copyOf(values, newCapacity);
        }
        tags[size] = key;
        values[size] = value;
        ++size;
    }

    public byte[] get(int key) {
        for (int i = 0; i < size; i++) {
            if (tags[i] == key) {
                return values[i];
            }
        }
        System.out.println(String.format(ExceptionMessages.TAG_NOT_FOUND, key));
        return null;
    }

    public byte[] getValueByIndex(int idx) {
        return values[idx];
    }

    public int getTagByIndex(int idx) {
        return tags[idx];
    }

}
