package org.fixparser.component;


public class TagValuePair<T, V> {
    private T tag;
    private V value;

    private int tagSidx;
    private int tagEidx;

    private int valueSidx;
    private int valueEidx;

    public void tag(T tag) {
        this.tag = tag;
    }

    public TagValuePair(int tagSidx, int tagEidx, int valueSidx, int valueEidx) {
        this.tagSidx = tagSidx;
        this.tagEidx = tagEidx;
        this.valueSidx = valueSidx;
        this.valueEidx = valueEidx;
    }

    public TagValuePair(T tag, V value) {
        if (null == tag) {
            throw new NullPointerException("Argument tag is null!");
        }
        if (null == value) {
            throw new NullPointerException("Argument value is null!");
        }
        this.tag = tag;
        this.value = value;
    }

    public T getTag() {
        return tag;
    }

    public V getValue() {
        return value;
    }

    public String toString() {
        return this.tagSidx + " " + this.tagEidx + " " + this.valueSidx + " " + this.valueEidx;
        //return String.valueOf(tag) + "=" + String.valueOf(value);
    }

}