package com.ailk.jccard.mina.bean.rsp;

public class JCIF2RspBodyBean {

    private short resultCode;

    private String apdu;

    public void setResultCode(short resultCode) {
        this.resultCode = resultCode;
    }

    public short getResultCode() {
        return resultCode;
    }

    public void setApdu(String apdu) {
        this.apdu = apdu;
    }

    public String getApdu() {
        return apdu;
    }

}
