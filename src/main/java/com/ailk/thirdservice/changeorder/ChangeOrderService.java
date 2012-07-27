package com.ailk.thirdservice.changeorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.phw.core.lang.Collections;
import org.phw.eop.api.bean.checknum.CheckNumReq;
import org.phw.eop.api.bean.checknum.rsp.CheckNumRspBody;
import org.phw.eop.api.bean.checknum.rsp.ResourcesNumRspBean;
import org.phw.ibatis.engine.PDao;
import org.phw.ibatis.util.PDaoEngines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.mall.base.utils.EopConstantUtils.NumResModify;
import com.ailk.mall.common.eop.NewCheckNumReqIface;
import com.ailk.thirdservice.base.KeyConstants;
import com.ailk.thirdservice.base.NumConstants;
import com.ailk.thirdservice.base.ThirdPartyDbUnit;
import com.ailk.thirdservice.base.ThirdPartyUtils;
import com.ailk.thirdservice.base.goodsrv.AbstractGoodsSrv;
import com.ailk.thirdservice.base.goodsrv.GoodsSrvFactory;

/**
 * 第三方接入之订单状态变更。
 * 
 * @author wanglei 2012-2-24
 */
public class ChangeOrderService {
    private static final Logger logger         = LoggerFactory.getLogger(ChangeOrderService.class);
    private static final String NUM_OCCUPY_RET = "号码预占失败：";
    private static final String SQL_PATH       = "com/ailk/face/thirdservice/changeorder/ChangeOrderSQL.xml";
    public PDao                 dao            = PDaoEngines.getDao(SQL_PATH, "EcsStore");

    public Map execute(Map inMap) {
        try {
            checkOrderState(inMap);
            AbstractGoodsSrv goodsSrv = getGoodsSrv(inMap);
            goodsSrv.changeOrderState(inMap);
            return ThirdPartyUtils.buildRspMsg(NumConstants.RESP_CODE_SUCCESS, "成功");
        } catch (Exception e) {
            return ThirdPartyUtils.buildRspMsg(e);
        }
    }

    private void checkOrderState(Map inMap) {
        inMap.put(KeyConstants.ORDER_STATE, inMap.get(KeyConstants.OLD_STATE));
        Map retMap = dao.selectMap("ChangeOrder.checkOrderCurrentState", inMap);
        if (Collections.isEmpty(retMap)) {
            throw new RuntimeException("订单原状态与商城侧不一致");
        }
        inMap.put(KeyConstants.ORDER_ID, retMap.get(KeyConstants.ORDER_ID));

        retMap = dao.selectMap("ChangeOrder.checkOrderStateChange", inMap);
        if (Collections.isEmpty(retMap)) {
            throw new RuntimeException("无法进行指定的状态变更");
        }
        inMap.putAll(retMap);
    }

    private AbstractGoodsSrv getGoodsSrv(Map inMap) {
        Map retMap = dao.selectMap("ChangeOrder.getGoodsTmplId", inMap);
        String tmplId = retMap.get(KeyConstants.TMPL_ID) + "";
        AbstractGoodsSrv goodsSrv = GoodsSrvFactory.createGoodsSrv(tmplId, dao);
        return goodsSrv;
    }

    /**
     * 未支付变更已支付，付费预定号码信息。
     * 
     * @param inMap
     */
    public static void occupyNumberPaied(Map inMap) {
        Map params = inMap;
        String orderId = params.get(KeyConstants.ORDER_ID) + "";
        params.put(KeyConstants.ACCESS_TYPE, "01");
        List resList = new ArrayList();
        Map resMap = new HashMap();
        resMap.put(KeyConstants.KEY_CHANGE_TAG, "0");
        resMap.put(KeyConstants.PRO_KEYMODE, "2");
        resMap.put(KeyConstants.PRO_KEY, orderId);

        String valCode = ThirdPartyDbUnit.queryGoodsResType(orderId);
        String resType = NumResModify.GOODS_PAY_AFTER.equals(valCode) ? NumConstants.PAYFEE_TYPE_POST
            : NumConstants.PAYFEE_TYPE_PRE;
        resMap.put(KeyConstants.RESOURCE_TYPE, resType);
        resMap.put(KeyConstants.RESOURCE_CODE, ThirdPartyDbUnit.queryGoodsNumber(orderId));
        resMap.put(KeyConstants.OCCUPY_FLAG, NumConstants.OCCUPY_FLAG_PAIED);
        resMap.put(KeyConstants.OCCUPY_TYPE, NewCheckNumReqIface.PAY_BOOK);
        resList.add(resMap);
        params.put(KeyConstants.RESOURCE_INFO, resList);

        CheckNumReq checkNumReqBean = new CheckNumReq();
        Map ret = null;
        try {
            NewCheckNumReqIface checkNum = new NewCheckNumReqIface(checkNumReqBean, params);
            ret = checkNum.execute(checkNumReqBean);
        } catch (Exception e) {
            throw new RuntimeException(NUM_OCCUPY_RET + e.getMessage());
        }
        if (ret.containsKey("ErrCode")) {
            throw new RuntimeException(NUM_OCCUPY_RET + ret.get("ErrDesc"));
        }
        CheckNumRspBody body = (CheckNumRspBody)ret.get("body");
        if (!NumConstants.RESP_CODE_SUCCESS.equals(body.getRespCode())) {
            throw new RuntimeException(NUM_OCCUPY_RET);
        }
        ResourcesNumRspBean numRsp = body.getResourcesRsp().get(0);
        if (!NumConstants.RESP_CODE_SUCCESS.equals(numRsp.getRscStateCode())) {
            throw new RuntimeException(NUM_OCCUPY_RET + numRsp.getRscStateDesc());
        }
    }

}
