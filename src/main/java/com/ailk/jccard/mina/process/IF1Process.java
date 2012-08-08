package com.ailk.jccard.mina.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.phw.core.lang.Collections;
import org.phw.ibatis.engine.PDao;

import com.ailk.jccard.mina.bean.AppItemBean;
import com.ailk.jccard.mina.bean.rsp.JCIF1Rsp01BodyBean;
import com.ailk.jccard.mina.bean.rsp.JCIF1Rsp02BodyBean;
import com.ailk.jccard.mina.bean.rsp.JCIF1Rsp04BodyBean;
import com.ailk.phw.frombytes.ObjectFromBytes;

public class IF1Process extends IFProcess {

    private int jobType;

    private String id;

    public IF1Process(PDao dao, String ifNo, int jobType, String id) {
        super(dao, ifNo);
        this.jobType = jobType;
        this.id = id;
    }

    @Override
    public Map getResponseInfo(ObjectFromBytes objectFromBytes, byte[] bytes) {
        if (jobType == 1) {
            JCIF1Rsp01BodyBean body = objectFromBytes.fromBytes(bytes, JCIF1Rsp01BodyBean.class);
            Map insertParam = Collections.asMap("RSP_RESULT", body.getResultCode(),
                    "CARD_PRODUCT_NAME", body.getMerchantName(), "USERFLAG", body.getUserFlag());
            List<Map> insertAppList = fetchAppList(id, body.getApps());

            return Collections.asMap("insertParam", insertParam, "insertAppList", insertAppList);
        } else if (jobType == 2 || jobType == 3) {
            JCIF1Rsp02BodyBean body = objectFromBytes.fromBytes(bytes, JCIF1Rsp02BodyBean.class);
            Map insertParam = Collections.asMap("RSP_RESULT", body.getResultCode());
            List<Map> insertAppList = fetchAppList(id, body.getApps());

            return Collections.asMap("insertParam", insertParam, "insertAppList", insertAppList);
        } else {
            JCIF1Rsp04BodyBean body = objectFromBytes.fromBytes(bytes, JCIF1Rsp04BodyBean.class);
            Map insertParam = Collections.asMap("RSP_RESULT", body.getResultCode());

            return Collections.asMap("insertParam", insertParam);
        }
    }

    @Override
    public Object getRequestInfo(Map responseInfo) {
        return null;
    }

    private List<Map> fetchAppList(String id, List<AppItemBean> appList) {
        List<Map> result = new ArrayList<Map>();
        for (AppItemBean app : appList) {
            result.add(Collections.asMap("ID", id, "NAME", app.getAppName(), "AID", app.getAppAid(),
                    "APPSIZE", app.getAppSize(), "MAYOPERATE", app.getAppOperateType(),
                    "SUPPLIERNAME", app.getProvider(), "FEEDESC", app.getFeeDesc()));
        }
        return result;
    }

}
