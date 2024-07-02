package org.fixparser.factory;

import org.fixparser.component.FixHeader;
import org.fixparser.component.FixTrailer;
import org.fixparser.component.TagValuePair;
import org.fixparser.constant.ExceptionMessages;
import org.fixparser.constant.FixConstants;
import org.fixparser.util.ByteArrayWrapper;

public class GenericMessage extends FixMessage {

    private GenericMessage(FixHeader header, FixTrailer trailer, int requiredFieldCount) {
      super(header, trailer, requiredFieldCount);
    }

    @Override
    public boolean parse(byte[] fixMsgArr) {
        int sIdx = header.getBodyIdx();
        int eIdx = 0;
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

                TagValuePair<byte[], byte[]> tagValue = new TagValuePair<>(new byte[eIdx - sIdx], new byte[i  - eIdx - 1]);
                System.arraycopy(fixMsgArr, sIdx, tagValue.getTag(), 0, eIdx - sIdx);
                System.arraycopy(fixMsgArr, eIdx + 1 , tagValue.getValue(), 0, i  - eIdx -1);

                //new TagValuePair<>(sIdx, eIdx, eIdx+2, i - 1);
                //tagValuePairs[idx] = tagValue;
                tagValueMap.put(new ByteArrayWrapper(tagValue.getTag()), tagValue.getValue());
                idx++;
                sIdx = i + 1;
            }
        }
        return true;
    }

}
