package com.ailk.thirdservice.submitorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.phw.core.exception.BusinessException;
import org.phw.core.lang.Collections;
import org.phw.eop.api.ApiException;
import org.phw.eop.api.bean.checkcust.CheckCustReq;
import org.phw.eop.api.bean.checkcust.rsp.CheckCustRspBody;
import org.phw.eop.api.bean.checknum.CheckNumReq;
import org.phw.eop.api.bean.checknum.rsp.CheckNumRspBody;
import org.phw.eop.api.bean.checknum.rsp.ResourcesNumRspBean;
import org.phw.eop.api.bean.numqry.rsp.NumInfoBean;
import org.phw.eop.api.bean.numqry.rsp.NumQryRspBody;
import org.phw.ibatis.engine.PDao;
import org.phw.ibatis.util.PDaoEngines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.mall.base.utils.StringUtils;
import com.ailk.mall.common.eop.CheckNumResIface;
import com.ailk.mall.common.eop.NewCheckCustIface;
import com.ailk.mall.common.eop.NewCheckNumReqIface;
import com.ailk.mall.number.QueryLocalNumService;
import com.ailk.thirdservice.base.KeyConstants;
import com.ailk.thirdservice.base.NumConstants;
import com.ailk.thirdservice.base.OrderInfoConstants;
import com.ailk.thirdservice.base.ThirdPartyDbUnit;
import com.ailk.thirdservice.base.ThirdPartyUtils;
import com.ailk.thirdservice.base.goodsrv.AbstractGoodsSrv;
import com.ailk.thirdservice.base.goodsrv.GoodsSrvFactory;

/**
 * 第三方接入之订单提交处理。
 * 
 * @author wanglei 2012-2-15
 */
public class SubmitOrderService {
    private static final String CUSTCHECK_RET = "客户资料校验失败：";
    private static final String NUMCHECK_RET  = "手机号码校验失败：";
    private static final Logger logger        = LoggerFactory.getLogger(SubmitOrderService.class);
    private static final String SQL_PATH      = "com/ailk/face/thirdservice/submitorder/SubmitOrderSQL.xml";
    public PDao                 dao           = PDaoEngines.getDao(SQL_PATH, "EcsStore");

    /**
     * 订单提交处理。
     * 
     * @param inMap
     * @return
     */
    public Map execute(Map inMap) {
        try {
            // 校验商品是否存在
            checkGoodsExists(inMap);
            String tmplId = (String)inMap.get(KeyConstants.TMPL_ID);
            // 获取goodsSrv
            AbstractGoodsSrv goodsSrv = getGoodsSrv(tmplId);
            // 校验商品详情
            Map checkRet = goodsSrv.checkGoodsDtlInfo(inMap);
            // 落订单
            inMap.putAll(checkRet == null ? new HashMap() : checkRet);
            goodsSrv.recordOrderInfo(inMap);
            // 返回成功信息
            return buildSuccessRsp(inMap);
        } catch (Exception e) {
            // 返回失败信息
            return ThirdPartyUtils.buildRspMsg(e);
        }
    }

    /**
     * 根据商品所属模板获得商品处理类。
     * 
     * @param tmplId
     * @return
     */
    private AbstractGoodsSrv getGoodsSrv(String tmplId) {
        AbstractGoodsSrv goodsSrv = GoodsSrvFactory.createGoodsSrv(tmplId, dao);
        if (goodsSrv == null) {
            throw new BusinessException(NumConstants.RESP_GOODS_FAIL, "不可销售此类商品");
        }
        return goodsSrv;
    }

    /**
     * 校验商品是否存在。
     * 
     * @param inMap
     * @return
     */
    private void checkGoodsExists(Map inMap) {
        // 校验商品是否存在
        Map params = Collections.newHashMap();
        params.put(KeyConstants.GOODS_ID, inMap.get(KeyConstants.GOODS_ID));
        params.put(KeyConstants.GOODS_STATE, "3"); // 上架状态
        Map ret = dao.selectMap("SubmitOrder.queryGoodsBaseInfo", params);
        if (Collections.isEmpty(ret)) {
            throw new BusinessException(NumConstants.RESP_GOODS_FAIL, "此商品不存在");
        }
        // 带回信息
        inMap.put(KeyConstants.MERCHANT_ID, ret.get(KeyConstants.MERCHANT_ID) + "");
        inMap.put(KeyConstants.TMPL_ID, ret.get(KeyConstants.TMPL_ID) + "");
        inMap.put(KeyConstants.GOODS_NAME, ret.get(KeyConstants.GOODS_NAME));

        // 校验商品是否归属当前商户
        if (!inMap.get(KeyConstants.MERCHANT_CODE).equals(StringUtils.toString(ret.get(KeyConstants.MERCHANT_ID)))) {
            throw new BusinessException(NumConstants.RESP_GOODS_FAIL, "此商品不属于当前商户");
        }

        // 校验产品信息
        params.put(KeyConstants.PRODUCT_CODE, inMap.get(KeyConstants.PRODUCT_CODE));
        Map prodMap = dao.selectMap("SubmitOrder.queryProdInfo", params);
        if (Collections.isEmpty(prodMap)) {
            throw new BusinessException(NumConstants.RESP_GOODS_FAIL, "此产品不存在");
        }
        inMap.put(KeyConstants.BILL_TYPE, prodMap.get(KeyConstants.BILL_TYPE));

    }

