package com.ailk.thirdservice.base.goodsrv;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.phw.core.exception.BusinessException;
import org.phw.core.lang.Collections;
import org.phw.ibatis.engine.PDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.thirdservice.base.KeyConstants;
import com.ailk.thirdservice.base.NumConstants;
import com.ailk.thirdservice.base.ThirdPartyDbUnit;
import com.ailk.thirdservice.changeorder.ChangeOrderService;

/**
 * 订单落地处理父类。
 * 
 * @author wanglei 2012-2-15
 */
public abstract class AbstractGoodsSrv {
    private static final Logger logger = LoggerFactory.getLogger(AbstractGoodsSrv.class);
    public PDao                 dao;

    public AbstractGoodsSrv(PDao dao) {
        this.dao = dao;
    }

    /**
     * 商品合法性校验。
     * 
     * @param inMap
     * @return
     */
    public abstract Map checkGoodsDtlInfo(Map inMap);

    /**
     * 入库前准备数据。
     * 
     * @param inMap
     */
    public void prepareOrderData(Map inMap) {
        // 订单信息
        inMap.put(KeyConstants.DEAL_CONTENT, "第三方系统订单入库");
        inMap.put(KeyConstants.RESULT_CODE, "1");
        inMap.put(KeyConstants.RESULT_INFO, "成功");
        inMap.put(KeyConstants.POST_TAG, "0");
        inMap.put(KeyConstants.ORDER_ID, ThirdPartyDbUnit.createOrderId());
        inMap.put(KeyConstants.ORDER_NO, ThirdPartyDbUnit.createOrderNo());
        inMap.put(KeyConstants.ORDER_STATE, inMap.get(KeyConstants.SUBSCRIBE_STATE));

        // 商品信息
        String tmplId = (String)inMap.get(KeyConstants.TMPL_ID);
        inMap.put(KeyConstants.GOODS_CTLG_CODE, ThirdPartyDbUnit.getGoodsLevel(tmplId));
        inMap.put(KeyConstants.GOODS_INST_ID, ThirdPartyDbUnit.createGoodsInstId());

        // 费用数据
        String feeSum = (String)inMap.get(KeyConstants.FEE_SUM);
        String alreadyPay = (String)inMap.get(KeyConstants.ALREADY_PAY);
        String postage = (String)inMap.get(KeyConstants.POSTAGE);
        inMap.put(KeyConstants.ORIGINAL_PRICE, convertFee(feeSum));
        inMap.put(KeyConstants.COUPON_MONEY, "0");
        inMap.put(KeyConstants.TOPAY_MONEY, convertFee(feeSum, postage));
        inMap.put(KeyConstants.INCOME_MONEY, convertFee(alreadyPay, postage));
        inMap.put(KeyConstants.POST_FEE, convertFee(postage));
        // TODO: 商品应付、实付价格
        inMap.put(KeyConstants.AMOUNT_RECEIVED, convertFee(alreadyPay));
        inMap.put(KeyConstants.AMOUNT_RECEVABLE, convertFee(feeSum));
    }

    /**
     * 落订单信息。
     * 
     * @param inMap
     * @return
     * @throws Exception
     */
    public void recordOrderInfo(Map inMap) throws Exception {
        // 准备数据
        prepareOrderData(inMap);
        // TODO:根据商品类别决定是否预占号码
        // SubmitOrderService.occupyNumInfo(inMap);

        // 校验是商城是否存在该订单
        String mallOrderId = dao.selectString("SubmitOrder.queryOrderInfo", inMap);
        if (StringUtils.isNotEmpty(mallOrderId)) {
            throw new BusinessException(NumConstants.RESP_RE_SUBMIT, mallOrderId);
        }

        boolean transactionStart = false;
        try {
            transactionStart = dao.tryStart();
            dao.startBatch();
            dao.insert("SubmitOrder.insertOrder", inMap);
            // TODO: 查询商品是否有号码属性, 有则记录订单信息表
            dao.insert("SubmitOrder.insertOrderNetin", inMap);
            dao.insert("SubmitOrder.insertOrderPost", inMap);
            dao.insert("SubmitOrder.insertOrderGoodsins", inMap);
            recordOrderGoodsInsAttr(inMap);
            recordOrderFee(inMap);
            dao.insert("SubmitOrder.insertOrderThirdpartyRef", inMap); // 记录商城订单与第三方订单关系
            dao.insert("SubmitOrder.insertOrderDeal", inMap);
            dao.executeBatch();
            dao.commit(transactionStart);
        } catch (Exception e) {
            logger.error("生成订单失败", e);
            throw new BusinessException(NumConstants.RESP_ORDER_FAIL, "生成订单失败");
        } finally {
            dao.end(transactionStart);
        }
    }

