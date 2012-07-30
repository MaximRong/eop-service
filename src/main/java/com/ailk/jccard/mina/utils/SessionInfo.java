package com.ailk.jccard.mina.utils;

public class SessionInfo {

    private String ifNo;

    private String sessionId;

    private String staffId;

    private int jobType;

    private int orderNo;

    public SessionInfo() {
        orderNo = 0;
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
