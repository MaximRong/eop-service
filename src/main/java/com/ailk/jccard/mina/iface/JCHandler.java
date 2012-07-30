package com.ailk.jccard.mina.iface;

import java.util.HashMap;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.ailk.jccard.mina.utils.SessionInfo;
import com.ailk.mall.base.utils.StringUtils;

public abstract class JCHandler implements IoHandler {

    private HashMap<Long, SessionInfo> sessionInfoMap = new HashMap<Long, SessionInfo>();

    public void newSessionInfo(IoSession session) {
        sessionInfoMap.put(session.getId(), new SessionInfo());
    }

    public SessionInfo getSessionInfo(IoSession session) {
        return sessionInfoMap.get(session.getId());
    }

    public void removeSessionInfo(IoSession session) {
        sessionInfoMap.remove(session.getId());
    }

    public void incrementSessionOrderNo(IoSession session) {
        sessionInfoMap.get(session.getId()).incrementOrderNo();
    }

    public int getSessionOrderNo(IoSession session) {
        return sessionInfoMap.get(session.getId()).getOrderNo();
    }

    public void threadSleep(Object obj) throws Exception {
        Thread.sleep(Integer.valueOf(StringUtils.toString(obj)));
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        newSessionInfo(session);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        removeSessionInfo(session);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
        session.close(true);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        session.close(true);
    }

}