    /**
     * 添加商品实例属性表.
     * 
     * @param inMap
     */
    public void recordOrderGoodsInsAttr(Map inMap) {

        // 首月付费模式转换 接口是01,02,03要转换成商城编码
        String fstBillMode = (String)inMap.get(KeyConstants.FRSTMON_BILLMODE);
        if ("01".equals(fstBillMode)) {
            fstBillMode = "A000011V000003";
        } else if ("02".equals(fstBillMode)) {
            fstBillMode = "A000011V000001";
        } else if ("03".equals(fstBillMode)) {
            fstBillMode = "A000011V000002";
        }

        String[] attrCodeArr = {"A000003", "A000004", "A000005", "A000009", "A000011", "A000020", "A000022", "A000023",
            "A000025", "A000026", "A000032", "A000033"};

        String[] attrCodeValArr = {"A000003V000002", "A000004V000002", inMap.get(KeyConstants.NET_PHONE_NO).toString(),
            (String)inMap.get(KeyConstants.BILL_TYPE), fstBillMode, (String)inMap.get(KeyConstants.NETPHONE_FROM),
            (String)inMap.get(KeyConstants.USIM_FEE), (String)inMap.get(KeyConstants.PRODUCT_CODE),
            (String)inMap.get(KeyConstants.NUM_CITY), (String)inMap.get(KeyConstants.PROD_BILL),
            (String)inMap.get(KeyConstants.EXTRA_ADVANCE_FEE), "1"};

        Map m = new HashMap();
        for (int i = 0; i < attrCodeArr.length; i++) {
            m.put(KeyConstants.ATTR_CODE, attrCodeArr[i]);
            m.put(KeyConstants.ATTR_VAL_CODE, attrCodeValArr[i]);
            m.put(KeyConstants.ORDER_ID, inMap.get(KeyConstants.ORDER_ID));
            m.put(KeyConstants.GOODS_INST_ID, inMap.get(KeyConstants.GOODS_INST_ID));
            m.put(KeyConstants.GOODS_ID, inMap.get(KeyConstants.GOODS_ID));
            dao.insert("SubmitOrder.insertOrderGoodsInsAttr1", m);
        }

        inMap.put("attrCodes", Arrays.asList(attrCodeArr));
        dao.insert("SubmitOrder.insertOrderGoodsInsAttr2", inMap);
    }

    /**
     * 记录订单费用明细表。
     * 
     * @param inMap
     */
    public void recordOrderFee(Map inMap) {
        Map map = new HashMap();
        map.put(KeyConstants.ORDER_ID, inMap.get(KeyConstants.ORDER_ID));
        List<Map> feeList = (List<Map>)inMap.get(KeyConstants.FEE_INFO);
        if (Collections.isEmpty(feeList)) {
            return;
        }
        for (Map feeMap : feeList) {
            map.put(KeyConstants.FEE_CODE, feeMap.get(KeyConstants.FEE_CODE));
            String fee = (String)feeMap.get(KeyConstants.FEE);
            map.put(KeyConstants.FEE, convertFee(fee));

            // 把费用细项信息放入inMap中, 入商品属性表的时候需要
            inMap.put(feeMap.get(KeyConstants.FEE_CODE), convertFee(fee));

            dao.insert("SubmitOrder.insertOrderFee", map);
        }
        // TODO: 若费用细项包含邮寄费, 则不需要此处代码
        // map.put(KeyConstants.FEE_CODE, NumConstants.FEE_TYPE_POST);
        // String postage = (String)inMap.get(KeyConstants.POSTAGE);
        // map.put(KeyConstants.FEE, convertFee(postage));
        // dao.insert("SubmitOrder.insertOrderFee", map);
    }

    /**
     * 转化接口费用to入库费用(单位:分to厘)。
     * 
     * @param feeStrs
     * @return
     */
    public int convertFee(String... feeStrs) {
        int fee = 0;
        for (String feeStr : feeStrs) {
            if (!StringUtils.isNumeric(feeStr) || StringUtils.isEmpty(feeStr)) {
                continue;
            }
            fee += Integer.parseInt(feeStr) * 10;
        }
        return fee;
    }

    /**
     * 变更订单状态。
     * 
     * @param inMap
     * @return
     */
    public void changeOrderState(Map inMap) {
        // 未支付变更已支付
        String newPayState = (String)inMap.get(KeyConstants.NEW_PAY_STATE);
        if (NumConstants.PAY_STATE_PAIED.equals(newPayState)) {
            ChangeOrderService.occupyNumberPaied(inMap);
        }
        // TODO:其他业务场景
        // 更新订单状态
        boolean transactionStart = false;
        try {
            transactionStart = dao.tryStart();
            dao.startBatch();
            dao.update("ChangeOrder.changeOrderState", inMap);
            dao.insert("ChangeOrder.recordOrderDeal", inMap);
            dao.executeBatch();
            dao.commit(transactionStart);
        } catch (Exception e) {
            logger.error("更新订单状态失败", e);
            throw new RuntimeException("更新订单状态失败");
        } finally {
            dao.end(transactionStart);
        }
    }
}
