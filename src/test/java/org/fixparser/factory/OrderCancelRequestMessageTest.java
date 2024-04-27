package org.fixparser.factory;

import org.fixparser.component.FixComponent;
import org.fixparser.component.FixHeader;
import org.fixparser.component.FixTrailer;
import org.fixparser.component.GenericFixMessage;
import org.fixparser.service.FixParserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

public class OrderCancelRequestMessageTest {
    @Mock
    private FixHeader header;
    @Mock
    private FixTrailer trailer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Mock header
        when(header.getMsgType()).thenReturn("F". getBytes());
        when(header.getBodyIdx()).thenReturn(21);
        when(header.getFixVersion()).thenReturn("FIX.4.4".getBytes());
        when(header.getBodyLength()).thenReturn(251);

        // Mock trailer
        when(trailer.getCheckSum()).thenReturn(127);
    }
    @Test
    public void parserTest_validFixMessage() {
        String input = "8=FIX.4.4\u00019=122\u000135=F\u000134=1\u000149=SenderCompID\u000156=TargetCompID\u000141=origClOrdID\u000160=20240101-10:00:00\u000111=OrderID123\u000155=MSFT\u000154=1\u000138=100\u000140=1\u000159=0\u000110=232\u0001";
        when(trailer.getBodyEidx()).thenReturn(input.getBytes().length);
        // Parse the body
        FixMessage message = FixMessageFactory.createFixMessage(input.getBytes(), header, trailer);

        // Verify message type
        assertEquals(message.getClass(), OrderCancelRequestMessage.class);

        // Verify body field
        if(message instanceof OrderCancelRequestMessage) {
            OrderCancelRequestMessage ncrMsg = (OrderCancelRequestMessage) message;
            assertEquals("OrderID123", new String(ncrMsg.getClOrdID()));
            assertEquals('1', (char)ncrMsg.getSide());
            assertEquals(null, ncrMsg.getAccount());
            assertEquals("origClOrdID", new String(ncrMsg.getOrigClOrdID()));
            assertEquals(LocalDateTime.parse("20240101-10:00:00", DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss")), ncrMsg.getTransactTime());
        } else {
            fail("Message is not of type NewOrderMessage");
        }
    }

}
