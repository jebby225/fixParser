package org.fixparser.component;

import org.fixparser.component.FixComponent;
import org.fixparser.constant.ExceptionMessages;
import org.fixparser.constant.FixConstants;
import org.fixparser.util.ByteArrayToolBox;
import org.fixparser.util.TagValueMap;
import java.util.Arrays;
import static org.fixparser.util.ByteArrayToolBox.bytesToInt;

public class GenericFixMessage implements FixComponent {
    private byte[] fixVersion;
    private int bodyLength;
    private byte[] msgType;
    private int checkSum;
    private TagValueMap tagValueMap;
    private int bodyIdx;
    private int bodyEidx;
    
    @Override
    public boolean parse(byte[] fixMsgArr) {
        if(!parseHeader(fixMsgArr)) {
            throw new NullPointerException(ExceptionMessages.INVALID_HEADER);
        }
        if(!parseTrailer(fixMsgArr)){
            throw new NullPointerException(ExceptionMessages.INVALID_TRAILER);
        }
        int cnt = ByteArrayToolBox.countOccurrences(fixMsgArr, (char) FixConstants.SOH, bodyIdx) ;
        this.tagValueMap = new TagValueMap(cnt);
        return parseBody(fixMsgArr);
    }

    private boolean parseBody(byte[] fixMsgArr) {
        int sIdx = bodyIdx;
        int eIdx = 0;
        int idx = 0;
        byte[] valueArr;

        for(int i = bodyIdx; i < bodyEidx; i++) {
            if(fixMsgArr[i] == FixConstants.EQUAL) {
                eIdx = i;
            } else if(fixMsgArr[i] == FixConstants.SOH) {
                if(eIdx - sIdx <= 0) {  // if tag is empty
                    throw new IllegalArgumentException(ExceptionMessages.EMPTY_TAG_VALUE_PAIR);
                } else if(i  - eIdx -1 <= 0) {  // if value is empty
                    throw new IllegalArgumentException(ExceptionMessages.EMPTY_TAG_VALUE_PAIR);
                }
                valueArr = new byte[i  - eIdx - 1];
                System.arraycopy(fixMsgArr, eIdx + 1 , valueArr, 0, i  - eIdx -1);
                tagValueMap.put(ByteArrayToolBox.bytesToInt(fixMsgArr, sIdx, eIdx), valueArr);
                sIdx = i + 1;
            }
        }
        return true;
    }

    /************
     FIX header is expected to start with the tag 8, 9, and 35 in order; for instance:
     8=FIX.4.254=19=25135=D
     ***********/
    private boolean parseHeader(byte[] fixMsgArr) {
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
                        bodyLength = bytesToInt(Arrays.copyOfRange(fixMsgArr, eIdx + 1, i));
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

    /*********
     FIX trailer is expected to end with the tag 10:
     10=1
     *********/
    private boolean parseTrailer(byte[] fixMsgArr) {
        int sIdx = 0;
        int eIdx = fixMsgArr.length - 2;

        if(fixMsgArr == null || fixMsgArr.length < 7 || fixMsgArr[fixMsgArr.length - 1] != FixConstants.SOH) {
            throw new IllegalArgumentException(ExceptionMessages.INVALID_TRAILER);
        }

        for(int i = fixMsgArr.length - 2; i >= 0; i--) {
            if(fixMsgArr[i] == FixConstants.EQUAL) {
                sIdx = i + 1;
            } else if(fixMsgArr[i] == FixConstants.SOH) {
                if(eIdx - sIdx + 1 <= 0) {  // if value is empty
                    throw new IllegalArgumentException(ExceptionMessages.INVALID_TRAILER);
                }
                if(i + 2 >= fixMsgArr.length || fixMsgArr[i + 1] != '1' || fixMsgArr[i + 2] != '0') {
                    throw new IllegalArgumentException(ExceptionMessages.INVALID_TRAILER);
                }
                byte[] valByte = new byte[eIdx - sIdx + 1];
                System.arraycopy(fixMsgArr, sIdx, valByte, 0, eIdx - sIdx + 1);
                checkSum = ByteArrayToolBox.bytesToInt(valByte);
                bodyEidx = i + 1;
                return true;
            }
        }
        return false;
    }

    public byte[] getFixVersion() {
        return fixVersion;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public byte[] getMsgType() {
        return msgType;
    }

    public int getCheckSum() {
        return checkSum;
    }

    @Override
    public byte[] getValueByTag(int tag) {
        return tagValueMap.get(tag);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("fixVersion=").append(new String(fixVersion)).append("\n")
          .append("bodyLength=").append(bodyLength).append("\n")
          .append("msgType=").append(new String(msgType)).append("\n")
          .append("checkSum=").append(checkSum).append("\n");
        byte[] val;
        for(int i = 0; i < tagValueMap.getSize(); i++) {
            val =tagValueMap.getValueByIndex(i);
            if( val == null) {
                return sb.toString();
            }
            sb.append(tagValueMap.getTagByIndex(i)).append("=").append(new String(val)).append("\n");
        }
        return sb.toString();
    }

}
