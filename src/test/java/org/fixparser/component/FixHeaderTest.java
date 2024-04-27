package org.fixparser.component;

import org.fixparser.constant.ExceptionMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;


public class FixHeaderTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "8=FIX.4.2\u00019=p\u000135=D\u000149=AFUNDMGR\u0001"
            // Add more test cases here...
    })
    public void parseTest_headerWithInvalidValueType(String fixMessage) {
        byte[] fixByte = fixMessage.getBytes();

        // Parse the header
        FixHeader header = new FixHeader();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            header.parse(fixByte);
        });

        String expectedMessage = ExceptionMessages.INVALID_VALUE_TYPE;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "9=251\u00018=FIX.4.2\u000135=D\u000149=AFUNDMGR\u0001",
            "8=FIX.4.2\u000135=D\u000149=AFUNDMGR\u0001",
            "8=FIX.4.2\u00019=\u000135=D\u000149=AFUNDMGR\u0001",
            "\u0001",
            "\u00018=FIX.4.2\u00019=251\u000135=D\u000149=AFUNDMGR",
            ""
            // Add more test cases here...
    })
    public void parseTest_invalidHeader(String fixMessage) {
        byte[] fixByte = fixMessage.getBytes();

        // Parse the header
        FixHeader header = new FixHeader();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            header.parse(fixByte);
        });

        String expectedMessage = ExceptionMessages.INVALID_HEADER;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void parserTest_validHeader() {
        byte[] fixByte = "8=FIX.4.2\u00019=251\u000135=D\u000149=AFUNDMGR\u0001".getBytes();

        // Parse the header
        FixHeader header = new FixHeader();
        header.parse(fixByte);

        // Verify header fields
        assertEquals(21, header.getBodyIdx());
        assertEquals("FIX.4.2", new String(header.getFixVersion()));
        assertEquals(251, header.getBodyLength());
        assertEquals("D", new String(header.getMsgType()));
    }
}

