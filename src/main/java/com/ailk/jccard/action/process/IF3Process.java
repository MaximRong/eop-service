package com.ailk.jccard.action.process;

import java.util.Map;

import org.phw.core.lang.Collections;
import org.phw.ibatis.engine.PDao;

import com.ailk.jccard.mina.bean.req.JCIF3ReqBodyBean;
import com.ailk.jccard.mina.bean.rsp.JCIF3RspBodyBean;
import com.ailk.phw.fromBytes.ObjectFromBytes;
import com.ailk.phw.utils.ObjectUtils;

public class IF3Process extends IFProcess {

    public IF3Process(PDao dao, String ifNo) {
        super(dao, ifNo);
    }

    @Override
    public Map getRequestInfo(ObjectFromBytes objectFromBytes, byte[] bytes) {
        JCIF3ReqBodyBean body = objectFromBytes.fromBytes(bytes, JCIF3ReqBodyBean.class);
        return Collections.asMap("insertParam", Collections.asMap("RSPDATA", body.getData()));
    }

    @Override
    public Object getResponseInfo(Map responseInfo) {
        JCIF3RspBodyBean rspBody = ObjectUtils.populateBean(Collections.asMap(
                "resultCode", responseInfo.get("RSP_RESULT")), JCIF3RspBodyBean.class);
        return rspBody;
    }

}
