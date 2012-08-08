package com.ailk.jccard.mina.client;

import java.util.HashMap;
import java.util.Map;

import org.phw.core.lang.Collections;
import org.phw.ibatis.engine.PDao;
import org.phw.ibatis.util.PDaoEngines;

import com.ailk.jccard.mina.iface.JCClientHandler;
import com.ailk.jccard.mina.process.IF1Process;
import com.ailk.jccard.mina.process.IF2Process;
import com.ailk.jccard.mina.process.IF3Process;
import com.ailk.jccard.mina.process.IFProcess;
import com.ailk.jccard.mina.utils.JCHandlerUtils;
import com.ailk.jccard.mina.utils.SessionInfo;
import com.ailk.mall.base.utils.StringUtils;
import com.ailk.phw.frombytes.ObjectFromBytes;

public class MinaClientHandler extends JCClientHandler {

    private final static String SQL_XML = "com/ailk/sql/jccard/JCClientSQL.xml";
    private static PDao dao = PDaoEngines.getDao(SQL_XML, "EcsStore");

    @Override
    public byte[] messageRequest(SessionInfo sessionInfo, byte[] message) throws Exception {
        ObjectFromBytes objectFromBytes = new ObjectFromBytes();
        String ifNo = JCHandlerUtils.parseServerResponse(objectFromBytes, message, sessionInfo);

        String seqId = StringUtils.toString(dao.selectMap("JCClientSQL.getSeq", new HashMap()).get("SEQ"));
        Map rspParam = Collections.asMap("ID", seqId, "SESSIONID", sessionInfo.getSessionId(), "IF_NO", ifNo);

        IFProcess process = null;
        if (ifNo.equals("IF1")) {
            process = new IF1Process(dao, ifNo, sessionInfo.getJobType(), seqId);
        } else if (ifNo.equals("IF2")) {
            process = new IF2Process(dao, ifNo);
        } else if (ifNo.equals("IF3")) {
            process = new IF3Process(dao, ifNo);
        } else {
            return null;
        }
        return process.process(objectFromBytes, message, rspParam, sessionInfo);
    }

}
