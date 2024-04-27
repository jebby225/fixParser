package org.fixparser.service;

import org.fixparser.component.FixComponent;
import org.fixparser.component.FixHeader;
import org.fixparser.factory.FixMessageFactory;
import org.fixparser.component.FixTrailer;
import org.fixparser.constant.ExceptionMessages;
import org.fixparser.component.GenericFixMessage;

public class FixParserService {
    public static FixComponent parseByMessageType(byte[] fixArr) {
        FixHeader header = new FixHeader();
        if(!header.parse(fixArr)) {
            throw new NullPointerException(ExceptionMessages.INVALID_HEADER);
        }
        FixTrailer trailer = new FixTrailer();
        if(!trailer.parse(fixArr)) {
            throw new NullPointerException(ExceptionMessages.INVALID_TRAILER);
        }
        return FixMessageFactory.createFixMessage(fixArr, header, trailer);
    }

    public static FixComponent parseGeneric(byte[] fixArr) {
        GenericFixMessage message = new GenericFixMessage();
        message.parse(fixArr);
        return message;
    }

}
