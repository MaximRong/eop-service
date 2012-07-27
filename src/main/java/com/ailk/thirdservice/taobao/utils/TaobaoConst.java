package com.ailk.thirdservice.taobao.utils;

/**
 * 淘宝接入常量类。
 *
 * @author wanglei
 *
 * 2012-5-22
 */
public interface TaobaoConst {

    /** 日志类型 */
    String GETORDER = "01"; // 订单日志
    String RETORDER = "02"; // 退款日志
    String SEND = "03"; // 配送日志
    String MODSEND = "04"; // 配送地址变更
    String MONITORORDER = "05"; // 日志同步监控

    /** 接口结果 */
    String IFAIL = "00"; // 接口失败
    String ISUCC = "01"; // 接口成功

    /** 存储过程返回码 */
    String PROCFAIL = "00"; // 存储过程调用异常
    String PROCSUCC = "01"; // 存储过程调用正常
    String PROCSPECFAIL = "02"; // 存储过程调用异常
    String PROCSPECSUCC = "03"; // 存储过程调用正常
    String ORDERINVALID = "04"; // 订单无效，已有该订单
    String ORDERINVALIDSTATUE = "05"; // 订单无效，已有该订单
    String ORDERCHGFAIL = "06"; // 省份地市转换编码错误
    String RETNOORDER = "07"; // 退款信息表有数据，订单没有数据

    String SENDSUCC = "08"; // 发送物流信息成功，更新成功
    String SENDPROCFAIL = "09"; // 发送物流信息成功，更新失败
    String SENDRETFAIL = "10"; // 发送物流信息成功，淘宝更新物流信息失败
    String SENDFAIL = "11"; // 发送物流信息失败

    String SQLFAIL = "12"; // 未调用存储过程，java程序异常
    String ORDERNOTWT = "13"; // 非网厅订单

    String SPEC = "99"; // 特殊，非网厅订单
    String SPECFAIL = "98"; // 特殊，非网厅订单，异常了

    String CID_SWK = "50016085"; // 上网卡类目编码

    String DATE_FORMAT = "yyyyMMddHHmmss"; // 日期格式

    long SPEC_RANDOM = 999999l; // 特殊标识

}
