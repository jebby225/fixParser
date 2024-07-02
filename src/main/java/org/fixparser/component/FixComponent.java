package org.fixparser.component;

public interface FixComponent {
    boolean parse(byte[] fixMsgArr);
    byte[] getValueByTag(byte[] tag);
}