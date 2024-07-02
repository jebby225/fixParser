package org.fixparser.util;

import org.fixparser.constant.ExceptionMessages;

import java.util.Arrays;

public class ByteArrayWrapper {
    private final byte[] data;

    public ByteArrayWrapper(byte[] data) {
        // Ensure the data is not null
        if (data != null) {
            this.data = Arrays.copyOf(data, data.length);
        } else {
            throw new IllegalArgumentException(ExceptionMessages.EMPTY_TAG_VALUE_PAIR);
        }
    }

    public byte[] getData() {
        return this.data;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ByteArrayWrapper that = (ByteArrayWrapper) obj;
        return Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

}
