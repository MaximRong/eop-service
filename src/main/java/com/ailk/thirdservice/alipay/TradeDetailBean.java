package com.ailk.thirdservice.alipay;

import java.io.Serializable;

/**
 * 帐务明细实体bean.
 *
 * @author wanglei
 *
 * 2012-6-6
 */
public class TradeDetailBean implements Serializable {
    private static final long serialVersionUID = -193131594071447251L;
    // 帐务流水号
    private String accountSeq;
    // 支付宝交易号
    private String tradeId;
    // 外部订单号
    private String outerId;
    // 交易渠道
    private String tradeChannel;
    // 交易类型
    private String tradeType;
    // 商品名称
    private String goodsName;
    // 用户编号
    private String userId;
    // 交易对方
    private String opposeCode;
    // 交易对方EMAIL
    private String opposeEmail;
    // 收入（元）
    private String income;
    // 支出（元）
    private String payout;
    // 账户余额（元）
    private String balance;
    // 交易时间
    private String tradeTime;
    // 备注说明
    private String remark;
    // 淘宝订单号
    private String taobaoId;
    // 校验结果编码
    private String checkCode;
    // 校验结果描述
    private String checkDesc;
    // 日志标识
    private String logId;

    public String getAccountSeq() {
        return accountSeq;
    }

    public void setAccountSeq(String accountSeq) {
        this.accountSeq = accountSeq;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getOuterId() {
        return outerId;
    }

    public void setOuterId(String outerId) {
        this.outerId = outerId;
    }

    public String getTradeChannel() {
        return tradeChannel;
    }

    public void setTradeChannel(String tradeChannel) {
        this.tradeChannel = tradeChannel;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOpposeCode() {
        return opposeCode;
    }

    public void setOpposeCode(String opposeCode) {
        this.opposeCode = opposeCode;
    }

    public String getOpposeEmail() {
        return opposeEmail;
    }

    public void setOpposeEmail(String opposeEmail) {
        this.opposeEmail = opposeEmail;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getPayout() {
        return payout;
    }

    public void setPayout(String payout) {
        this.payout = payout;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTaobaoId() {
        return taobaoId;
    }

    public void setTaobaoId(String taobaoId) {
        this.taobaoId = taobaoId;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getCheckDesc() {
        return checkDesc;
    }

    public void setCheckDesc(String checkDesc) {
        this.checkDesc = checkDesc;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

}
