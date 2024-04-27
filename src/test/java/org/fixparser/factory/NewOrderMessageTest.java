package org.fixparser.factory;

import org.fixparser.component.FixHeader;
import org.fixparser.component.FixTrailer;
import org.fixparser.constant.ExceptionMessages;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class NewOrderMessageTest {
    @Mock
    private FixHeader header;
    @Mock
    private FixTrailer trailer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Mock header
        when(header.getMsgType()).thenReturn("D". getBytes());
        when(header.getBodyIdx()).thenReturn(21);
        when(header.getFixVersion()).thenReturn("FIX.4.4".getBytes());
        when(header.getBodyLength()).thenReturn(251);

        // Mock trailer
        when(trailer.getCheckSum()).thenReturn(127);
    }
    @Test
    public void parserTest_validBody() {
        // 11, 21, 40,54, 55,v60
        // Create a sample FIX message byte array for the body
        byte[] fixByte = "8=FIX.4.2\u00019=251\u000135=D\u000149=AFUNDMGR\u000121=1\u000111=ABCDEFG\u000160=20240215-01:14:49\u000140=2\u000154=3\u000163=0\u000164=20030621\u0001110=1000\u0001111=50000\u000155=IBM\u000110=127\u0001".getBytes();
        when(trailer.getBodyEidx()).thenReturn(fixByte.length - 7);

        // Parse the body
        FixMessage message = FixMessageFactory.createFixMessage(fixByte, header, trailer);

        // Verify message type
        assertEquals(message.getClass(), NewOrderMessage.class);

        // Verify body field
        if(message instanceof NewOrderMessage) {
            NewOrderMessage noMsg = (NewOrderMessage) message;
            assertEquals("ABCDEFG", new String(noMsg.getClOrdID()));
            assertEquals('3', (char)noMsg.getSide());
            assertEquals(0.0, noMsg.getPrice());
            assertEquals('1', (char)noMsg.getHandlInst());
            assertEquals('2', (char)noMsg.getOrdType());
            assertEquals("IBM", new String(noMsg.getSymbol()));
            assertEquals(LocalDateTime.parse("20240215-01:14:49", DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss")), noMsg.getTransactTime());
        } else {
            fail("Message is not of type NewOrderMessage");
        }
    }

    @Test
    public void parserTest_missingRequiredFields() {
        // 11, 21, 40,54, 55, 60
        // Create a sample FIX message byte array for the body
        byte[] fixByte = "8=FIX.4.2\u00019=251\u000135=D\u000149=AFUNDMGR\u000140=2\u000154=3\u000163=0\u000164=20030621\u0001110=1000\u0001111=50000\u000155=IBM\u000110=127\u0001".getBytes();
        when(trailer.getBodyEidx()).thenReturn(fixByte.length - 7);

        // Parse the body
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                    FixMessage message = FixMessageFactory.createFixMessage(fixByte, header, trailer);
                });

        String expectedMessage = ExceptionMessages.MISSING_REQUIRED_FIELDS;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void parserTest_invalidTimestamp() {
        // 11, 21, 40,54, 55, 60
        // Create a sample FIX message byte array for the body
        byte[] fixByte = "8=FIX.4.2\u00019=251\u000135=D\u000121=1\u000149=AFUNDMGR\u000111=ABCDEFG\u000160=202421501:14:49\u000140=2\u000154=3\u000163=0\u000164=20030621\u0001110=1000\u0001111=50000\u000155=IBM\u000110=127\u0001".getBytes();
        when(trailer.getBodyEidx()).thenReturn(fixByte.length - 7);

        // Parse the body
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            FixMessage message = FixMessageFactory.createFixMessage(fixByte, header, trailer);
        });

        String expectedMessage = ExceptionMessages.INVALID_TIMESTAMP_FORMAT;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}

