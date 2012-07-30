package com.ailk.jccard.mina.bean.req;

import com.ailk.phw.annotations.JCBytes;
import com.ailk.phw.utils.ConstantUtils;

public class JCIF3ReqBodyBean {

    @JCBytes(charset = ConstantUtils.CHARSET_UNICODE)
    private String data;

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

}
