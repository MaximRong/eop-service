package com.ailk.thirdservice.queryorder;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.FastDateFormat;
import org.phw.core.exception.BusinessException;
import org.phw.core.lang.Collections;
import org.phw.ibatis.engine.PDao;
import org.phw.ibatis.util.PDaoEngines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.thirdservice.base.KeyConstants;
import com.ailk.thirdservice.base.NumConstants;
import com.ailk.thirdservice.submitorder.SubmitOrderService;

/**
 * 第三方接入之订单查询处理。
 * 
 * @author wanglei
 *         2012-2-15
 */
public class QueryOrderService {

    private static final Logger logger   = LoggerFactory.getLogger(SubmitOrderService.class);
    private static final String SQL_PATH = "com/ailk/face/thirdservice/queryorder/QueryOrderSQL.xml";
    public PDao                 dao      = PDaoEngines.getDao(SQL_PATH, "EcsStore");

    public Map execute(Map inMap) {

        Map retMap = Collections.newHashMap();

        // 根据商城订单号查询订单表
        Map orderMap = dao.selectMap("QueryOrder.queryOrderInfo", inMap);

        if (Collections.isEmpty(orderMap)) {
            throw new BusinessException(NumConstants.RESP_ORDER_FAIL, "未查询到该订单");
        }
        inMap.put(KeyConstants.ORDER_ID, orderMap.get(KeyConstants.ORDER_ID));
        retMap.putAll(orderMap);

        // 查询订单入网信息表
        Map orderNetMap = dao.selectMap("QueryOrder.queryOrderNetInfo", inMap);
        retMap.putAll(orderNetMap);

        // 查询邮寄信息表
        Map orderPostMap = dao.selectMap("QueryOrder.queryOrderPostInfo", inMap);
        retMap.putAll(orderPostMap);

        // 查询物流信息
        Map orderLgtsMap = dao.selectMap("QueryOrder.queryOrderLgtsInfo", inMap);
        if (!Collections.isEmpty(orderLgtsMap)) {
            retMap.putAll(orderLgtsMap);
        }

        // 查询发货时间
        Map orderDealMap = dao.selectMap("QueryOrder.queryOrderDealInfo", inMap);
        if (!Collections.isEmpty(orderDealMap)) {
            Timestamp ts = (Timestamp)orderDealMap.get(KeyConstants.DELV_DATE_TIME);
            retMap.put(KeyConstants.DELV_DATE_TIME,
                FastDateFormat.getInstance("yyyyMMddHHmmss").format(new Date(ts.getTime())));
        }

        // 查询费用信息
        List<Map> orderFeeList = dao.select("QueryOrder.queryOrderFeeInfo", inMap);
        retMap.put(KeyConstants.FEE_INFO, orderFeeList);

        // 查询商品实例信息
        List<Map> orderGoodAtvalList = dao.select("QueryOrder.queryOrderGoodAtvalInfo", inMap);
        for (Map<String, String> m : orderGoodAtvalList) {
            String key = m.get(KeyConstants.ATTR_CODE);
            String value = m.get(KeyConstants.ATTR_VAL_CODE);
            if ("A000023".equals(key)) {
                retMap.put(KeyConstants.PRODUCT_CODE, value);

            } else if ("A000011".equals(key)) {
                String fstBillMode = "";
                if ("A000011V000003".equals(value)) {
                    fstBillMode = "01";
                } else if ("A000011V000001".equals(value)) {
                    fstBillMode = "02";
                } else if ("A000011V000002".equals(value)) {
                    fstBillMode = "03";
                }
                retMap.put(KeyConstants.FRSTMON_BILLMODE, fstBillMode);

            } else if ("A000025".equals(key)) {
                retMap.put("PhCodeProvince", m.get(KeyConstants.PROVINCE));
                retMap.put("PhCodeCity", value);
            }

        }
        // 返回成功信息
        return retMap;
    }
}
