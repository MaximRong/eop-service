package com.ailk.jccard.mina.utils;

import org.apache.mina.core.buffer.IoBuffer;

import com.ailk.phw.utils.ConstantUtils;
import com.ailk.phw.utils.JCConvertUtils;

public class SessionInfo {

    private String ifNo;

    private String sessionId;

    private String staffId;

    private int jobType;

    private int orderNo;

    private byte[] messageBuffer;

    public SessionInfo() {
        orderNo = 0;
        messageBuffer = new byte[0];
    }

    public void pushMessage(Object message) {
        messageBuffer = JCHandlerUtils.addBufferToBytes(messageBuffer, (IoBuffer) message);
    }

    public byte[] popMessage() {
        byte[] result = JCHandlerUtils.fetchBytesFromBuffer(messageBuffer);
        if (result == null) {
            return null;
        }
        messageBuffer = JCConvertUtils.subBytes(messageBuffer,
                ConstantUtils.PrimitiveOffset.SHORT_OFFSET + result.length);
        return result;
    }

    public void incrementOrderNo() {
        orderNo++;
    }

    public void setIfNo(String ifNo) {
        this.ifNo = ifNo;
    }

    public String getIfNo() {
        return ifNo;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setJobType(int jobType) {
        this.jobType = jobType;
    }

    public int getJobType() {
        return jobType;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public int getOrderNo() {
        return orderNo;
    }

}
