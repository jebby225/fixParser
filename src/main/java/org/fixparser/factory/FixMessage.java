package org.fixparser.factory;

import org.fixparser.component.FixComponent;
import org.fixparser.component.FixHeader;
import org.fixparser.component.FixTrailer;
import org.fixparser.component.TagValuePair;
import org.fixparser.constant.ExceptionMessages;
import org.fixparser.constant.FixConstants;
import org.fixparser.util.ByteArrayToolBox;
import org.fixparser.util.ByteArrayWrapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class FixMessage implements FixComponent {
    protected final FixHeader header;
    protected final FixTrailer trailer;
    protected final int requiredFieldCount;
    //protected TagValuePair<byte[], byte[]>[] tagValuePairs;
    protected HashMap<ByteArrayWrapper, byte[]> tagValueMap;

    protected FixMessage(FixHeader header, FixTrailer trailer, int requiredFieldCount) {
        this.header = header;
        this.trailer = trailer;
        this.requiredFieldCount = requiredFieldCount;
    }

    protected final void initializeTagValuePairsArr(byte[] inputArr) {
        tagValueMap = new HashMap<>();
        //int cnt = ByteArrayToolBox.countOccurrences(inputArr, (char) FixConstants.SOH, header.getBodyIdx()) ;
        //tagValuePairs = new TagValuePair[cnt];
    }

    protected final String displayTagValuePairsArr() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<ByteArrayWrapper, byte[]> entry : tagValueMap.entrySet()) {
            if(entry == null) {
                return sb.toString();
            }
            sb.append(entry.getKey()).append("=").append(new String(entry.getValue())).append("\n");
        }

       /* for(TagValuePair<byte[], byte[]> tvp : this.tagValuePairs) {
             if(tvp == null) {
                return sb.toString();
            }
            sb.append(new String(tvp.getTag())).append("=").append(new String(tvp.getValue())).append("\n");
        } */
        return sb.toString();
    }

    @Override
    public byte[] getValueByTag(byte[] tag) {
        return tagValueMap.get(new ByteArrayWrapper(tag));
        /*
        outterLoop:
        for(TagValuePair<byte[], byte[]> tvp : this.tagValuePairs) {
            if (tvp == null) {
                System.out.println(String.format(ExceptionMessages.TAG_NOT_FOUND, tag));
                return null;
            } else {
                int i = tvp.getTag().length - 1;
                int tagNum = tag;
                while (i >= 0 ){//&& tagNum != 0) {
                    if(tagNum == 0 && i == 0) {
                        continue outterLoop;
                    }
                    int d = tagNum % 10;
                    if (tvp.getTag()[i] != d + '0') {
                        continue outterLoop;
                    }

                    i--;
                    tagNum /= 10;
                }

                return tvp.getValue();
            }
        }
        System.out.println(String.format(ExceptionMessages.TAG_NOT_FOUND, tag));
        return null; */
    }
}
