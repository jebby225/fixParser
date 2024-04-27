package org.fixparser.component;

import org.fixparser.constant.ExceptionMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FixTrailerTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "8=FIX.4.2\u00019=251\u000135=D\u000149=AFUNDMGR\u000110=abc\u0001",    // expected int value
            // Add more test cases here...
    })
    public void parseTest_trailerWithInvalidValueType(String fixMessage) {
        byte[] fixByte = fixMessage.getBytes();

        // Parse the header
        FixTrailer trailer = new FixTrailer();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            trailer.parse(fixByte);
        });

        String expectedMessage = ExceptionMessages.INVALID_VALUE_TYPE;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "8=FIX.4.2\u00019=251\u000135=D\u000149=AFUNDMGR\u000110=29",
            "8=FIX.4.2\u00019=251\u000135=D\u000149=AFUNDMGR\u000110=\u0001",
            "8=FIX.4.2\u000135=D\u0001",
            "\u0001",
            "\u00018=FIX.4.2\u00019=251\u000110=20\u000149=AFUNDMGR\u0001",
            ""
            // Add more test cases here...
    })
    public void parseTest_invalidTrailer(String fixMessage) {
        byte[] fixByte = fixMessage.getBytes();

        // Parse the header
        FixTrailer trailer = new FixTrailer();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            trailer.parse(fixByte);
        });

        String expectedMessage = ExceptionMessages.INVALID_TRAILER;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void parseTest_validTrailer() {
        byte[] fixByte = "8=FIX.4.2\u00019=251\u000135=D\u000149=AFUNDMGR\u000110=29\u0001".getBytes();

        // Parse the footer
        FixTrailer trailer = new FixTrailer();
        trailer.parse(fixByte);

        // Verify header fields
        assertEquals(29, trailer.getCheckSum());
        assertEquals(33, trailer.getBodyEidx());
    }
}
