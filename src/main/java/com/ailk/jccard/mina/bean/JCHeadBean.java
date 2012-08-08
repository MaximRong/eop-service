package com.ailk.jccard.mina.bean;

import com.ailk.phw.annotations.JCBytes;
import com.ailk.phw.enums.JCExpType;

public class JCHeadBean {

    @JCBytes(type = JCExpType.ASCII, length = 20)
    private String sessionId;

    private byte typeFlag;

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
