package com.ailk.jccard.mina.process;

import java.util.List;
import java.util.Map;

import org.phw.core.lang.Collections;
import org.phw.ibatis.engine.PDao;

import com.ailk.jccard.mina.bean.JCHeadBean;
import com.ailk.jccard.mina.utils.JCHandlerUtils;
import com.ailk.jccard.mina.utils.SessionInfo;
import com.ailk.jccard.mina.utils.TypeUtils;
import com.ailk.phw.frombytes.ObjectFromBytes;
import com.ailk.phw.utils.ObjectUtils;

public abstract class IFProcess {

    private PDao dao;

    private String ifNo;

    public IFProcess(PDao dao, String ifNo) {
        this.dao = dao;
        this.ifNo = ifNo;
    }

    public abstract Map getResponseInfo(ObjectFromBytes objectFromBytes, byte[] bytes);

    public abstract Object getRequestInfo(Map responseInfo);

    public byte[] process(ObjectFromBytes objectFromByte, byte[] bytes, Map insertParam, SessionInfo sessionInfo)
            throws Exception {
        Map insertInfo = getResponseInfo(objectFromByte, bytes);

        insertParam.putAll((Map) insertInfo.get("insertParam"));
        List<Map> insertAppList = (List<Map>) insertInfo.get("insertAppList");
        recordResponseInfo(insertParam, insertAppList);

        if (ifNo.equals("IF1")) {
            return null;
        }

        Map responseInfo = queryRequestInfo(insertParam);
        JCHeadBean rspHead = ObjectUtils.populateBean(Collections.asMap(
                "sessionId", sessionInfo.getSessionId(), "typeFlag", TypeUtils.getEssCode(ifNo)), JCHeadBean.class);
        return JCHandlerUtils.generateRequest(rspHead, getRequestInfo(responseInfo), sessionInfo);
    }

    protected void recordResponseInfo(Map insertParam, List<Map> insertAppList) {
        boolean transactionStart = false;
        try {
            transactionStart = dao.tryStart();
            dao.startBatch();
            dao.insert("JCClientSQL.insertRspData", insertParam);
            if (!Collections.isEmpty(insertAppList)) {
                for (Map app : insertAppList) {
                    dao.insert("JCClientSQL.insertRspSubData", app);
                }
            }
            dao.executeBatch();
            dao.commit(transactionStart);
        } finally {
            dao.end(transactionStart);
        }
    }

    protected Map queryRequestInfo(Map insertParam) throws Exception {
        Map requestInfo = dao.selectMap("JCClientSQL.queryRequestInfo", insertParam);
        while (Collections.isEmpty(requestInfo)) {
            Thread.sleep(5000);
            requestInfo = dao.selectMap("JCClientSQL.queryRequestInfo", insertParam);
        }
        dao.update("JCClientSQL.updateRequestInfoState", requestInfo);
        return requestInfo;
    }

}
