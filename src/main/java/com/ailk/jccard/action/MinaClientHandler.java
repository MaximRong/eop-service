package com.ailk.jccard.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.phw.core.lang.Collections;
import org.phw.ibatis.engine.PDao;
import org.phw.ibatis.util.PDaoEngines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.jccard.action.process.IF1Process;
import com.ailk.jccard.action.process.IF2Process;
import com.ailk.jccard.action.process.IF3Process;
import com.ailk.jccard.action.process.IFProcess;
import com.ailk.jccard.mina.bean.JCHeadBean;
import com.ailk.jccard.mina.iface.JCClientHandler;
import com.ailk.jccard.mina.utils.TypeUtils;
import com.ailk.mall.base.utils.StringUtils;
import com.ailk.phw.fromBytes.ObjectFromBytes;
import com.alibaba.fastjson.JSON;

public class MinaClientHandler extends JCClientHandler {

    private static Logger logger = LoggerFactory.getLogger(MinaClientHandler.class);

    private final static String SQL_XML = "";
    private static PDao dao = PDaoEngines.getDao(SQL_XML, "EcsStore");

    @Override
    public byte[] messageRequest(IoSession session, Object message) throws Exception {
        byte[] bytes = ((IoBuffer) message).array();
        ObjectFromBytes objectFromBytes = new ObjectFromBytes();
        JCHeadBean head = objectFromBytes.fromBytes(bytes, JCHeadBean.class);
        logger.info("Request Head Bean - " + JSON.toJSONString(head));
        String ifNo = TypeUtils.getJcType(head.getTypeFlag());
        int jobType = getSessionInfo(session).getJobType();

        String id = StringUtils.toString(dao.selectMap("JCClientSQL.getSeq", new HashMap()).get("SEQ"));
        Map insertParam = Collections.asMap("ID", id, "SESSIONID", head.getSessionId(), "IF_NO", ifNo);
        IFProcess process = null;
        if (ifNo.equals("IF1")) {
            process = new IF1Process(dao, ifNo, jobType, id);
        } else if (ifNo.equals("IF2")) {
            process = new IF2Process(dao, ifNo);
        } else if (ifNo.equals("IF3")) {
            process = new IF3Process(dao, ifNo);
        } else {
            return null;
        }
        return process.process(objectFromBytes, bytes, insertParam, getSessionInfo(session));
    }

}