    /**
     * 构建返回成功信息。
     * 
     * @param inMap
     * @return
     */
    private Map buildSuccessRsp(Map inMap) {
        Map retMap = ThirdPartyUtils.buildRspMsg(NumConstants.RESP_CODE_SUCCESS, "成功");
        retMap.put(KeyConstants.ORDER_ID, inMap.get(KeyConstants.ORDER_ID));
        return retMap;
    }

    /**
     * 获取号码归属信息。
     * 
     * @param inMap
     * @return
     */
    private static Map getNumBelongInfo(Map inMap) {
        Map params = Collections.newHashMap();
        params.put(KeyConstants.PROVINCE, inMap.get(KeyConstants.NUM_PROVINCE));
        params.put(KeyConstants.CITY, inMap.get(KeyConstants.NUM_CITY));
        params.put(KeyConstants.DISTRICT, inMap.get(KeyConstants.NUM_DISTRICT));
        // TODO:待接口补充渠道信息
        params.put(KeyConstants.OPERATOR_ID, "000088");
        params.put(KeyConstants.CHANNEL_ID, "Z0025");
        params.put(KeyConstants.CHANNEL_TYPE, "1010200");
        params.put(KeyConstants.ACCESS_TYPE, "01");
        return params;
    }

    /**
     * 校验入网客户资料信息。
     * 
     * @param inMap
     * @return
     */
    public static CheckCustRspBody checkNetInCustInfo(Map inMap) {
        Map params = getNumBelongInfo(inMap);
        params.put(KeyConstants.CERT_TYPE, inMap.get(KeyConstants.NET_PSPT_TYPE));
        params.put(KeyConstants.CERT_NUM, inMap.get(KeyConstants.NET_PSPT_NO));

        CheckCustReq req = new CheckCustReq();
        NewCheckCustIface iface = new NewCheckCustIface(req, params);
        Map ret = null;

        try {
            ret = iface.execute(req);
        } catch (Exception e) {
            throw new BusinessException(NumConstants.RESP_CUST_FAIL, CUSTCHECK_RET + e.getMessage());
        }
        if (ret.containsKey("ErrCode")) {
            throw new BusinessException(NumConstants.RESP_CUST_FAIL, CUSTCHECK_RET + ret.get("ErrDesc"));
        }
        CheckCustRspBody body = (CheckCustRspBody)ret.get("body");
        String respCode = body.getRespCode();
        if (!("0000".equals(respCode) || "0001".equals(respCode))) {
            throw new BusinessException(NumConstants.RESP_CUST_FAIL, CUSTCHECK_RET + body.getRespDesc());
        }

        return body;
    }

    /**
     * 校验号码信息。
     * 
     * @param inMap
     * @param numType
     * @return
     */
    public static NumInfoBean checkNumInfo(Map inMap, String numType) {
        Map params = getNumBelongInfo(inMap);
        params.put(KeyConstants.KEY_VALUE, inMap.get(KeyConstants.NET_PHONE_NO));
        params.put(KeyConstants.BACK_NUMBER, "1");
        params.put(KeyConstants.RESOURCE_TYPE, numType);

        QueryLocalNumService qryNumServ = new QueryLocalNumService();
        Map ret = qryNumServ.execute(params, "0");

        NumQryRspBody body = (NumQryRspBody)ret.get("body");
        if (!NumConstants.RESP_CODE_SUCCESS.equals(body.getRespCode())) {
            throw new BusinessException(NumConstants.RESP_RESOUTCE_FAIL, NUMCHECK_RET + body.getRespDesc());
        }

        List<NumInfoBean> numList = body.getNumInfo();
        if (Collections.isEmpty(numList)) {
            throw new BusinessException(NumConstants.RESP_RESOUTCE_FAIL, NUMCHECK_RET + "未查询到号码信息");
        }
        NumInfoBean numInfo = numList.get(0);
        int numLevel = Integer.parseInt(numInfo.getNumLevel());
        ret = ThirdPartyDbUnit.queryGoodsPrdInfo(inMap.get(KeyConstants.GOODS_ID) + "");
        if (Collections.isEmpty(ret)) {
            throw new BusinessException(NumConstants.RESP_RESOUTCE_FAIL, NUMCHECK_RET + "未查询到当前商品对应的产品信息");
        }
        int prdValue = Integer.parseInt(ret.get(KeyConstants.PRODUCT_VALUE) + "");
        if (prdValue < numLevel) {
            throw new BusinessException(NumConstants.RESP_RESOUTCE_FAIL, NUMCHECK_RET + "当前商品的产品等级金额小于所选号码等级金额");
        }
        return numInfo;
    }

