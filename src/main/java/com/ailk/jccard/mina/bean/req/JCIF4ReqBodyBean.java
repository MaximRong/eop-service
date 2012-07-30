package com.ailk.jccard.mina.bean.req;

import com.ailk.phw.annotations.JCBytes;

public class JCIF4ReqBodyBean {

    @JCBytes(length = 0)
    private String nullContent;

    public void setNullContent(String nullContent) {
        this.nullContent = nullContent;
    }

    public String getNullContent() {
        return nullContent;
    }

}
