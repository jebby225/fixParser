package org.fixparser.factory;

import org.fixparser.component.FixHeader;
import org.fixparser.component.FixTrailer;
import org.fixparser.component.TagValuePair;
import org.fixparser.util.ByteArrayToolBox;
import org.fixparser.constant.ExceptionMessages;
import org.fixparser.constant.FixConstants;

import java.time.LocalDateTime;

public class NewOrderMessage extends FixMessage {
    private byte[] clOrdID;              // tag 11
    private char handlInst;              // tag 21
    private char ordType;                // tag 40
    private char side;                   // tag 54
    private byte[] symbol;               // tag 55
    private LocalDateTime transactTime;  // tag 60
    private double price;                 // tag 44 (Not required)


    public byte[] getClOrdID() {
        return clOrdID;
    }

    public char getHandlInst() {
        return handlInst;
    }

    public char getOrdType() {
        return ordType;
    }

    public char getSide() {
        return side;
    }

    public byte[] getSymbol() {
        return symbol;
    }

    public LocalDateTime getTransactTime() {
        return transactTime;
    }

    public double getPrice() {
        return price;
    }

    public NewOrderMessage(FixHeader header, FixTrailer trailer, int requiredFieldCount) {
        super(header, trailer, requiredFieldCount);
    }

    @Override
    public boolean parse(byte[] fixMsgArr) {
        int sIdx = header.getBodyIdx();
        int eIdx = 0;
        int reqTagCnt = 0;
        int idx = 0;

        for(int i = header.getBodyIdx(); i < trailer.getBodyEidx(); i++) {
            if(fixMsgArr[i] == FixConstants.EQUAL) {
                eIdx = i;
            } else if(fixMsgArr[i] == FixConstants.SOH) {
                if(eIdx - sIdx <= 0) {  // if tag is empty
                    throw new IllegalArgumentException(ExceptionMessages.EMPTY_TAG_VALUE_PAIR);
                } else if(i  - eIdx -1 <= 0) {  // if value is empty
                    throw new IllegalArgumentException(ExceptionMessages.EMPTY_TAG_VALUE_PAIR);
                }

                if(eIdx-sIdx == 2 && fixMsgArr[sIdx] == '1' && fixMsgArr[eIdx - 1] == '1') {
                    clOrdID = new byte[i - eIdx - 1];
                    System.arraycopy(fixMsgArr, eIdx + 1, clOrdID, 0, i - eIdx - 1);
                    reqTagCnt++;
                } else if(eIdx-sIdx == 2 && fixMsgArr[sIdx] == '2' && fixMsgArr[eIdx - 1] == '1') {
                    handlInst = (char) fixMsgArr[i- 1];
                    reqTagCnt++;
                } else if(eIdx-sIdx == 2 && fixMsgArr[sIdx] == '4' && fixMsgArr[eIdx - 1] == '0') {
                    ordType = (char) fixMsgArr[i - 1];
                    reqTagCnt++;
                } else if(eIdx-sIdx == 2 && fixMsgArr[sIdx] == '5' && fixMsgArr[eIdx - 1] == '4') {
                    side = (char) fixMsgArr[i - 1];
                    reqTagCnt++;
                } else if(eIdx-sIdx == 2 && fixMsgArr[sIdx] == '5' && fixMsgArr[eIdx - 1] == '5') {
                    symbol = new byte[i - eIdx - 1];
                    System.arraycopy(fixMsgArr, eIdx + 1, symbol, 0, i - eIdx - 1);
                    reqTagCnt++;
                } else if(eIdx-sIdx == 2 && fixMsgArr[sIdx] == '6' && fixMsgArr[eIdx - 1] == '0') {
                    transactTime = ByteArrayToolBox.bytesToUTCTimestamp(fixMsgArr, eIdx + 1, i);
                    reqTagCnt++;
                } else if(eIdx-sIdx == 2 && fixMsgArr[sIdx] == '4' && fixMsgArr[eIdx - 1] == '4') {
                    price = ByteArrayToolBox.bytesToDouble(fixMsgArr, eIdx + 1, i);
                } else {  // other tags
                    TagValuePair<byte[], byte[]> tagValue = new TagValuePair<>(new byte[eIdx - sIdx], new byte[i  - eIdx - 1]);
                    System.arraycopy(fixMsgArr, sIdx, tagValue.getTag(), 0, eIdx - sIdx);
                    System.arraycopy(fixMsgArr, eIdx + 1 , tagValue.getValue(), 0, i  - eIdx -1);

                    //new TagValuePair<>(sIdx, eIdx, eIdx+2, i - 1);
                    tagValuePairs[idx] = tagValue;
                    idx++;
                }
                sIdx = i + 1;
            }
        }

        if(reqTagCnt != requiredFieldCount) {
            throw new IllegalArgumentException(ExceptionMessages.MISSING_REQUIRED_FIELDS);
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(header.toString()).append("\n")
          .append("NewOrderMessage{").append("\n")
          .append("clOrdID=").append(new String(clOrdID)).append("\n")
          .append("handlInst=").append(handlInst).append("\n")
          .append("ordType=").append(ordType).append("\n")
          .append("side=").append(side).append("\n")
          .append("symbol=").append(new String(symbol)).append("\n")
          .append("transactTime=").append(transactTime).append("\n")
          .append("price=").append(price).append("\n")
          .append(displayTagValuePairsArr()).append("\n")
          .append("}").append("\n")
          .append(trailer.toString());
        return sb.toString();
    }
}
