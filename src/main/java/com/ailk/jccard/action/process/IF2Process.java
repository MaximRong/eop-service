package com.ailk.jccard.action.process;

import java.util.Map;

import org.phw.core.lang.Collections;
import org.phw.ibatis.engine.PDao;

import com.ailk.jccard.mina.bean.req.JCIF2ReqBodyBean;
import com.ailk.jccard.mina.bean.rsp.JCIF2RspBodyBean;
import com.ailk.phw.fromBytes.ObjectFromBytes;
import com.ailk.phw.utils.ObjectUtils;

public class IF2Process extends IFProcess {

    public IF2Process(PDao dao, String ifNo) {
        super(dao, ifNo);
    }

    @Override
    public Map getRequestInfo(ObjectFromBytes objectFromBytes, byte[] bytes) {
        JCIF2ReqBodyBean body = objectFromBytes.fromBytes(bytes, JCIF2ReqBodyBean.class);
        return Collections.asMap("insertParam", Collections.asMap("RSPDATA", body.getApdu()));
    }

    @Override
    public Object getResponseInfo(Map responseInfo) {
        JCIF2RspBodyBean rspBody = ObjectUtils.populateBean(Collections.asMap(
                "resultCode", responseInfo.get("RSP_RESULT"), "apdu", responseInfo.get("RSPDATA")),
                JCIF2RspBodyBean.class);
        return rspBody;
    }

}
