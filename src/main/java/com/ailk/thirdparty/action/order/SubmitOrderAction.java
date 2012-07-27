package com.ailk.thirdparty.action.order;

import java.util.HashMap;
import java.util.Map;

import org.phw.core.exception.BusinessException;
import org.phw.core.lang.Collections;
import org.phw.eop.domain.EopSystemParam;
import org.phw.eop.support.EopAction;
import org.phw.eop.support.EopActionException;
import org.phw.eop.support.EopSystemParamAware;
import org.phw.ibatis.engine.PDao;
import org.phw.ibatis.util.PDaoEngines;

import com.ailk.base.KeyConst;
import com.ailk.base.NumConst;
import com.ailk.base.utils.FaceUtils;
import com.ailk.thirdparty.utils.ReqProcessor;
import com.ailk.thirdservice.base.NumConstants;
import com.ailk.thirdservice.submitorder.SubmitOrderService;

/**
 * 订单提交Action。
 * 
 * @author wanglei
 *         2012-2-13
 */
public class SubmitOrderAction extends EopAction implements EopSystemParamAware {

    private static final String REQ_TEMPLATE = "com/ailk/face/thirdparty/order/SubmitOrder_Req.xml";
    private static final String RSP_TEMPLATE = "com/ailk/face/thirdparty/order/SubmitOrder_Rsp.xml";
    private static final String MALL_SQL     = "com/ailk/sql/MallCommonSQL.xml";
    private PDao                mallDao      = PDaoEngines.getDao(MALL_SQL, "EcsStore");
    private EopSystemParam      sysParam     = null;

    // private static final String MALL_SUBMITORDER_SRV =
    // "com.ailk.mall.common.thirdparty.submitorder.SubmitOrderService.execute";

    @Override
    public Object doAction() throws EopActionException {
        Map inMap = new HashMap();
        try {
            inMap = ReqProcessor.checkReqXml(getStr(KeyConst.REQ_XML), REQ_TEMPLATE);

            // 查询第三方syscode
            String appId = sysParam.getEopApp().getAppid();
            Map sysMap = mallDao.selectMap("MallCommon.getAppSysCode", appId);
            if (Collections.isEmpty(sysMap)) {
                throw new RuntimeException("未查询到接入方的系统编码信息");
            }
            inMap.put(KeyConst.SYSCODE, sysMap.get(KeyConst.SYSCODE));

            Map retMap = new SubmitOrderService().execute(inMap);
            inMap.putAll(retMap);
            if (NumConst.RESP_CODE_SUCCESS.equals(retMap.get(KeyConst.RESP_CODE))) {
                // 商城订单号
                inMap.put(KeyConst.MALL_ORDER_NO, retMap.get(KeyConst.ORDER_ID));
                return FaceUtils.createRspXml(inMap, RSP_TEMPLATE);
            }
            throw new BusinessException((String)retMap.get(KeyConst.RESP_CODE), (String)retMap.get(KeyConst.RESP_DESC));
        } catch (Throwable t) {
            if (t instanceof BusinessException) {
                inMap.put(KeyConst.RESP_CODE, ((BusinessException)t).getMessageCode());
            } else {
                inMap.put(KeyConst.RESP_CODE, NumConst.RESP_CODE_FAIL);
            }

            if (NumConstants.RESP_RE_SUBMIT.equals(inMap.get(KeyConst.RESP_CODE))) {
                inMap.put(KeyConst.RESP_DESC, "该订单已经生成");
                inMap.put(KeyConst.MALL_ORDER_NO, t.getMessage());
            } else {
                inMap.put(KeyConst.RESP_DESC, t.getMessage());
                inMap.put(KeyConst.MALL_ORDER_NO, "null");
            }
            return FaceUtils.createRspXml(inMap, RSP_TEMPLATE);
        }
    }

    @Override
    public void setSystemParam(EopSystemParam sysParam) {
        this.sysParam = sysParam;
    }

}