    /**
     * 预占号码信息。
     * 
     * @param inMap
     * @return
     * @throws Exception
     */
    public static void occupyNumInfo(Map inMap) {
        logger.info("号码资源预占开始");

        List<Map> resInfoList = new ArrayList();
        Map mapEopParam = new HashMap();
        Map resInfo = new HashMap();

        resInfo.put(KeyConstants.KEY_CHANGE_TAG, "1"); // 生成订单,号码预占关键字改为订单号,避免同客户重复预占
        resInfo.put(KeyConstants.PRO_KEYMODE, "2");
        resInfo.put(KeyConstants.RESOURCE_TYPE, "02"); // TODO 待接口下发
        resInfo.put(KeyConstants.OLD_KEY, inMap.get(KeyConstants.PRO_KEY));
        resInfo.put(KeyConstants.PRO_KEY, inMap.get(KeyConstants.ORDER_ID));
        resInfo.put(KeyConstants.RESOURCE_CODE, inMap.get(KeyConstants.NET_PHONE_NO));

        if (!OrderInfoConstants.PAY_TYPE.equals(inMap.get(KeyConstants.PAY_TYPE))) {

            resInfo.put(KeyConstants.OCCUPY_TYPE, CheckNumResIface.UNPAY_BOOK);
            resInfo.put(KeyConstants.OCCUPY_FLAG, 2);

        } else if (OrderInfoConstants.PAY_STATE.equals(inMap.get(KeyConstants.PAY_TYPE))) {

            resInfo.put(KeyConstants.OCCUPY_TYPE, CheckNumResIface.PAY_BOOK);
            resInfo.put(KeyConstants.OCCUPY_FLAG, 3);

        } else {

            resInfo.put(KeyConstants.OCCUPY_TYPE, CheckNumResIface.PRE_OCCUPY_ONLINE);
            resInfo.put(KeyConstants.OCCUPY_FLAG, 1);
        }

        resInfoList.add(resInfo);

        mapEopParam.put("ResourcesInfo", resInfoList);
        // TODO: 先写死
        mapEopParam.put(KeyConstants.SYSCODE, "CMBC");
        // 准备省份地市渠道等信息
        mapEopParam.putAll(getNumBelongInfo(inMap));

        logger.info("订单提交号码资源预占请求参数：{}", mapEopParam);

        CheckNumReq reqBean = new CheckNumReq();

        Map retMap = new HashMap();
        try {
            NewCheckNumReqIface checkNum = new NewCheckNumReqIface(reqBean, mapEopParam);
            retMap = checkNum.execute(reqBean);
        } catch (ApiException e) {
            logger.info("号码资源预占失败：{}", e.getErrMsg());
            throw new BusinessException(NumConstants.RESP_RESOUTCE_FAIL, "号码资源预占失败：" + e.getErrMsg());
        }
        logger.info("订单提交号码资源预占返回参数：{}", retMap);

        if (null == retMap || retMap.containsKey("ErrCode")) {
            logger.info("号码资源预占失败：{}", retMap.get("ErrDesc"));
            throw new BusinessException(NumConstants.RESP_RESOUTCE_FAIL, "号码资源预占失败：" + retMap.get("ErrDesc"));
        }

        CheckNumRspBody body = (CheckNumRspBody)retMap.get("body");
        if (!"0000".equals(body.getRespCode())) {
            logger.info("号码资源预占失败：此号码已被预定，请重新选择号码！");
            throw new BusinessException(NumConstants.RESP_RESOUTCE_FAIL, "号码资源预占失败：此号码已被预定，请重新选择号码！");
        }

        // TODO 目前List中只有一个Map
        ResourcesNumRspBean resNumBean = body.getResourcesRsp().get(0);
        if (!"0000".equals(resNumBean.getRscStateCode())) {
            logger.info("号码资源预占失败：{}", resNumBean.getRscStateDesc());
            throw new BusinessException(NumConstants.RESP_RESOUTCE_FAIL, "号码资源预占失败：" + resNumBean.getRscStateDesc());
        }
        Map m = ((List<Map>)mapEopParam.get("ResourcesInfo")).get(0);
        inMap.put(KeyConstants.PRO_KEYMODE, "2");
        inMap.put(KeyConstants.OCCUPY_TIME, m.get(KeyConstants.OCCUPY_TIME));
    }

}
