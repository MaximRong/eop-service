package com.ailk.thirdservice.processorder;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.phw.eop.api.bean.account.AccountReq;
import org.phw.eop.api.bean.account.rsp.AccountRspBody;
import org.phw.ibatis.engine.PDao;
import org.phw.ibatis.util.PDaoEngines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.mall.common.eop.NewAccountIface;
import com.ailk.thirdservice.base.KeyConstants;

/**
 * 第三方接入订单处理申请
 * 
 * @author max
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ProcessOrderService {

    private static final Logger logger   = LoggerFactory.getLogger(ProcessOrderService.class);
    public static final String  SQL_PATH = "com/ailk/face/thirdservice/processorder/ProcessOrderSQL.xml";
    public static PDao          pDao     = PDaoEngines.getDao(SQL_PATH, "EcsStore");

    public Map execute(Map inMap) {

        Map retMap = null;

        preOrderId(inMap);
        prePsptInfo(inMap);
        preGoodInsInfo(inMap);
        preEopAttrValInfo(inMap);
        prePrdAndActId(inMap);
        preFeesInfo(inMap);
        preOthers(inMap);

        // 调用开户接口
        AccountReq accountBean = new AccountReq();
        NewAccountIface account = new NewAccountIface(accountBean, inMap);

        try {
            retMap = account.execute(accountBean);

        } catch (Exception e) {
            logger.debug("开户失败！", e);
            throw new RuntimeException(e.getMessage());
        }

        if (retMap == null) {
            throw new RuntimeException("调用开户处理申请接口失败");
        }

        if (null != retMap.get("ErrCode")) {
            return retMap;
        }

        AccountRspBody accountBody = (AccountRspBody)retMap.get("body");
        if (!accountBody.getRespCode().equals("0000")) {
            retMap.put("ErrCode", accountBody.getRespCode());
            retMap.put("ErrDesc", accountBody.getRespDesc());
            return retMap;
        }
        // 返回成功
        // retMap.put("ErrCode", accountBody.getRespCode());

        inMap.put("ProvOrderID", accountBody.getProvOrderID());
        // inMap.put("ReceiptCode", accountBody.getAcceptanceTp()); // 受理单模板编码
        // inMap.put("ReceiptMode", accountBody.getAcceptanceMode()); // 受理单模板类型
        // inMap.put("ReceiptForm", accountBody.getAcceptanceForm()); // json对象转化成字符串

        // 开户成功后 更新SIM卡号 , 终端串号, 订单入网资料表
        pDao.start();
        pDao.update("ProcessOrder.addSimCode", inMap);
        pDao.update("ProcessOrder.addImeiCode", inMap);
        pDao.update("ProcessOrder.addOrderNetin", inMap);
        pDao.commit();
        pDao.end();

        return retMap;
    }

    /**
     * 准备商城订单号
     * 
     * @param inMap
     */
    public void preOrderId(Map inMap) {
        String orderId = pDao.selectString("ProcessOrder.queryOrderId", inMap);
        if (StringUtils.isEmpty(orderId)) {
            throw new RuntimeException("未查到对应的联通商城订单");
        }
        inMap.put(KeyConstants.ORDER_ID, orderId);

    }

    /**
     * 查询证件号码,类型,地址信息
     * 
     * @param inMap
     */
    public void prePsptInfo(Map inMap) {
        Map retMap = pDao.selectMap("ProcessOrder.queryPsptInfo", inMap);
        if (null != retMap) {
            inMap.putAll(retMap);
        }
    }

    /**
     * 查询商品实例号, 应付总金额, 实付总金额 目前认为只有一个商品!
     * 
     * @param inMap
     */
    public void preGoodInsInfo(Map inMap) {
        Map retMap = pDao.selectMap("ProcessOrder.queryGoodInsInfo", inMap);
        if (null != retMap) {
            inMap.putAll(retMap);
        }
    }

    /**
     * 获取订单商品属性值表对应ESS属性 如:首月付费方式、号码、付费类型、业务类型
     * 
     * @param inMap
     */

    public void preEopAttrValInfo(Map inMap) {
        inMap.put("attrCodes", new String[]{"A000004", "A000005", "A000011", "A000033"});
        List<Map> eopAttrList = pDao.select("ProcessOrder.queryEopAttrValInfo", inMap);
        for (Map eopAttr : eopAttrList) {
            inMap.put(eopAttr.get(KeyConstants.ATTR_CODE), eopAttr.get(KeyConstants.ATTR_VAL_CODE));
        }
    }

    /**
     * 查询产品ID、活动ID
     * 
     * @param inMap
     */
    public void prePrdAndActId(Map inMap) {
        Map retMap = pDao.selectMap("ProcessOrder.queryPrdAndActId", inMap);
        if (null != retMap) {
            inMap.putAll(retMap);
        }
    }

    /**
     * 准备费用信息
     * 
     * @param inMap
     */
    public void preFeesInfo(Map inMap) {
        List<Map> feesInfoList = pDao.select("ProcessOrder.queryFeesInfo", inMap);
    }

    /**
     * 准备其他信息
     * 
     * @param inMap
     */
    public void preOthers(Map inMap) {
        inMap.put("PROC_ID", inMap.get("CardDataProcID"));
        inMap.put("ORDER_ID", inMap.get(KeyConstants.ORDER_ID));
        inMap.put("NUM_PROC_ID", inMap.get(KeyConstants.ORDER_ID));

    }
}
