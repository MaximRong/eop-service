package com.ailk.thirdparty.action.order;

import java.util.HashMap;
import java.util.Map;

import org.phw.eop.support.EopAction;
import org.phw.eop.support.EopActionException;

import com.ailk.base.KeyConst;
import com.ailk.base.NumConst;
import com.ailk.base.utils.FaceUtils;
import com.ailk.thirdparty.utils.ReqProcessor;
import com.ailk.thirdservice.changeorder.ChangeOrderService;

/**
 * 订单状态变更Action。
 * 
 * @author wanglei
 *         2012-2-24
 */
public class ChangeOrderAction extends EopAction {

    public static final String REQ_TEMPLATE = "com/ailk/face/thirdparty/order/ChangeOrder_Req.xml";
    public static final String RSP_TEMPLATE = "com/ailk/face/thirdparty/order/ChangeOrder_Rsp.xml";

    // public static final String MALL_CHANGEORDER_SRV =
    // "com.ailk.mall.common.thirdparty.changeorder.ChangeOrderService.execute";

    @Override
    public Object doAction() throws EopActionException {
        Map inMap = new HashMap();
        try {
            inMap = ReqProcessor.checkReqXml(getStr(KeyConst.REQ_XML), REQ_TEMPLATE);
            // Map retMap = new ServiceCall("mall").call(MALL_CHANGEORDER_SRV, inMap);
            Map retMap = new ChangeOrderService().execute(inMap);
            inMap.putAll(retMap);
            if (NumConst.RESP_CODE_SUCCESS.equals(retMap.get(KeyConst.RESP_CODE))) {
                return FaceUtils.createRspXml(inMap, RSP_TEMPLATE);
            }
            throw new Exception((String)retMap.get(KeyConst.RESP_DESC));
        } catch (Throwable t) {
            inMap.put(KeyConst.RESP_CODE, NumConst.RESP_CODE_FAIL);
            inMap.put(KeyConst.RESP_DESC, t.getMessage());
            return FaceUtils.createRspXml(inMap, RSP_TEMPLATE);
        }
    }

}
