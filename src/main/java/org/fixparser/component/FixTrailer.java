package org.fixparser.component;

import org.fixparser.constant.ExceptionMessages;
import org.fixparser.constant.FixConstants;
import org.fixparser.util.ByteArrayToolBox;

import java.util.Arrays;

public class FixTrailer implements FixComponent {

    private int checkSum;
    private byte[] checkSumArr;

    private int bodyEidx; // to indicate where is the FIX message body starts

    public int getCheckSum() {
        return checkSum;
    }

    public int getBodyEidx() {
        return bodyEidx;
    }
   
    /*********
        FIX trailer is expected to end with the tag 10:
        10=1
     *********/
    public boolean parse(byte[] fixMsgArr) {
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
                checkSumArr = new byte[eIdx - sIdx + 1];
                System.arraycopy(fixMsgArr, sIdx, checkSumArr, 0, eIdx - sIdx + 1);
                checkSum = ByteArrayToolBox.bytesToInt(checkSumArr);
                bodyEidx = i + 1;
                return true;
            }
        }
        return false;
    }

    @Override
    public byte[] getValueByTag(byte[] tag) {
        if(Arrays.equals(tag, "10".getBytes())) {
            return checkSumArr;
        }
        return null;
       /* switch (tag) {
            case 10:
                return checkSumArr;
            default:
                return null;
        } */
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Fix Trailer{").append("\n")
                .append("checkSum=").append(checkSum).append("\n")
                .append("}");
        return sb.toString();
    }
}
