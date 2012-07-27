package com.ailk.ess.n6ess.cust;

import java.util.Map;

import org.phw.eop.support.EopAction;
import org.phw.eop.support.EopActionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.base.KeyConst;
import com.ailk.base.NumConst;
import com.ailk.base.utils.FaceUtils;

/**
 * 北六客户资料校验。
 *
 * @author wanglei
 *
 * 2012-3-31
 */
public class N6CheckCustAction extends EopAction {
    private static final Logger logger = LoggerFactory.getLogger(N6CheckCustAction.class);
    private static final String RSP_TEMPLATE = "com/ailk/face/g3ess/cust/CheckCust_Rsp.xml";

    @Override
    public Object doAction() throws EopActionException {
        logger.info("N6ESS客户资料校验请求参数：" + getParams());
        Map headMap = get(KeyConst.REQ_HEAD);
        headMap.put(KeyConst.RESP_CODE, NumConst.RESP_CODE_SUCCESS);
        headMap.put(KeyConst.RESP_DESC, "成功");
        String rspXml = FaceUtils.createRspXml(headMap, RSP_TEMPLATE);
        logger.info("N6ESS客户资料校验响应报文：" + rspXml);
        return FaceUtils.parseRspXml2EssMap(rspXml);
    }

}
