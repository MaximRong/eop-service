package com.ailk.jccard.mina.utils;

import java.util.Map;

import org.phw.core.lang.Collections;
import org.phw.ibatis.engine.PDao;
import org.phw.ibatis.util.PDaoEngines;

import com.ailk.jccard.mina.bean.JCHeadBean;
import com.ailk.jccard.mina.bean.req.JCIF1ReqBodyBean;
import com.ailk.phw.fromBytes.ObjectFromBytes;

public class RequestUtils {

    private final static String SQL_XML = "com/ailk/sql/jccard/JCServerSQL.xml";
    private static PDao dao = PDaoEngines.getDao(SQL_XML, "EcsStore");

    /**
     * 解析客户端请求的数据.
     */
    public static Map parseClientRequestInfo(byte[] bytes) {
        ObjectFromBytes objectFromBytes = new ObjectFromBytes();
        JCHeadBean head = objectFromBytes.fromBytes(bytes, JCHeadBean.class);
        JCIF1ReqBodyBean body = objectFromBytes.fromBytes(bytes, JCIF1ReqBodyBean.class);
        return Collections.asMap("IF_NO", TypeUtils.getEssType(head.getTypeFlag()),
                "SESSION_ID", head.getSessionId(),
                "STAFF_ID", body.getOperatorId(),
                "JOB_TYPE", body.getJobType());
    }

    /**
     * 查询服务端响应的数据.
     */
    public static Map queryServerResponseInfo(SessionInfo sessionInfo) {
        Map param = Collections.asMap("IF_NO", sessionInfo.getIfNo(), "SESSION_ID", sessionInfo.getSessionId(),
                "STAFF_ID", sessionInfo.getStaffId(), "JOB_TYPE", sessionInfo.getJobType());
        param.put("ORDER_NO", sessionInfo.getOrderNo());
        Map result = dao.selectMap("JCServerSQL.queryRspLs", param);
        if (Collections.isEmpty(result)) {
            result = dao.selectMap("JCServerSQL.queryDefaultRspLs", param);
        }
        return result;
    }

    /**
     * 解析服务端请求的数据.
     * 
     * @throws Exception
     */
    public static Map parseServerRequestInfo(byte[] bytes, int jobType) throws Exception {
        ObjectFromBytes objectFromBytes = new ObjectFromBytes();
        JCHeadBean head = objectFromBytes.fromBytes(bytes, JCHeadBean.class);
        String ifNo = TypeUtils.getJcType(head.getTypeFlag());
        Class bodyClass = Class.forName(JCBeanUtils.getResponseClassName(ifNo, jobType));
        Object body = objectFromBytes.fromBytes(bytes, bodyClass);
        return Collections.asMap("head", head, "body", body, "ifNo", ifNo, "class", bodyClass);
    }

}
