package com.ailk.jccard.mina.process;

import java.util.Map;

import org.phw.core.lang.Collections;
import org.phw.ibatis.engine.PDao;

import com.ailk.jccard.mina.bean.req.JCIF3ReqBodyBean;
import com.ailk.jccard.mina.bean.rsp.JCIF3RspBodyBean;
import com.ailk.phw.frombytes.ObjectFromBytes;
import com.ailk.phw.utils.ObjectUtils;

public class IF3Process extends IFProcess {

    public IF3Process(PDao dao, String ifNo) {
        super(dao, ifNo);
    }

    @Override
    public Map getResponseInfo(ObjectFromBytes objectFromBytes, byte[] bytes) {
        JCIF3ReqBodyBean body = objectFromBytes.fromBytes(bytes, JCIF3ReqBodyBean.class);
        return Collections.asMap("insertParam", Collections.asMap("RSPDATA", body.getData()));
    }

    @Override
    public Object getRequestInfo(Map responseInfo) {
        return ObjectUtils.populateBean(Collections.asMap("resultCode", 0), JCIF3RspBodyBean.class);
    }

    @Override
    protected Map queryRequestInfo(Map insertParam) {
        return null;
    }

}
