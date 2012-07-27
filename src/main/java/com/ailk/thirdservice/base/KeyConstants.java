package com.ailk.thirdservice.base;

/**
 * Key值常量。
 * 
 * @author max
 */
public interface KeyConstants {

    /*************** 订单相关 ***************/
    // 订单ID
    String ORDER_ID            = "OrderId";
    // 订单NO
    String ORDER_NO            = "OrderNo";
    // 总价格（除邮费）
    String ORIGINAL_PRICE      = "OriginalPrice";
    // 总减免金额
    String COUPON_MONEY        = "CouponMoney";
    // 总价格（含邮费）
    String TOPAY_MONEY         = "TopayMoney";
    // 实收总金额
    String INCOME_MONEY        = "IncomeMoney";
    // 费用科目编码
    String FEE_CODE            = "FeeCode";
    // 费用
    String FEE                 = "Fee";
    // 订单状态
    String ORDER_STATE         = "OrderState";
    // 支付方式
    String PAY_TYPE            = "PayType";
    // 支付状态
    String PAY_STATE           = "PayState";
    // 付费方式
    String PAY_WAY             = "PayWay";
    // 处理内容编码
    String DEAL_CONTENT        = "DealContent";
    // 处理结果编码
    String RESULT_CODE         = "ResultCode";
    // 结果说明
    String RESULT_INFO         = "ResultInfo";
    // 第三方订单状态
    String SUBSCRIBE_STATE     = "SubscribeState";

    /*************** 邮寄相关 ***************/
    // 收件人姓名
    String RECV_CUST_NAME      = "RecvCustName";
    // 收件人手机号码
    String RECV_LINK_PHONE     = "RecvLinkPhone";
    // 收件人固话号码
    String RECV_FIX_PHONE      = "RecvFixPhone";
    // 收件人邮箱
    String RECV_EMAIL          = "RecvEmail";
    // 邮寄标志
    String POST_TAG            = "PostTag";
    // 邮费
    String POST_FEE            = "PostFee";
    // 配送方式
    String DELV_TYPE           = "DelvType";
    // 配送日期
    String DELV_DATE_TYPE      = "DelvDateType";
    // 具体发货时间
    String DELV_DATE_TIME      = "DelvDateTime";
    // 配送确认
    String NEED_AFFIRM         = "NeedAffirm";
    // 发票标题
    String INVO_TITLE          = "InvoTitle";
    // 发票内容编码
    String INVO_CONT           = "InvoCont";
    // 用户标记
    String USER_TAG            = "UserTag";
    // 邮寄省份
    String POST_PROVINCE       = "PostProvince";
    // 邮寄地市
    String POST_CITY           = "PostCity";
    // 邮寄区县
    String POST_DISTRICT       = "PostDistrict";
    // 邮编
    String POST_CODE           = "PostCode";
    // 邮寄地址
    String POST_ADDR           = "PostAddr";

    /*************** 商品相关 ***************/
    // 商品ID
    String GOODS_ID            = "GoodsId";
    // 商品状态
    String GOODS_STATE         = "GoodsState";
    // 商品实例ID
    String GOODS_INST_ID       = "GoodsInstId";
    // 商品名称
    String GOODS_NAME          = "GoodsName";
    // 商品实收金额
    String AMOUNT_RECEIVED     = "AmountReceived";
    // 商品应收金额
    String AMOUNT_RECEVABLE    = "AmountRecevable";
    // 商品所属二级目录编码
    String GOODS_CTLG_CODE     = "GoodsCtlgCode";
    // 商品所属模板编码
    String TMPL_ID             = "TmplId";
    // 商品属性编码
    String ATTR_CODE           = "AttrCode";
    // 商品属性名称
    String ATTR_NAME           = "AttrName";
    // 商品属性值编码
    String ATTR_VAL_CODE       = "AttrValCode";
    // 商品属性值名称
    String ATTR_VAL_NAME       = "AttrValName";
    // 商品属性值描述
    String ATTR_VAL_DESC       = "AttrValDesc";

    /*************** 商户相关 ***************/
    // 商户ID
    String MERCHANT_ID         = "MerchantId";
    // 商户Code
    String MERCHANT_CODE       = "MerchantCode";
    // 省份
    String PROVINCE            = "Province";
    // 地市
    String CITY                = "City";
    // 区县
    String DISTRICT            = "District";
    // 通用员工ID
    String OPERATOR_ID         = "OperatorID";
    // 渠道ID
    String CHANNEL_ID          = "ChannelID";
    // 渠道类型
    String CHANNEL_TYPE        = "ChannelType";
    // 接入方式
    String ACCESS_TYPE         = "AccessType";

