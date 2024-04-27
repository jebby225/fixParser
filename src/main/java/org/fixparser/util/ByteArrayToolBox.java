package org.fixparser.util;

import org.fixparser.constant.ExceptionMessages;
import org.fixparser.constant.FixConstants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class ByteArrayToolBox {

    public static int bytesToInt(byte[] inputArr, int sIdx, int eIdx) {
        int value = 0;
        for(int i = sIdx; i < eIdx; i++) {
            if(inputArr[i] > FixConstants.DIGIT_9 || inputArr[i]  < FixConstants.DIGIT_0) {  // Not number
                throw new IllegalArgumentException(ExceptionMessages.INVALID_VALUE_TYPE);
            }
            value = (value * 10) + (inputArr[i] - '0');
        }
        return value;
    }

    public static int bytesToInt(byte[] inputArr) {
        return bytesToInt(inputArr, 0 , inputArr.length);
    }

    public static double bytesToDouble(byte[] inputArr, int sIdx, int eIdx) {
        double result = 0;
        boolean isNegative = (inputArr[sIdx] == '-');
        int i = sIdx;

        if (isNegative) {
            sIdx++;
        }

        while(i < eIdx && inputArr[i] != '.') {
            if(inputArr[i] > FixConstants.DIGIT_9 || inputArr[i] < FixConstants.DIGIT_0) {  // Not number
                throw new IllegalArgumentException(ExceptionMessages.INVALID_VALUE_TYPE);
            }
            result = result * 10 + (inputArr[i] - '0');
            i++;
        }
        i++;
        double decimal = 0.1;
        while(i < eIdx) {
            if(inputArr[i] > FixConstants.DIGIT_9 || inputArr[i] < FixConstants.DIGIT_0) {  // Not number
                throw new IllegalArgumentException(ExceptionMessages.INVALID_VALUE_TYPE);
            }
            result += (inputArr[i] - '0') * decimal;
            decimal *= 0.1;
            i++;
        }
        return isNegative ? -result : result;
    }

    /**********
        Time/date combination represented in UTC (Universal Time Coordinated, also known as "GMT") in either
        YYYYMMDD-HH:MM:SS (whole seconds) or
        YYYYMMDD-HH:MM:SS.sss (milliseconds) format, colons, dash, and period required.
    ***********/
    public static LocalDateTime bytesToUTCTimestamp(byte[] inputArr, int sIdx, int eIdx) {
        try {
            if(eIdx - sIdx < 17) {
                throw new IllegalArgumentException(ExceptionMessages.INVALID_TIMESTAMP_FORMAT);
            }

            int year = ((inputArr[sIdx] - '0') * 1000) + ((inputArr[sIdx + 1] - '0') * 100) + ((inputArr[sIdx + 2] - '0') * 10) + (inputArr[sIdx + 3] - '0');
            int month = ((inputArr[sIdx + 4] - '0') * 10) + (inputArr[sIdx + 5] - '0');
            int day = ((inputArr[sIdx + 6] - '0') * 10) + (inputArr[sIdx + 7] - '0');
            int hour = ((inputArr[sIdx + 9] - '0') * 10) + (inputArr[sIdx + 10] - '0');
            int minute = ((inputArr[sIdx + 12] - '0') * 10) + (inputArr[sIdx + 13] - '0');
            int second = ((inputArr[sIdx + 15] - '0') * 10) + (inputArr[sIdx + 16] - '0');
            int millisecond = 0;
            if(eIdx - sIdx == 21) { // hav milliseconds
                millisecond = ((inputArr[sIdx + 18] - '0') * 100) + ((inputArr[sIdx + 19] - '0') * 10) + (inputArr[sIdx + 20] - '0');
            }
            return LocalDateTime.of(year, month, day, hour, minute, second, millisecond * 1000000);
        } catch (Exception e) {
            throw new IllegalArgumentException(ExceptionMessages.INVALID_TIMESTAMP_FORMAT);
        }
    }

    public static void bytesToFile(byte[] inputArr, String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(inputArr);
            System.out.println("Byte array has been written to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int countOccurrences(byte[] inputArr, char c, int sIdx) {
        int cnt = 0;
        for(int i = sIdx; i < inputArr.length; i++) {
            if (inputArr[i] == c) {
                cnt++;
            }
        }
        return cnt + 1; // One more than the delimiter count
    }

}
