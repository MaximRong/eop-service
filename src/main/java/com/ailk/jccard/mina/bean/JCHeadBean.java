package com.ailk.jccard.mina.bean;

import com.ailk.phw.annotations.JCBytes;
import com.ailk.phw.annotations.JCToBytesTransient;
import com.ailk.phw.enums.JCExpType;

public class JCHeadBean {

    @JCToBytesTransient
    private short msgDataLength;

    @JCBytes(type = JCExpType.ASCII, length = 20)
    private String sessionId;

    private byte typeFlag;

    public void setMsgDataLength(short msgDataLength) {
        this.msgDataLength = msgDataLength;
    }

    public short getMsgDataLength() {
        return msgDataLength;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setTypeFlag(byte typeFlag) {
        this.typeFlag = typeFlag;
    }

    public byte getTypeFlag() {
        return typeFlag;
    }

}
