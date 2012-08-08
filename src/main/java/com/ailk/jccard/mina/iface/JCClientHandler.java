package com.ailk.jccard.mina.iface;

import org.apache.commons.lang.ArrayUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.ailk.jccard.mina.utils.JCHandlerUtils;
import com.ailk.jccard.mina.utils.SessionInfo;

public abstract class JCClientHandler extends JCHandler {

    public abstract byte[] messageRequest(SessionInfo sessionInfo, byte[] message) throws Exception;

    private byte[] requestBuffer = new byte[0];

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        SessionInfo sessionInfo = getSessionInfo(session);

        sessionInfo.pushMessage(message);
        byte[] msg = sessionInfo.popMessage();

        while (msg != null) {
            incrementSessionOrderNo(session);
            if (msg.length == 0) {
                session.close(true);
                return;
            }

            byte[] bytes = messageRequest(sessionInfo, msg);
            if (ArrayUtils.isEmpty(bytes)) {
                session.close(true);
                return;
            }
            session.write(IoBuffer.wrap(bytes));
            msg = sessionInfo.popMessage();
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        if (getSessionOrderNo(session) == 0) {
            requestBuffer = JCHandlerUtils.addBufferToBytes(requestBuffer, (IoBuffer) message);
            byte[] bytes = JCHandlerUtils.fetchBytesFromBuffer(requestBuffer);
            if (ArrayUtils.isNotEmpty(bytes)) {
                SessionInfo sessionInfo = getSessionInfo(session);
                JCHandlerUtils.parseClientRequest(bytes, sessionInfo);
            }
        }
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
    }

}
