package com.ailk.thirdparty.action.num;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.phw.eop.support.EopAction;
import org.phw.eop.support.EopActionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.base.KeyConst;
import com.ailk.base.NumConst;
import com.ailk.base.utils.FaceUtils;
import com.ailk.ecs.esf.base.utils.StrUtils;
import com.ailk.thirdparty.utils.ReqProcessor;
import com.ailk.thirdservice.num.CheckNumService;

/**
 * 号码状态变更Action。
 * 
 * @author wanglei
 *         2012-2-7
 */
public class CheckNumAction extends EopAction {
    private Logger logger = LoggerFactory.getLogger(CheckNumAction.class);
    // private static final String MALL_CHECKNUM_SRV =
    // "com.ailk.mall.common.thirdparty.num.CheckNumService.execute";
    public static final String REQ_TEMPLATE = "com/ailk/face/thirdparty/num/CheckNum_Req.xml";
    public static final String RSP_TEMPLATE = "com/ailk/face/thirdparty/num/CheckNum_Rsp.xml";
    private static final Pattern pattern = Pattern.compile("<TransIDO>\\w*</TransIDO>");

    @Override
    public Object doAction() throws EopActionException {
        Map inMap = new HashMap();
        try {
            logger.info("号码状态变更开始，请求报文：" + getStr(KeyConst.REQ_XML));
            // 校验请求报文
            inMap = ReqProcessor.checkReqXml(getStr(KeyConst.REQ_XML), REQ_TEMPLATE);
            // 添加预约信息
            addNumOrderInfo(inMap);
            // 调用商城APP
            // Map retMap = new ServiceCall("mall").call(MALL_CHECKNUM_SRV, inMap);
            Map retMap = new CheckNumService().execute(inMap);
            String rspXml = (String) retMap.get(KeyConst.RSP_XML);
            if (StringUtils.isNotEmpty(rspXml)) {
                // TransIDO一致性处理（ZZZD）
                String syncRspXml = syncTransIDO(rspXml, (String) inMap.get(KeyConst.TRANS_IDO));
                logger.info("响应报文：" + syncRspXml);
                return syncRspXml;
            }
            if (!retMap.containsKey(KeyConst.RESP_CODE)) {
                logger.error("响应报文为空");
                throw new RuntimeException("响应报文为空");
            }
            // 错误响应编码
            inMap.put(KeyConst.RESP_CODE, retMap.get(KeyConst.RESP_CODE));
            inMap.put(KeyConst.RESP_DESC, retMap.get(KeyConst.RESP_DESC));
        }
        catch (Throwable t) {
            logger.error("号码状态变更失败", t);
            inMap.put(KeyConst.RESP_CODE, NumConst.RESP_CODE_FAIL);
            inMap.put(KeyConst.RESP_DESC, t.getMessage());
        }
        return buildFailRspMsg(inMap);
    }

    /**
     * 构建返回失败报文。
     * 
     * @param inMap
     * @return
     */
    private String buildFailRspMsg(Map inMap) {
        // 响应报文非空节点
        fillNotEmptyRspNode(inMap);
        // 构建响应报文
        String failRspXml = FaceUtils.createRspXml(inMap, RSP_TEMPLATE);
        logger.error("号码状态变更结束，默认报文：" + failRspXml);
        return failRspXml;
    }

    /**
     * 填充响应报文非空节点。
     */
    private void fillNotEmptyRspNode(Map inMap) {
        Map rspRes = new HashMap();
        rspRes.put(KeyConst.RESOURCE_TYPE, NumConst.RESOURCE_TYPE_SERIAL);
        rspRes.put(KeyConst.RESOURCE_CODE, "null");
        rspRes.put(KeyConst.RSC_STATE_CODE, NumConst.RESP_CODE_FAIL);
        String respDesc = (String) inMap.get(KeyConst.RESP_DESC);
        rspRes.put(KeyConst.RSC_STATE_DESC, StringUtils.isNotEmpty(respDesc) ? StrUtils.substrb(respDesc, 100)
                : respDesc);
        rspRes.put(KeyConst.NUM_ID, "null");
        List<Map> rspResList = new ArrayList<Map>();
        rspResList.add(rspRes);
        inMap.put(KeyConst.RESOURCE_RSP, rspResList);
    }

    /**
     * 添加号码预约相关信息。
     * 
     * @param inMap
     */
    private void addNumOrderInfo(Map inMap) {
        List<Map> resLst = (List<Map>) inMap.get(KeyConst.RESOURCES_INFO);
        for (Map res : resLst) {
            res.put("SnChangeTag", "0"); // 号码不变更(ESS必传节点)
            String occupiedFlag = (String) res.get(KeyConst.OCCUPIED_FLAG);
            if ("2".equals(occupiedFlag) || "3".equals(occupiedFlag)) {
                res.put(KeyConst.PREORDER_TAG, "1"); // 开户时校验预约证件号码
            }
        }
    }

    /**
     * TransIDO一致性处理。
     * 
     * @param rspXml
     * @return
     */
    private String syncTransIDO(String rspXml, String lastTransIDO) {
        Matcher matcher = pattern.matcher(rspXml);
        return matcher.replaceFirst("<TransIDO>" + lastTransIDO + "</TransIDO>");
    }

}
