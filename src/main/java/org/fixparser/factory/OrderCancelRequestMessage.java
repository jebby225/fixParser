package org.fixparser.factory;

import org.fixparser.component.FixHeader;
import org.fixparser.component.FixTrailer;
import org.fixparser.component.TagValuePair;
import org.fixparser.util.ByteArrayToolBox;
import org.fixparser.constant.ExceptionMessages;
import org.fixparser.constant.FixConstants;

import java.time.LocalDateTime;

public class OrderCancelRequestMessage extends FixMessage {

    private byte[] origClOrdID;             // tag 41
    private byte[] clOrdID;                 // tag 11
    private char side;                      // tag 54
    private LocalDateTime transactTime;     // tag 60
    private byte[] account;                 // tag 1 (not required)
    public OrderCancelRequestMessage(FixHeader header, FixTrailer trailer, int requiredFieldCount) {
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
                } else if(eIdx-sIdx == 2 && fixMsgArr[sIdx] == '4' && fixMsgArr[eIdx - 1] == '1') {
                    origClOrdID = new byte[i - eIdx - 1];
                    System.arraycopy(fixMsgArr, eIdx + 1, origClOrdID, 0, i - eIdx - 1);
                    reqTagCnt++;
                } else if(eIdx-sIdx == 2 && fixMsgArr[sIdx] == '5' && fixMsgArr[eIdx - 1] == '4') {
                    side = (char) fixMsgArr[i - 1];
                    reqTagCnt++;
                } else if(eIdx-sIdx == 2 && fixMsgArr[sIdx] == '6' && fixMsgArr[eIdx - 1] == '0') {
                    transactTime = ByteArrayToolBox.bytesToUTCTimestamp(fixMsgArr, eIdx + 1, i);
                    reqTagCnt++;
                } else if(eIdx-sIdx == 1 && fixMsgArr[sIdx] == '1') {
                    account = new byte[i - eIdx - 1];
                    System.arraycopy(fixMsgArr, eIdx + 1, account, 0, i - eIdx - 1);
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
                .append("OrderCancelRequestMessage{").append("\n")
                .append("origClOrdID=").append(new String(origClOrdID)).append("\n")
                .append("clOrdID=").append(new String(clOrdID)).append("\n")
                .append("side=").append(side).append("\n")
                .append("account=").append(account == null ? " " : new String(account)).append("\n")
                .append(displayTagValuePairsArr()).append("\n")
                .append("}").append("\n")
                .append(trailer.toString());
        return sb.toString();
    }

    public byte[] getOrigClOrdID() {
        return origClOrdID;
    }

    public byte[] getClOrdID() {
        return clOrdID;
    }

    public char getSide() {
        return side;
    }

    public LocalDateTime getTransactTime() {
        return transactTime;
    }

    public byte[] getAccount() {
        return account;
    }
}
