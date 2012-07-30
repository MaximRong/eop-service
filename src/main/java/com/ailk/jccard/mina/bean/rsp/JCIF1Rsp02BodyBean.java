package com.ailk.jccard.mina.bean.rsp;

import java.util.List;

import com.ailk.jccard.mina.bean.AppItemBean;
import com.ailk.phw.annotations.JCBytes;
import com.ailk.phw.enums.JCLenType;

public class JCIF1Rsp02BodyBean {

    private short resultCode;

    @JCBytes(lenType = JCLenType.Short)
    private List<AppItemBean> apps;

    public void setResultCode(short resultCode) {
        this.resultCode = resultCode;
    }

    public short getResultCode() {
        return resultCode;
    }

    public void setApps(List<AppItemBean> apps) {
        this.apps = apps;
    }

    public List<AppItemBean> getApps() {
        return apps;
    }

}
