package com.ailk.thirdparty.action.order;

import java.util.HashMap;
import java.util.Map;

import org.phw.eop.support.EopAction;
import org.phw.eop.support.EopActionException;

import com.ailk.base.KeyConst;
import com.ailk.base.NumConst;
import com.ailk.base.utils.FaceUtils;
import com.ailk.thirdparty.utils.ReqProcessor;
import com.ailk.thirdservice.processorder.ProcessOrderService;

/**
 * 订单处理申请接口
 * 
 * @author max
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ProcessOrderAction extends EopAction {

    public static final String REQ_TEMPLATE = "com/ailk/face/thirdparty/order/ProcessOrder_Req.xml";
    public static final String RSP_TEMPLATE = "com/ailk/face/thirdparty/order/ProcessOrder_Rsp.xml";

    // public static final String MALL_PROCESSORDER_SRV =
    // "com.ailk.mall.common.thirdparty.processorder.ProcessOrderService.execute";

    @Override
    public Object doAction() throws EopActionException {
        Map inMap = new HashMap();
        try {
            inMap = ReqProcessor.checkReqXml(getStr(KeyConst.REQ_XML), REQ_TEMPLATE);
            // Map retMap = new ServiceCall("mall").call(MALL_PROCESSORDER_SRV, inMap);
            Map retMap = new ProcessOrderService().execute(inMap);
            inMap.putAll(retMap);
            if (NumConst.RESP_CODE_SUCCESS.equals(retMap.get(KeyConst.RESP_CODE))) {
                // 商城订单号
                inMap.put(KeyConst.MALL_ORDER_NO, retMap.get(KeyConst.ORDER_ID));
                return FaceUtils.createRspXml(inMap, RSP_TEMPLATE);
            }
            throw new Exception((String)retMap.get(KeyConst.RESP_DESC));
        } catch (Throwable t) {
            inMap.put(KeyConst.RESP_CODE, NumConst.RESP_CODE_FAIL);
            inMap.put(KeyConst.RESP_DESC, t.getMessage());
            return buildFailRspMsg(inMap);
        }
    }

    /**
     * 构建返回失败报文。
     * 
     * @param inMap
     * @return
     */
    private String buildFailRspMsg(Map inMap) {
        // 响应报文非空节点
        inMap.put(KeyConst.MALL_ORDER_NO, "null");
        return FaceUtils.createRspXml(inMap, RSP_TEMPLATE);
    }

}