    /*************** 号码相关 ***************/
    // 号码预占关键字类型
    String PRO_KEYMODE         = "ProKeyMode";
    // 入网号码
    String NET_PHONE_NO        = "NetPhoneNo";
    // 入网客户名称
    String NET_CUST_NAME       = "NetCustName";
    // 证件类型
    String NET_PSPT_TYPE       = "NetPsptType";
    // 入网客户证件号码
    String NET_PSPT_NO         = "NetPsptNo";
    // 入网证件地址
    String NET_PSPT_ADDR       = "NetPsptAdress";
    // 号码修改标识
    String KEY_CHANGE_TAG      = "KeyChangeTag";
    // 号码原关键字
    String OLD_KEY             = "OldKey";
    // 号码预占关键字
    String PRO_KEY             = "ProKey";
    // 号码查询关键字
    String KEY_VALUE           = "KeyValue";
    // 返回号码个数
    String BACK_NUMBER         = "BackNumber";
    // 资源类型
    String RESOURCE_TYPE       = "ResourcesType";
    // 资源编码
    String RESOURCE_CODE       = "ResourcesCode";
    // 资源信息
    String RESOURCE_INFO       = "ResourcesInfo";
    // 服务号码类型：0老号码1新号码
    String NET_PHONENO_TYPE    = "NetPhoneNoType";
    // 预占类型
    String OCCUPY_TYPE         = "OccupiedType";
    // 预占标识
    String OCCUPY_FLAG         = "OccupiedFlag";
    // 号码预占时间
    String OCCUPY_TIME         = "OccupiedTime";
    // 号码归属省份
    String NUM_PROVINCE        = "NumProvince";
    // 号码归属地市
    String NUM_CITY            = "NumCity";
    // 号码归属区县
    String NUM_DISTRICT        = "NumDistrict";
    // 号码归属渠道编码
    String NUM_CHANNELID       = "NumChannelId";
    // 号码归属渠道类型
    String NUM_CHANNELTYPE     = "NumChannelType";
    // 备注
    String REMARK              = "Remark";

    /*************** 产品相关 ***************/
    // 产品等级金额
    String PRODUCT_VALUE       = "PRODUCT_VALUE";

    /*************** 商品属性 ***************/
    // 活动ID
    String ACTIVITY_ID         = "ACTIVITY_ID";
    // 产品ID
    String PRODUCT_ID          = "PRODUCT_ID";
    // 产品类型
    String PRODUCT_TYPE        = "PRODUCT_TYPE";
    // 活动类型
    String ACTIVITY_TYPE       = "ACTIVITY_TYPE";
    // 品牌编码
    String BRAND_CODE          = "BRAND_CODE";
    // 型号编码
    String MODEL_CODE          = "MODEL_CODE";
    // 颜色编码
    String COLOR_CODE          = "COLOR_CODE";
    // 号码来源
    String NETPHONE_FROM       = "NetPhoneFrom";
    // 接口下发产品编码
    String PRODUCT_CODE        = "ProductId";
    // 首月付费模式
    String FRSTMON_BILLMODE    = "FrstMonBillMode";
    // 资费分类(A/B/C)
    String BILL_TYPE           = "BillType";
    // 产品资费
    String PROD_BILL           = "ProdBill";
    // USIM卡费
    String USIM_FEE            = "1001";
    // 终端费用
    String TML_FEE             = "1002";
    // 靓号预存费用
    String NICE_NUM_FEE        = "2001";
    // 合约费用
    String CONTRACT_FEE        = "2002";
    // 普号预存费用
    String ADVANCE_FEE         = "2009";
    // 多交预存费用
    String EXTRA_ADVANCE_FEE   = "4001";

    /*************** 费用相关 ***************/
    // 应付总金额
    String FEE_SUM             = "FeeSum";
    // 已付总金额
    String ALREADY_PAY         = "AlreadyPay";
    // 邮费
    String POSTAGE             = "Postage";

    /*************** 其他 ***************/
    // 第三方系统编码
    String SYSCODE             = "SysCode";
    // 第三方订单号
    String THIRDPARTY_ORDER_ID = "SubscribeNo";
    // 客户信息
    String CUST_INFO           = "CustInfo";
    // 号码信息
    String NUM_INFO            = "NumInfo";
    // 费用信息
    String FEE_INFO            = "FeeInfo";
    // 证件类型
    String CUST_NAME           = "CustName";
    // 证件类型
    String CERT_TYPE           = "CertType";
    // 证件号码
    String CERT_NUM            = "CertNum";
    // 返回编码
    String RESP_CODE           = "RespCode";
    // 返回描述
    String RESP_DESC           = "RespDesc";

    /*************** 订单状态变更接口 ***************/
    // 前订单状态
    String OLD_STATE           = "OldState";
    // 新订单状态
    String NEW_STATE           = "NewState";
    // 前支付状态
    String OLD_PAY_STATE       = "OldPayState";
    // 新支付状态
    String NEW_PAY_STATE       = "NewPayState";

    /*************** 号码状态变更接口 ***************/
    String ORDER_TYPE          = "OrderType";
    // 受理渠道标识
    String ACCEPT_CHANNEL_TAG  = "AcceptChannelTag";
    // 发展人/推荐人判断标识
    String DEVELOP_PERSON_TAG  = "DevelopPersonTag";
}
