package com.ailk.thirdservice.base;

import java.util.List;
import java.util.Map;

import org.phw.ibatis.engine.PDao;
import org.phw.ibatis.util.PDaoEngines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.mall.service.orderno.IOrderNoGenerator;
import com.ailk.mall.service.orderno.OrderNoGeneratorMgr;

/**
 * 第三方接入DB操作类。
 * 
 * @author wanglei 2012-2-27
 */
public class ThirdPartyDbUnit {
    private static final Logger logger   = LoggerFactory.getLogger(ThirdPartyDbUnit.class);
    private static final String SQL_PATH = "com/ailk/face/thirdservice/base/ThirdPartyDbUnitSQL.xml";
    private static PDao         dao      = PDaoEngines.getDao(SQL_PATH, "EcsStore");

    /**
     * 查询商品套餐信息。
     * 
     * @param inMap
     * @return
     */
    public static Map queryGoodsPrdInfo(String goodsId) {
        return dao.selectMap("ThirdPartyDbUnit.queryGoodsProduct", goodsId);
    }

    /**
     * 生成订单号。
     * 
     * @return
     */
    public static String createOrderNo() {
        IOrderNoGenerator ong = OrderNoGeneratorMgr.getOrderNoGenerator();
        try {
            return ong.getOrderNo();
        } catch (Exception e) {
            logger.debug("生成订单号失败", e);
            throw new RuntimeException("生成订单号失败");
        }
    }

    /**
     * 生成订单标识.
     * 
     * @return
     */
    public static String createOrderId() {
        try {
            return dao.selectString("ThirdPartyDbUnit.getOrderId", null);
        } catch (Exception e) {
            logger.error("生成订单标识失败", e);
            throw new RuntimeException("生成订单标识失败");
        }
    }

    /**
     * 获取商品实例标识。
     * 
     * @return
     */
    public static String createGoodsInstId() {
        try {
            return dao.selectString("ThirdPartyDbUnit.getGoodsInstId", null);
        } catch (Exception e) {
            logger.error("生成商品实例标识失败", e);
            throw new RuntimeException("生成商品实例标识失败");
        }
    }

    /**
     * 获取商品二级目录。
     * 
     * @param tmplId
     * @return
     */
    public static String getGoodsLevel(String tmplId) {
        List<Map> list = dao.select("ThirdPartyDbUnit.getGoodsLevel", tmplId);
        if (list.size() >= 2) {
            return list.get(1).get("TMPL_CTGR_CODE") + "";
        }
        return list.get(0).get("TMPL_CTGR_CODE") + "";
    }

    /**
     * 查询商品实例付费类型属性值编码。
     * 
     * @param orderId
     * @return
     */
    public static String queryGoodsResType(String orderId) {
        return dao.selectString("ThirdPartyDbUnit.queryGoodsResType", orderId);
    }

    /**
     * 查询商品实例手机号码。
     * 
     * @param orderId
     * @return
     */
    public static String queryGoodsNumber(String orderId) {
        return dao.selectString("ThirdPartyDbUnit.queryGoodsNumber", orderId);
    }

    /**
     * 是否允许记录特殊订单。
     * 
     * @param inMap
     * @return
     */
    public static boolean specOrderAuthority(Map inMap) {
        int count = dao.selectInteger("ThirdPartyDbUnit.isNeedSpecOrder", inMap);
        return count > 0 ? true : false;
    }

    /**
     * 记录号码预定订单。
     * 
     * @param inMap
     * @return
     */
    public static void recordNumOrder(Map inMap) {
        dao.insert("ThirdPartyDbUnit.recordNumOrder", inMap);
    }

}
