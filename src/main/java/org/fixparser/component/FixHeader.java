package org.fixparser.component;
import org.fixparser.constant.ExceptionMessages;
import org.fixparser.constant.FixConstants;
import java.util.Arrays;
import static org.fixparser.util.ByteArrayToolBox.bytesToInt;

public class FixHeader implements FixComponent {
    private byte[] fixVersion;
    private int bodyLength;
    private byte[] bodyLenArr;
    private byte[] msgType;
    private int bodyIdx;    // to indicate where is the FIX message body starts

    public byte[] getFixVersion() {
        return fixVersion;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public byte[] getMsgType() {
        return msgType;
    }

    public int getBodyIdx() {
        return bodyIdx;
    }

    /************
        FIX header is expected to start with the tag 8, 9, and 35 in order; for instance:
        8=FIX.4.254=19=25135=D
     ***********/
    public boolean parse(byte[] fixMsgArr) {
        int sIdx = 0;
        int eIdx = 0;
        int tagCount = 1;

        if(fixMsgArr == null || fixMsgArr.length == 0) {
            throw new IllegalArgumentException(ExceptionMessages.INVALID_HEADER);
        }

        for(int i = 0; i < fixMsgArr.length; i++) {
            if(fixMsgArr[i] == FixConstants.EQUAL) {
                eIdx = i;
            } else if(fixMsgArr[i] == FixConstants.SOH) {
                if(i - eIdx - 1 <= 0) {     // if value is empty
                    throw new IllegalArgumentException(ExceptionMessages.INVALID_HEADER);
                }

                switch (tagCount) {
                    case 1: // Tag 8 (Fix Version)
                        if(fixMsgArr[sIdx] != '8') {
                            throw new IllegalArgumentException(ExceptionMessages.INVALID_HEADER);
                        }
                        fixVersion = new byte[i  - eIdx - 1];
                        System.arraycopy(fixMsgArr, eIdx + 1, fixVersion, 0, i  - eIdx - 1);
                        break;
                    case 2: // Tag 9 (Body Length)
                        if(fixMsgArr[sIdx] != '9') {
                            throw new IllegalArgumentException(ExceptionMessages.INVALID_HEADER);
                        }
                        bodyLenArr = new byte[i  - eIdx - 1];
                        System.arraycopy(fixMsgArr, eIdx + 1, bodyLenArr, 0, i  - eIdx - 1);
                        bodyLength = bytesToInt(bodyLenArr);
                        break;
                    case 3: // Tag 35 (Msg Type)
                        if(eIdx - sIdx != 2 ||  fixMsgArr[sIdx] != '3' || fixMsgArr[eIdx - 1] != '5') {
                            throw new IllegalArgumentException(ExceptionMessages.INVALID_HEADER);
                        }
                        msgType = new byte[i  - eIdx - 1];
                        System.arraycopy(fixMsgArr, eIdx + 1, msgType, 0, i  - eIdx - 1);
                        bodyIdx = i + 1;
                        return true;
                    default:
                        return false;
                }
                tagCount++;
                sIdx = i + 1;
            }
        }
        return false;
    }

    @Override
    public byte[] getValueByTag(int tag) {
        switch (tag) {
            case 8:
                return fixVersion;
            case 9:
                return bodyLenArr;
            case 35:
                return msgType;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Fix Header{").append("\n")
                .append("fixVersion=").append(new String(fixVersion)).append("\n")
                .append("bodyLength=").append(bodyLength).append("\n")
                .append("msgType=").append(new String(msgType)).append("\n")
                .append("}");
        return sb.toString();
    }

}
