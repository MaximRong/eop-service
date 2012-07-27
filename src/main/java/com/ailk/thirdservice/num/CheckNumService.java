package com.ailk.thirdservice.num;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.phw.eop.api.bean.checknum.CheckNumReq;
import org.phw.eop.api.bean.checknum.rsp.CheckNumRspBody;
import org.phw.eop.api.bean.checknum.rsp.ResourcesNumRspBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.mall.common.eop.NewCheckNumReqIface;
import com.ailk.thirdservice.base.KeyConstants;
import com.ailk.thirdservice.base.NumConstants;
import com.ailk.thirdservice.base.ThirdPartyDbUnit;
import com.ailk.thirdservice.base.ThirdPartyUtils;

/**
 * 第三方接入之号码状态变更处理。
 * 
 * @author wanglei 2012-3-8
 */
public class CheckNumService {
    private static final Logger logger      = LoggerFactory.getLogger(CheckNumService.class);
    private static final String SYSCODE_BCK = "SysCodeBck";

    /**
     * 号码状态变更处理。
     * 
     * @param inMap
     * @return
     */
    public Map execute(Map inMap) {
        try {
            // 备份SysCode
            inMap.put(SYSCODE_BCK, inMap.get(KeyConstants.SYSCODE));
            inMap.put(KeyConstants.SYSCODE, NumConstants.SYSCODE_MALL);

            inMap.put(KeyConstants.ACCEPT_CHANNEL_TAG, "0");
            inMap.put(KeyConstants.DEVELOP_PERSON_TAG, "0");

            CheckNumReq req = new CheckNumReq();
            NewCheckNumReqIface face = new NewCheckNumReqIface(req, inMap);
            return handleResult(inMap, face.execute(req));
        } catch (Exception e) {
            return ThirdPartyUtils.buildRspMsg(e);
        }
    }

    /**
     * 处理接口调用结果。
     * 
     * @param inMap
     * @param result
     */
    private Map handleResult(Map inMap, Map result) {
        String errCode = (String)result.get("ErrCode");
        if (errCode == null) {
            CheckNumRspBody body = (CheckNumRspBody)result.get("body");
            ResourcesNumRspBean resBean = body.getResourcesRsp().get(0);
            if (NumConstants.RESP_CODE_SUCCESS.equals(body.getRespCode())
                && body.getRespCode().equals(resBean.getRscStateCode())) {
                logger.info("号码状态变更IFace结果：业务级成功");
                // 号码状态变更成功，记录号码预定订单
                numOrder(inMap, result);
            }
        }
        if (result.containsKey("ErrCode")) {
            logger.info("号码状态变更IFace结果：ErrCode=" + errCode + "，ErrDesc：" + result.get("ErrDesc"));
            result.put(KeyConstants.RESP_CODE, NumConstants.RESP_CODE_FAIL);
            result.put(KeyConstants.RESP_DESC, result.get("ErrDesc"));
        }
        return result;
    }

    /**
     * 记录号码预定订单。
     * 
     * @param inMap
     * @param retMap
     */
    private void numOrder(Map inMap, Map retMap) {
        // 还原SysCode
        inMap.put(KeyConstants.SYSCODE, inMap.get(SYSCODE_BCK));
        try {
            if (isNeedNumOrder(inMap)) {
                // 订单号
                String orderId = ThirdPartyDbUnit.createOrderId();
                logger.info("需要记录号码预定订单");
                inMap.put(KeyConstants.ORDER_ID, orderId);
                ThirdPartyDbUnit.recordNumOrder(inMap);
                logger.info("生成号码预定订单成功，订单号：" + orderId);
            }
        } catch (Exception e) {
            logger.error("生成号码预定订单失败：", e);
        }
    }

    /**
     * 判断是否需要记录号码预定订单。
     * 
     * @param inMap
     * @return
     */
    private boolean isNeedNumOrder(Map inMap) {
        try {
            Map map = new HashMap();
            map.put(KeyConstants.SYSCODE, inMap.get(KeyConstants.SYSCODE));
            map.put(KeyConstants.ORDER_TYPE, NumConstants.SPEC_ORDER_TYPE_NUM);
            List<Map> resLst = (List<Map>)inMap.get(KeyConstants.RESOURCE_INFO);
            Map resMap = resLst.get(0);
            map.put("Rule_Key1", resMap.get(KeyConstants.OCCUPY_FLAG));

            // 提取入库信息
            inMap.put(KeyConstants.RESOURCE_CODE, resMap.get(KeyConstants.RESOURCE_CODE));
            inMap.put(KeyConstants.OCCUPY_TIME, resMap.get(KeyConstants.OCCUPY_TIME));
            inMap.put(KeyConstants.CUST_NAME, resMap.get(KeyConstants.CUST_NAME));
            inMap.put(KeyConstants.CERT_TYPE, resMap.get(KeyConstants.CERT_TYPE));
            inMap.put(KeyConstants.CERT_NUM, resMap.get(KeyConstants.CERT_NUM));
            inMap.put("ProductID", resMap.get("ProductID"));
            inMap.put(KeyConstants.ORDER_NO, resMap.get(KeyConstants.ORDER_NO));
            inMap.put(KeyConstants.REMARK, resMap.get(KeyConstants.REMARK));

            return ThirdPartyDbUnit.specOrderAuthority(map);
        } catch (Exception e) {
            logger.error("判断是否需要号码预定订单失败：读取特殊订单配置表失败。", e);
            return false;
        }
    }
}
