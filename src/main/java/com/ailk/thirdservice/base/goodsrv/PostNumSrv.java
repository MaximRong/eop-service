package com.ailk.thirdservice.base.goodsrv;

import java.util.Map;

import org.phw.core.lang.Collections;
import org.phw.ibatis.engine.PDao;

import com.ailk.thirdservice.base.KeyConstants;
import com.ailk.thirdservice.base.NumConstants;

/**
 * 后付费号码订单落地处理类。
 * 
 * @author wanglei 2012-2-15
 */
public class PostNumSrv extends AbstractGoodsSrv {

    public PostNumSrv(PDao dao) {
        super(dao);
    }

    public static final String NUM_TYPE = NumConstants.NUM_TYPE_POST;

    @Override
    public Map checkGoodsDtlInfo(Map inMap) {
        // 校验入网客户资料
        // CheckCustRspBody custInfo = SubmitOrderService.checkNetInCustInfo(inMap);
        // 校验号码信息
        // NumInfoBean numInfo = SubmitOrderService.checkNumInfo(inMap, NUM_TYPE);
        // TODO：校验价格等
        return Collections.asMap(KeyConstants.CUST_INFO, "", KeyConstants.NUM_INFO, "");
    }

}
