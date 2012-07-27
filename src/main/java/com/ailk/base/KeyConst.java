package com.ailk.base;

public interface KeyConst {
    /*****************业务Key**************/
    // 省份编码
    String PROVINCE = "Province";
    // 请求报文头根节点
    String HEAD_ROOT = "HeadRoot";
    // 请求报文头Map
    String REQ_HEAD = "ReqHead";
    // 请求报文体根节点
    String BODY_ROOT = "BodyRoot";
    // 请求报文体Map
    String REQ_BODY = "ReqBody";
    // 请求报文XML
    String REQ_XML = "REQ_XML";
    // 响应报文头Map
    String RSP_HEAD = "RspHead";
    // 响应报文体Map
    String RSP_BODY = "RspBody";
    // 响应报文XML
    String RSP_XML = "RspXml";
    // Ess类型：3G、N6
    String ESS_TYPE = "ess.type.";
    // 总部Ess
    String HQ_ESS = "Ess";
    // 北六号码数据源名称
    String DS_NAME = "DsName";

    /*****************报文头Key**************/
    String ORIG_DOMAIN = "OrigDomain";
    String HOME_DOMAIN = "HomeDomain";
    String BIP_VER = "BIPVer";
    String ACTIVITY_CODE = "ActivityCode";
    String ACTION_CODE = "ActionCode";
    String ROUTING = "Routing";
    String ROUTE_TYPE = "RouteType";
    String ROUTE_VALUE = "RouteValue";
    String TRANS_IDO = "TransIDO";
    String PROCESS_TIME = "ProcessTime";
    String RESPONSE = "Response";
    String RSP_TYPE = "RspType";
    String RSP_CODE = "RspCode";
    String RSP_DESC = "RspDesc";
    String TEST_FLAG = "TestFlag";
    String MSG_SENDER = "MsgSender";
    String MSG_RECEIVER = "MsgReceiver";
    String SVC_CONT_VER = "SvcContVer";
    String SVC_CONT = "SvcCont";

    /*****************报文体Key**************/
    String RESP_CODE = "RespCode";
    String RESP_DESC = "RespDesc";
    String ACCESS_TYPE = "AccessType";
    String CHANNEL_TYPE = "ChannelType";
    String CHANNEL_ID = "ChannelID";
    String DISTRICT = "District";
    String CITY = "City";

    /*****************订单提交接口**************/
    // 接口下发商城订单号
    String MALL_ORDER_NO = "MallOrderNo";
    // 商城订单号
    String ORDER_ID = "OrderId";

    /*****************号码资源状态变更接口**************/
    String RESOURCES_INFO = "ResourcesInfo";
    String RESOURCE_RSP = "ResourcesRsp";
    String RESOURCE_TYPE = "ResourcesType";
    String RESOURCE_CODE = "ResourcesCode";
    String RSC_STATE_CODE = "RscStateCode";
    String RSC_STATE_DESC = "RscStateDesc";
    String NUM_ID = "NumID";
    String SYSCODE = "SysCode";
    String PROKEY = "ProKey";
    String OCCUPIED_FLAG = "OccupiedFlag";
    String DELAY_OCCUPIED_FLAG = "DelayOccupiedFlag";
    String OCCUPIED_TIME = "OccupiedTime";
    String CUST_NAME = "CustName";
    String CERT_TYPE = "CertType";
    String CERT_NUM = "CertNum";
    String PREORDER_TAG = "PreOrderTag";
    String REMARK = "Remark";
    String CONTACT_NUM = "ContactNum";
    String PRODUCT_ID = "ProductID";

    /*****************写卡数据查询接口**************/
    String ICCID = "ICCID";
    String PROC_ID = "ProcId";
    String ACTIVE_ID = "ActiveId";
    String SCRIPT_SEQ = "ScriptSeq";

}
