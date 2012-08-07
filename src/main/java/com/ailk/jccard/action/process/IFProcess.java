package com.ailk.jccard.action.process;

import java.util.List;
import java.util.Map;

import org.phw.core.lang.Collections;
import org.phw.ibatis.engine.PDao;

import com.ailk.jccard.mina.bean.JCHeadBean;
import com.ailk.jccard.mina.utils.ResponseUtils;
import com.ailk.jccard.mina.utils.SessionInfo;
import com.ailk.phw.fromBytes.ObjectFromBytes;

public abstract class IFProcess {

    private PDao dao;

    private String ifNo;

    public IFProcess(PDao dao, String ifNo) {
        this.dao = dao;
        this.ifNo = ifNo;
    }

    public abstract Map getRequestInfo(ObjectFromBytes objectFromBytes, byte[] bytes);

    public abstract Object getResponseInfo(Map responseInfo);

    public byte[] process(ObjectFromBytes objectFromByte, byte[] bytes, Map insertParam,
            SessionInfo sessionInfo) {
        Map insertInfo = getRequestInfo(objectFromByte, bytes);

        insertParam.putAll((Map) insertInfo.get("insertParam"));
        List<Map> insertAppList = (List<Map>) insertInfo.get("insertAppList");
        insertRequestToDB(insertParam, insertAppList);
        Map responseInfo = ifNo.equals("IF3") ? Collections.asMap("RSP_RESULT", 0) : queryResponseFromDB(insertParam);
        JCHeadBean rspHead = ResponseUtils.generateEssHeadBean(ifNo, sessionInfo);

        return ResponseUtils.generateResponse(rspHead, getResponseInfo(responseInfo));
    }

    protected void insertRequestToDB(Map insertParam, List<Map> insertAppList) {
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

    private Map queryResponseFromDB(Map insertParam) {
        Map responseInfo = null;
        while (Collections.isEmpty(responseInfo)) {
            responseInfo = dao.selectMap("JCClientSQL.queryResponseInfo", insertParam);
        }
        return responseInfo;
    }

}
