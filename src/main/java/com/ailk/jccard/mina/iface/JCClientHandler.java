package com.ailk.jccard.mina.iface;

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.ailk.jccard.mina.utils.RequestUtils;
import com.ailk.jccard.mina.utils.SessionInfo;
import com.ailk.mall.base.utils.StringUtils;

public abstract class JCClientHandler extends JCHandler {

    public abstract byte[] messageRequest(IoSession session, Object message) throws Exception;

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        incrementSessionOrderNo(session);

        byte[] bytes = messageRequest(session, message);
        if (ArrayUtils.isEmpty(bytes)) {
            session.close(true);
            return;
        }

        session.write(IoBuffer.wrap(bytes));
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        if (getSessionOrderNo(session) == 0) {
            Map reqInfo = RequestUtils.parseClientRequestInfo(((IoBuffer) message).array());
            SessionInfo sessionInfo = getSessionInfo(session);
            sessionInfo.setIfNo(StringUtils.toString(reqInfo.get("IF_NO")));
            sessionInfo.setSessionId(StringUtils.toString(reqInfo.get("SESSION_ID")));
            sessionInfo.setStaffId(StringUtils.toString(reqInfo.get("STAFF_ID")));
            sessionInfo.setJobType(Integer.valueOf(StringUtils.toString(reqInfo.get("JOB_TYPE"))));
        }
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
    }

}
