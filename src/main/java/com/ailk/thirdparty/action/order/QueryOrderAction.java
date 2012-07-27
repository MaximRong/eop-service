package com.ailk.thirdparty.action.order;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.FastDateFormat;
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
import com.ailk.thirdservice.base.KeyConstants;
import com.ailk.thirdservice.base.NumConstants;
import com.ailk.thirdservice.queryorder.QueryOrderService;

/**
 * 第三方订单查询接口
 * 
 * @author max
 */
public class QueryOrderAction extends EopAction implements EopSystemParamAware {

    private static final String REQ_TEMPLATE = "com/ailk/face/thirdparty/order/QueryOrder_Req.xml";
    private static final String RSP_TEMPLATE = "com/ailk/face/thirdparty/order/QueryOrder_Rsp.xml";
    private static final String MALL_SQL     = "com/ailk/sql/MallCommonSQL.xml";
    private PDao                mallDao      = PDaoEngines.getDao(MALL_SQL, "EcsStore");
    private EopSystemParam      sysParam     = null;

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

            inMap.putAll(new QueryOrderService().execute(inMap));
            inMap.put(KeyConstants.RESP_CODE, NumConstants.RESP_CODE_SUCCESS);
            inMap.put(KeyConstants.RESP_DESC, "成功");

            return FaceUtils.createRspXml(inMap, RSP_TEMPLATE);
        } catch (Throwable t) {
            if (t instanceof BusinessException) {
                inMap.put(KeyConst.RESP_CODE, ((BusinessException)t).getMessageCode());
            } else {
                inMap.put(KeyConst.RESP_CODE, NumConst.RESP_CODE_FAIL);
            }
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
        inMap.put(KeyConstants.MERCHANT_CODE, "0000000");
        inMap.put(KeyConstants.SUBSCRIBE_STATE, "20");
        inMap.put(KeyConstants.GOODS_ID, "000000000000");
        inMap.put(KeyConstants.FEE_SUM, "0");
        inMap.put(KeyConstants.PRODUCT_CODE, "null");
        inMap.put(KeyConstants.PAY_TYPE, "01");
        inMap.put(KeyConstants.PAY_STATE, "0");
        inMap.put(KeyConstants.PAY_WAY, "00");
        inMap.put(KeyConstants.ALREADY_PAY, "0");
        inMap.put(KeyConstants.DELV_TYPE, "01");
        inMap.put(KeyConstants.DELV_DATE_TYPE, "00");
        inMap.put(KeyConstants.RECV_CUST_NAME, "null");
        inMap.put(KeyConstants.POST_PROVINCE, "000000");
        inMap.put(KeyConstants.POST_CITY, "000000");
        inMap.put(KeyConstants.POST_ADDR, "null");
        inMap.put("SubscribeTime", FastDateFormat.getInstance("yyyyMMddHHmmss").format(new Date()));
        inMap.put("PhCode", "null");
        inMap.put("PhCodeType", "1");
        inMap.put("PhCodeProvince", "00");
        inMap.put("PhCodeCity", "000");
        inMap.put("AffirmTag", "1");

        return FaceUtils.createRspXml(inMap, RSP_TEMPLATE);
    }

    @Override
    public void setSystemParam(EopSystemParam sysParam) {
        this.sysParam = sysParam;
    }

}
