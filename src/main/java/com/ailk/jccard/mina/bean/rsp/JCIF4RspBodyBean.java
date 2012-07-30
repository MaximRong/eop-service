package com.ailk.jccard.mina.bean.rsp;

public class JCIF4RspBodyBean {

    private short resultCode;

    private String atr;

    public void setResultCode(short resultCode) {
        this.resultCode = resultCode;
    }

    public short getResultCode() {
        return resultCode;
    }

    public void setAtr(String atr) {
        this.atr = atr;
    }

    public String getAtr() {
        return atr;
    }

}
