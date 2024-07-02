package org.fixparser.component;

import org.fixparser.constant.ExceptionMessages;
import org.fixparser.service.FixParserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GenericFixMessageTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "9=251\u00018=FIX.4.2\u000135=D\u000149=AFUNDMGR\u0001",
            "8=FIX.4.2\u000135=D\u000149=AFUNDMGR\u0001",
            "8=FIX.4.2\u00019=\u000135=D\u000149=AFUNDMGR\u0001",
            "\u0001",
            "\u00018=FIX.4.2\u00019=251\u000135=D\u000149=AFUNDMGR",
            ""
    })
    public void parseTest_invalidHeader(String fixMessage) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            FixParserService.parseGeneric(fixMessage.getBytes());
        });

        String expectedMessage = ExceptionMessages.INVALID_HEADER;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "8=FIX.4.2\u00019=251\u000135=D\u000149=AFUNDMGR\u000110=abc\u0001",    // expected int value
            // Add more test cases here...
    })
    public void parseTest_trailerWithInvalidValueType(String fixMessage) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            FixParserService.parseGeneric(fixMessage.getBytes());
        });

        String expectedMessage = ExceptionMessages.INVALID_VALUE_TYPE;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "8=FIX.4.2\u00019=251\u000135=D\u000149=AFUNDMGR\u000110=29",
            "8=FIX.4.2\u00019=251\u000135=D\u000149=AFUNDMGR\u000110=\u0001",
            "8=FIX.4.2\u00019=251\u000135=D\u0001",
            "8=FIX.4.2\u00019=251\u000135=D\u000110=20\u000149=AFUNDMGR\u0001"
    })
    public void parseTest_invalidTrailer(String fixMessage) {

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            FixParserService.parseGeneric(fixMessage.getBytes());
        });

        String expectedMessage = ExceptionMessages.INVALID_TRAILER;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void parserTest_validHeaderAndTrailer() {
        byte[] fixMessage = "8=FIX.4.2\u00019=251\u000135=D\u000149=AFUNDMGR\u000110=127\u0001".getBytes();

        FixComponent message = FixParserService.parseGeneric(fixMessage);
        assertEquals(message.getClass(), GenericFixMessage.class);

        // Verify header fields
        if(message instanceof GenericFixMessage) {
            GenericFixMessage gMessage = (GenericFixMessage) message;
            assertEquals("FIX.4.2", new String(gMessage.getFixVersion()));
            assertEquals( 127, gMessage.getCheckSum());
            assertEquals(251, gMessage.getBodyLength());
            assertEquals("D", new String(gMessage.getMsgType()));
        } else {
            fail("Message is not of type GenericFixMessage");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "8=FIX.4.2\u00019=251\u000135=D\u0001=AFUNDMGR\u000110=291\u0001",
            "8=FIX.4.2\u00019=251\u000135=D\u000149=\u000110=291\u0001",
            "8=FIX.4.2\u00019=251\u000135=D\u0001=\u000110=291\u0001",
            "8=FIX.4.2\u00019=251\u000135=D\u0001\u000149=AFUNDMGR\u000110=291\u0001"
    })
    public void parserTest_invalidTagValuePair(String fixMessage) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            FixParserService.parseGeneric(fixMessage.getBytes());
        });

        String expectedMessage = ExceptionMessages.EMPTY_TAG_VALUE_PAIR;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void parserTest_validFixMessage() {
        String input = "8=FIX.4.2\u00019=251\u000135=D\u000149=AFUNDMGR\u000156=ABROKER\u000134=2\u000152=2003061501:14:49\u000111=12345\u00011=111111\u000163=0\u000164=20030621\u000121=3\u0001110=1000\u0001111=50000\u000155=IBM\u000148=459200101\u000122=1\u000154=1\u000160=20030615-01:14:49\u000138=5000\u000140=1\u000144=15.75\u000115=USD\u000159=0\u000110=127\u0001";
        FixComponent message = FixParserService.parseGeneric(input.getBytes());
        assertEquals(message.getClass(), GenericFixMessage.class);

        // Verify header fields
        if(message instanceof GenericFixMessage) {
            GenericFixMessage gMessage = (GenericFixMessage) message;
            assertEquals("FIX.4.2", new String(gMessage.getFixVersion()));
            assertEquals( 127, gMessage.getCheckSum());
            assertEquals(251, gMessage.getBodyLength());
            assertEquals("D", new String(gMessage.getMsgType()));
            assertEquals("AFUNDMGR", new String(gMessage.getValueByTag("49".getBytes())));
            assertEquals("2003061501:14:49", new String(gMessage.getValueByTag("52".getBytes())));
        } else {
            fail("Message is not of type GenericFixMessage");
        }
    }

    @Test
    public void parserTest_missingTag() {
        String input = "8=FIX.4.2\u00019=251\u000135=D\u000149=AFUNDMGR\u000156=ABROKER\u000134=2\u000152=2003061501:14:49\u000111=12345\u00011=111111\u000163=0\u000164=20030621\u000121=3\u0001110=1000\u0001111=50000\u000155=IBM\u000148=459200101\u000122=1\u000154=1\u000160=20030615-01:14:49\u000138=5000\u000140=1\u000144=15.75\u000115=USD\u000159=0\u000110=127\u0001";
        FixComponent message = FixParserService.parseGeneric(input.getBytes());
        assertEquals(message.getClass(), GenericFixMessage.class);

        // Verify header fields
        if(message instanceof GenericFixMessage) {
            GenericFixMessage gMessage = (GenericFixMessage) message;
            assertEquals(null, gMessage.getValueByTag("900".getBytes()));
        } else {
            fail("Message is not of type GenericFixMessage");
        }
    }

}
