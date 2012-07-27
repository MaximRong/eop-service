package com.ailk.thirdservice.base;

/**
 * 数字常量。
 * 
 * @author wanglei 2012-2-16
 */
public interface NumConstants {
    // 号码类型
    String NUM_TYPE_PRE        = "01";
    String NUM_TYPE_POST       = "02";

    // 费用科目编码
    String FEE_TYPE_USIM       = "1001";
    String FEE_TYPE_TERM       = "1002";
    String FEE_TYPE_NUMPRE     = "2001";
    String FEE_TYPE_CONTRACT   = "2002";
    String FEE_TYPE_POST       = "3001";
    String FEE_TYPE_MOREPRE    = "4001";

    /**************** 响应编码 ****************************/
    // 处理成功
    String RESP_CODE_SUCCESS   = "0000";
    // 重复提交
    String RESP_RE_SUBMIT      = "1001";
    // 商品合法性校验失败
    String RESP_GOODS_FAIL     = "1002";
    // 客户资料校验失败
    String RESP_CUST_FAIL      = "1003";
    // 资源校验失败
    String RESP_RESOUTCE_FAIL  = "1004";
    // 订单生成失败
    String RESP_ORDER_FAIL     = "1005";
    // 其他错误
    String RESP_CODE_FAIL      = "9999";

    // 付费类型
    String PAYFEE_TYPE_POST    = "02";
    String PAYFEE_TYPE_PRE     = "01";

    // 预占标志
    String OCCUPY_FLAG_NO      = "0";
    String OCCUPY_FLAG_YES     = "1";
    String OCCUPY_FLAG_UNPAY   = "2";
    String OCCUPY_FLAG_PAIED   = "3";
    String OCCUPY_FLAG_RELEASE = "4";
    String OCCUPY_FLAG_CHG     = "5";

    // 支付状态
    String PAY_STATE_UNPAY     = "0";
    String PAY_STATE_PAIED     = "1";

    // 订单状态
    String ORDER_STATE_A0      = "A0";
    String ORDER_STATE_B0      = "B0";

    // 特殊订单类型
    String SPEC_ORDER_TYPE_NUM = "01";

    // 系统编码
    String SYSCODE_MALL        = "EMAL";

}
