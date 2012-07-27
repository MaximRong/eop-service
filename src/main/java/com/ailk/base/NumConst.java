package com.ailk.base;

import static org.phw.config.impl.PhwConfigMgrFactory.getConfigMgr;

public interface NumConst {
    /*******************3G相关*******************/
    // 报文头响应类型：成功
    String RSP_TYPE_SUCCESS = "0";
    // 报文头响应类型：成功
    String RSP_TYPE_FAIL = "1";
    // 报文响应编码：成功
    String RESP_CODE_SUCCESS = "0000";
    // 报文响应编码：其他错误
    String RESP_CODE_FAIL = "9999";
    // EOP平台响应编码
    String EOP_RSP_OK = "0";
    // 资源类型
    String RESOURCE_TYPE_SERIAL = "03";
    // 预占标志
    String OCCUPY_FLAG_NO = "0";
    String OCCUPY_FLAG_YES = "1";
    String OCCUPY_FLAG_UNPAY = "2";
    String OCCUPY_FLAG_PAIED = "3";
    String OCCUPY_FLAG_RELEASE = "4";
    String OCCUPY_FLAG_CHG = "5";

    // 延长预占标志
    String DELAY_OCCUPY_FALG_NO = "0";
    String DELAY_OCCUPY_FALG_YES = "1";

    /*******************北六相关*******************/
    // 北六号码服务
    String N6ESS_NUMSER_ENDPOINT = getConfigMgr().getString("N6Ess.NumSer.EndPoint");
    // 北六测试模式
    String N6ESS_TESTFLAG = getConfigMgr().getString("N6Ess.TestFlag");
    // 报文常量
    String N6_ORIG_DOMAIN = "USEE";
    String N6_ACTION_CODE = "0";
    String N6_ACTION_RELATION = "0";
    String N6_MSG_RECEIVER = "0400";
    String N6_MSG_SENDER = "0000";
    String N6_HSNDUNS = "9800";
    String N6_OSNDUNS = "0002";
    String N6_ROUTE_TYPE = "00";
    String N6_OPER_ID = "linkage";
    String N6_ORDER_TYPE = "01";
    // 服务
    String N6_SERVICE_NUM = "ESSNumSer";
    // 操作
    String N6_OPERATE_PRENUM = "PreOccupyRes";
    String N6_OPERATE_ORDERNUM = "numOrder";
    String N6_OPERATE_DELORDERNUM = "delNumOrder";

    /*******************BSS相关*******************/
    // BSS号码预约是否开放
    boolean BSS_NUMORDER_OPEN = getConfigMgr().getBoolean("Bss.NumOrder.Open", true);
    // BSS测试模式
    String BSS_TESTFLAG = getConfigMgr().getString("Bss.TestFlag");
    String BSS_NUM_BIPCODE = "BIP2D001";
    String BSS_NUM_PRE_ACTCODE = "T2000301";
    String BSS_NUM_ORDER_ACTCODE = "T2030302";
    String BSS_NUM_CHANNEL = "1020100";
    String BSS_NUM_SEQ_PREFIX = "01";

    /*******************公共参数*******************/
    // 3GESS
    String ESS_TYPE_3G = "3G";
    // N6ESS
    String ESS_TYPE_N6 = "N6";
    // 商城SysCode
    String SYSCODE_MALL = "EMAL";
    // 自助终端SysCode
    String SYSCODE_ZZZD = "ZZZD";
}
