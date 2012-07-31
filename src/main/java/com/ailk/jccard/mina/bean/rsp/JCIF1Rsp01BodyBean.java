package com.ailk.jccard.mina.bean.rsp;

import java.util.ArrayList;
import java.util.List;

import com.ailk.jccard.mina.bean.AppItemBean;
import com.ailk.phw.annotations.JCBytes;
import com.ailk.phw.enums.JCLenType;
import com.ailk.phw.utils.ConstantUtils;
import com.alibaba.fastjson.JSON;

public class JCIF1Rsp01BodyBean {

    private short resultCode;

    @JCBytes(charset = ConstantUtils.CHARSET_UNICODE)
    private String merchantName;

    @JCBytes(length = 3)
    private String availSpace;

    private byte userFlag;

    @JCBytes(lenType = JCLenType.Short)
    private List<AppItemBean> apps;

    public void setResultCode(short resultCode) {
        this.resultCode = resultCode;
    }

    public short getResultCode() {
        return resultCode;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setAvailSpace(String availSpace) {
        this.availSpace = availSpace;
    }

    public String getAvailSpace() {
        return availSpace;
    }

    public void setUserFlag(byte userFlag) {
        this.userFlag = userFlag;
    }

    public byte getUserFlag() {
        return userFlag;
    }

    public void setApps(List<AppItemBean> apps) {
        this.apps = apps;
    }

    public List<AppItemBean> getApps() {
        return apps;
    }

    public static void main(String[] args) {
        JCIF1Rsp01BodyBean bodyBean = new JCIF1Rsp01BodyBean();
        bodyBean.setResultCode((short) 0);
        bodyBean.setMerchantName("MerchantName");
        bodyBean.setAvailSpace("31");
        bodyBean.setUserFlag((byte) 0);

        ArrayList<AppItemBean> appItems = new ArrayList<AppItemBean>();
        AppItemBean itemBean1 = new AppItemBean();
        itemBean1.setAppAid("adasd");
        itemBean1.setAppName("名称");
        itemBean1.setAppOperateType((byte) 0);
        itemBean1.setAppSize((short) 1024);
        itemBean1.setProvider("Provider");
        itemBean1.setProductId("007");
        itemBean1.setFeeDesc("007");

        AppItemBean itemBean2 = new AppItemBean();
        itemBean2.setAppAid("ddddd");
        itemBean2.setAppName("名称2");
        itemBean2.setAppOperateType((byte) 0);
        itemBean2.setAppSize((short) 2048);
        itemBean2.setProvider("Provider");
        itemBean2.setProductId("007");
        appItems.add(itemBean1);
        appItems.add(itemBean2);
        itemBean2.setFeeDesc("非要有么？");

        bodyBean.setApps(appItems);
        System.out.println(JSON.toJSONString(bodyBean));
    }
}
