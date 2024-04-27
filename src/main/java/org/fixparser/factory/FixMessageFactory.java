package org.fixparser.factory;

import org.fixparser.component.FixHeader;
import org.fixparser.component.FixTrailer;

public class FixMessageFactory {
    public static FixMessage createFixMessage(byte[] fixArr, FixHeader header, FixTrailer trailer) {
        FixMessage message;
        switch (new String(header.getMsgType())) {
            case "D": // New Order
                message = new NewOrderMessage(header, trailer, 6);
                break;
            case "F": // Order Cancel Request
                message = new OrderCancelRequestMessage(header, trailer, 4);
                break;
            default:
                throw new IllegalArgumentException("Unknown message type: " + new String(header.getMsgType()));
        }
        message.initializeTagValuePairsArr(fixArr);
        message.parse(fixArr);
        return message;
    }
}
