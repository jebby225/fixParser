package org.fixparser.component;

public interface TagValue<T,V> {
    T tag();
    V value();
}
