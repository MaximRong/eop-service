package org.phw.eop.domain;

import java.io.Serializable;

public class EopLogBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private RspFormat rspFmt;
    private String trxid, appcode, appid, actionname, actionid, apptx,
            clientip, serverip, reqcontent, rspcode, rspdesc, rspmsg, mock, fmt, eopCipher, eopSign, reqtsStr;

    private long reqts, arrivalts, pats, aats, rspts;

    @Override
    public String toString() {
        return "EopLogBean [trxid=" + trxid + ", appcode=" + appcode + ", appid=" + appid
                + ", actionname=" + actionname + ", actionid=" + actionid + ", apptx=" + apptx
                + ", clientip=" + clientip + ", serverip=" + serverip + ", reqcontent=" + reqcontent
                + ", rspcode=" + rspcode + ", rspdesc=" + rspdesc + ", rspmsg=" + rspmsg + ", reqts="
                + reqts + ", arrivalts=" + arrivalts + ", pats=" + pats + ", aats=" + aats + ", rspts=" + rspts + "]";
    }

    public String getTrxid() {
        return trxid;
    }

    public String getAppid() {
        return appid;
    }

    public String getActionid() {
        return actionid;
    }

    public String getApptx() {
        return apptx;
    }

    public String getClientip() {
        return clientip;
    }

    public String getServerip() {
        return serverip;
    }

    public String getReqcontent() {
        return reqcontent;
    }

    public String getRspcode() {
        return rspcode;
    }

    public String getRspdesc() {
        return rspdesc;
    }

    public String getRspmsg() {
        return rspmsg;
    }

    public long getReqts() {
        return reqts;
    }

    public long getArrivalts() {
        return arrivalts;
    }

    public long getRspts() {
        return rspts;
    }

    public void setTrxid(String trxid) {
        this.trxid = trxid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public void setActionid(String actionid) {
        this.actionid = actionid;
    }

    public void setApptx(String apptx) {
        this.apptx = apptx;
    }

    public void setClientip(String clientip) {
        this.clientip = clientip;
    }

    public void setServerip(String serverip) {
        this.serverip = serverip;
    }

    public void setReqcontent(String reqcontent) {
        this.reqcontent = reqcontent;
    }

    public void setRspcode(String rspcode) {
        this.rspcode = rspcode;
    }

    public void setRspdesc(String rspdesc) {
        this.rspdesc = rspdesc;
    }

    public void setRspmsg(String rspmsg) {
        this.rspmsg = rspmsg;
    }

    public void setReqts(long reqts) {
        this.reqts = reqts;
    }

    public void setArrivalts(long arrivalts) {
        this.arrivalts = arrivalts;
    }

    public void setRspts(long rspts) {
        this.rspts = rspts;
    }

    public void setAppcode(String appcode) {
        this.appcode = appcode;
    }

    public String getAppcode() {
        return appcode;
    }

    public void setActionname(String actionname) {
        this.actionname = actionname;
    }

    public String getActionname() {
        return actionname;
    }

    public void setMock(String mock) {
        this.mock = mock;
    }

    public String getMock() {
        return mock;
    }

    public void setFmt(String fmt) {
        this.fmt = fmt;
        setRspFmt(RspFormat.from(fmt));
    }

    public String getFmt() {
        return fmt;
    }

    public void setEopCipher(String eopCipher) {
        this.eopCipher = eopCipher;
    }

    public String getEopCipher() {
        return eopCipher;
    }

    public void setEopSign(String eopSign) {
        this.eopSign = eopSign;
    }

    public String getEopSign() {
        return eopSign;
    }

    public void setRspFmt(RspFormat rspFmt) {
        this.rspFmt = rspFmt;
    }

    public RspFormat getRspFmt() {
        return rspFmt;
    }

    public void setReqtsStr(String reqtsStr) {
        this.reqtsStr = reqtsStr;
    }

    public String getReqtsStr() {
        return reqtsStr;
    }

    public long getPats() {
        return pats;
    }

    public void setPats(long pats) {
        this.pats = pats;
    }

    public long getAats() {
        return aats;
    }

    public void setAats(long aats) {
        this.aats = aats;
    }

}
