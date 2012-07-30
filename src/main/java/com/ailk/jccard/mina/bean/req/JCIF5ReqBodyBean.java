package com.ailk.jccard.mina.bean.req;

import com.ailk.phw.annotations.JCBytes;
import com.ailk.phw.enums.JCExpType;

public class JCIF5ReqBodyBean {

    @JCBytes(type = JCExpType.Octet)
    private byte idType;

    private String id;

    @JCBytes(type = JCExpType.Octet, length = 2)
    private String provinceId;

    public void setIdType(byte idType) {
        this.idType = idType;
    }

    public byte getIdType() {
        return idType;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceId() {
        return provinceId;
    }

}
