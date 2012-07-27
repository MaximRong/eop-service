package com.ailk.ess.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.phw.core.lang.Collections;
import org.phw.core.lang.ThreadSafeSimpleDateFormat;
import org.phw.eop.domain.EopSystemParam;
import org.phw.eop.mgr.EopParamMgr;
import org.phw.eop.support.EopAction;
import org.phw.eop.support.EopActionException;
import org.phw.eop.support.EopSystemParamAware;
import org.phw.eop.utils.Strings;
import org.phw.eop.utils.XmlUtils;
import org.phw.memcached.biz.NumberOccupyChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.base.KeyConst;
import com.ailk.base.NumConst;
import com.ailk.base.utils.FaceUtils;
import com.ailk.ecs.esf.base.utils.MapUtils;
import com.ailk.ess.g3ess.G3EssCmnAction;
import com.ailk.ess.n6ess.num.N6CheckNumAction;
import com.alibaba.fastjson.JSON;
import com.linkage.ecssales.bo.ESaleBOImpl;
import com.linkage.ecssales.interfaces.req.NumOrderRequest;
import com.linkage.ecssales.interfaces.req.NumPreOrderRequest;
import com.linkage.ecssales.interfaces.req.UNIBSSRequestHead;
import com.linkage.ecssales.interfaces.req.UniNumOrderReq;
import com.linkage.ecssales.interfaces.req.UniNumPreOrderReq;
import com.linkage.ecssales.interfaces.rsp.UniNumOrderRsp;
import com.linkage.ecssales.interfaces.rsp.UniNumPreOrderRsp;

/**
 * 号码状态变更统一访问入口。
 *
 * @author wanglei
 *
 * 2012-3-6
 */
public class CheckNumAccessible extends EopAction implements EopSystemParamAware {
    private static final Logger logger = LoggerFactory.getLogger(CheckNumAccessible.class);
    private EopSystemParam sysParam = null;
    private EopAction instance = null;

    @Override
    public Object doAction() throws EopActionException {
        // 号码预占次数校验
        /*boolean reachMaxOccupies = reachMaxOccupies();
        if (reachMaxOccupies) {
            // TODO：封装成响应报文格式
            throw new RuntimeException("号码预占达到最大次数");
        }*/
        // 初始化
        initInstance();
        // 调用ESS接口
        Map essMap = (Map) instance.doAction(getParams());
        return essMap;
        // 不再调用BSS
        /*Map bssMap = processNumOrder(essMap);
        return bssMap;*/
    }

    /**
     * 校验号码预占次数。
     * @return true 达到最大预占次数。
     */
    private boolean reachMaxOccupies() {
        String paramCode = sysParam.getEopApp().getAppid() + ".NumberOccupyCheck";
        boolean bool = EopParamMgr.getBool(paramCode, false);
        if (!bool) {
            return false;
        }
        Map reqBody = (Map) this.get(KeyConst.REQ_BODY);
        List<Map> resLst = (List<Map>) reqBody.get(KeyConst.RESOURCES_INFO);
        for (Map res : resLst) {
            String custId = (String) res.get(KeyConst.PROKEY);
            long number = Long.valueOf((String) res.get(KeyConst.RESOURCE_CODE));
            boolean reachMaxOccupies = NumberOccupyChecker.reachMaxOccupies(custId, number);
            if (reachMaxOccupies) {
                return true;
            }
        }
        return false;
    }

    /**
     * 初始化号码资源状态变更处理类。
     */
    private void initInstance() {
        Map reqBody = get(KeyConst.REQ_BODY);
        if (null == reqBody || Collections.isEmpty(reqBody)) {
            instance = new G3EssCmnAction();
            return;
        }
        String city = (String) reqBody.get(KeyConst.CITY);
        // 根据地市获取EssType及路由信息(N6)
        String paramKey = KeyConst.ESS_TYPE + getStr(KeyConst.PROVINCE) + "." + city;
        String essType = EopParamMgr.getStr(paramKey, "");
        // 地市获取不到,再根据省份获取EssType及路由信息(N6)
        if (StringUtils.isEmpty(essType)) {
            essType = EopParamMgr.getStr(KeyConst.ESS_TYPE + getStr(KeyConst.PROVINCE), NumConst.ESS_TYPE_3G);
        }
        logger.info("号码状态变更，省份编码=" + getStr(KeyConst.PROVINCE) + "，地市编码=" + city + "，EssType=" + essType);
        String[] essTypeArr = essType.split("-");
        if (NumConst.ESS_TYPE_N6.equalsIgnoreCase(essTypeArr[0])) {
            instance = new N6CheckNumAction();
            if (essTypeArr.length < 2) {
                logger.error("EOP平台EssType参数配置有误：未配置北六数据源信息。");
                return;
            }
            getParams().put(KeyConst.DS_NAME, essTypeArr[1]);
        }
        else {
            instance = new G3EssCmnAction();
        }
    }

    /**
     * 判断是否需要通知BSS。
     * @param essMap
     * @return
     */
    private Object parseEssRsp(Map essMap) {
        // 北六时无需通知BSS
        if (instance instanceof N6CheckNumAction) {
            return false;
        }

        // ESS响应成功时，才需通知BSS
        Map essRspBody = (Map) essMap.get(KeyConst.RSP_BODY);
        if (null == essRspBody) {
            logger.info("ESS侧未返回报文体");
            return false;
        }
        String essRespCode = (String) essRspBody.get(KeyConst.RESP_CODE);
        Object essResRsp = essRspBody.get(KeyConst.RESOURCE_RSP);
        if (!NumConst.RESP_CODE_SUCCESS.equals(essRespCode) || null == essResRsp) {
            logger.info("ESS侧响应编码错误或未返回ResourcesRsp信息");
            return false;
        }
        // 解析响应数据
        List<Map> essRspResLst = new ArrayList<Map>();
        if (essResRsp instanceof Map) {
            essRspResLst.add((Map) essResRsp);
        }
        else if (essResRsp instanceof List) {
            essRspResLst.addAll((List<Map>) essResRsp);
        }
        else {
            logger.warn("ESS侧响应报文ResourcesRsp节点异常");
            return false;
        }

        // ESS请求报文正常时，才通知BSS（极端情况）
        Map essReqBody = get(KeyConst.REQ_BODY);
        if (null == essReqBody) {
            logger.info("ESS侧请求报文缺失报文体");
            return false;
        }
        Object essResReq = essReqBody.get(KeyConst.RESOURCES_INFO);
        if (null == essResReq) {
            logger.info("ESS侧请求报文无ResourcesInfo信息");
            return false;
        }
        // 解析请求数据
        List<Map> essReqResLst = new ArrayList<Map>();
        if (essResReq instanceof Map) {
            essReqResLst.add((Map) essResReq);
        }
        else if (essResReq instanceof List) {
            essReqResLst.addAll((List<Map>) essResReq);
        }
        else {
            logger.warn("ESS侧请求报文ResourcesInfo节点异常");
            return false;
        }

        // 需通知BSS时带出解析所得数据
        return MapUtils.asMap("EssReqResLst", essReqResLst, "EssRspResLst", essRspResLst);
    }

    /**
     * 号码预约处理。
     * @param essMap
     * @return
     */
    private Map processNumOrder(Map essMap) {
        // 是否开放BSS预约功能（测试环境不开放）
        if (!NumConst.BSS_NUMORDER_OPEN) {
            logger.info("未开放BSS号码预约功能。");
            return essMap;
        }
        // 解析ESS响应
        Object result = parseEssRsp(essMap);
        // 无需通知BSS进行预约处理
        if (result instanceof Boolean && !(Boolean) result) {
            return essMap;
        }

        Map reqBody = get(KeyConst.REQ_BODY);
        String sysCode = (String) reqBody.get(KeyConst.SYSCODE);
        // 商城
        if (NumConst.SYSCODE_MALL.equals(sysCode)) {
            return processMallNumOrder(essMap, (Map) result);
        }
        // 通用
        return processNormalNumOrder(essMap, (Map) result);
    }

    /**
     * 处理商城号码预约。
     * @param essMap
     * @param result
     * @return
     */
    private Map processMallNumOrder(Map essMap, Map result) {
        logger.info("开始商城号码预约处理逻辑。");
        Map essReqBody = get(KeyConst.REQ_BODY);
        List<Map> essReqResLst = (List<Map>) result.get("EssReqResLst");
        Map essRspBody = (Map) essMap.get(KeyConst.RSP_BODY);
        List<Map> essRspResLst = (List<Map>) result.get("EssRspResLst");

        List<Map> bssResRsp = new ArrayList<Map>();
        for (Map reqRes : essReqResLst) {
            String preOrderTag = (String) reqRes.get(KeyConst.PREORDER_TAG);
            String reqNum = (String) reqRes.get(KeyConst.RESOURCE_CODE);
            // 当前号码响应信息
            Map currentRspRes = null;
            // 请求、响应号码匹配
            for (Map rspRes : essRspResLst) {
                String rspNum = (String) rspRes.get(KeyConst.RESOURCE_CODE);
                if (reqNum.equals(rspNum)) {
                    currentRspRes = rspRes;
                    break;
                }
            }
            if (null == currentRspRes) {
                logger.error("请求号码[" + reqNum + "]未找到对应的响应号码");
                // 未知该号码是否预占/预定成功，亦即无法强制释放，也无需带回ESS
                continue;
            }
            // 是否预约业务
            if (!"1".equals(preOrderTag)) {
                logger.info("请求号码[" + reqNum + "]非预约业务");
                bssResRsp.add(currentRspRes);
                continue;
            }

            // 操作类型
            String occupiedFlag = (String) reqRes.get(KeyConst.OCCUPIED_FLAG);
            if ("1".equals(occupiedFlag)) {
                logger.info("商城发起号码预约的预占请求，不予处理{}。", reqNum);
                bssResRsp.add(currentRspRes);
                continue;
            }
            else if ("2".equals(occupiedFlag) || "3".equals(occupiedFlag)) {
                logger.info("开始通知BSS进行号码预约（先预占再预定）处理{}", reqNum);
                // BSS预占
                notifyBssPreOrder(essReqBody, reqRes, currentRspRes);
                // BSS预占失败
                if (NumConst.RESP_CODE_FAIL.equals(currentRspRes.get(KeyConst.RSC_STATE_CODE))) {
                    bssResRsp.add(currentRspRes);
                    continue;
                }
                // BSS预约
                notifyBssOrder(essReqBody, reqRes, currentRspRes);
                bssResRsp.add(currentRspRes);
                continue;
            }
            else {
                logger.error("预约标记PreOrderTag与操作标识OccupiedFlag冲突{}", reqNum);
                bssResRsp.add(currentRspRes);
                continue;
            }
        }
        // 带回Bss处理结果
        essRspBody.put(KeyConst.RESOURCE_RSP, bssResRsp);
        essMap.put(KeyConst.RSP_BODY, essRspBody);
        addRspXml(essMap);
        return essMap;
    }

    /**
     * 处理通用场景下的号码预约。
     * @param essMap
     * @param result
     * @return
     */
    private Map processNormalNumOrder(Map essMap, Map result) {
        logger.info("开始通用号码预约处理逻辑。");
        Map essReqBody = get(KeyConst.REQ_BODY);
        List<Map> essReqResLst = (List<Map>) result.get("EssReqResLst");
        Map essRspBody = (Map) essMap.get(KeyConst.RSP_BODY);
        List<Map> essRspResLst = (List<Map>) result.get("EssRspResLst");

        List<Map> bssResRsp = new ArrayList<Map>();
        for (Map reqRes : essReqResLst) {
            String preOrderTag = (String) reqRes.get(KeyConst.PREORDER_TAG);
            String reqNum = (String) reqRes.get(KeyConst.RESOURCE_CODE);
            // 当前号码响应信息
            Map currentRspRes = null;
            // 请求、响应号码匹配
            for (Map rspRes : essRspResLst) {
                String rspNum = (String) rspRes.get(KeyConst.RESOURCE_CODE);
                if (reqNum.equals(rspNum)) {
                    currentRspRes = rspRes;
                    break;
                }
            }
            if (null == currentRspRes) {
                logger.error("请求号码{}未找到对应的响应号码", reqNum);
                // 未知该号码是否预占/预定成功，亦即无法强制释放，也无需带回ESS
                continue;
            }
            // 是否预约业务
            if (!"1".equals(preOrderTag)) {
                logger.info("请求号码{}非预约业务", reqNum);
                bssResRsp.add(currentRspRes);
                continue;
            }

            // 操作类型
            String occupiedFlag = (String) reqRes.get(KeyConst.OCCUPIED_FLAG);
            if ("1".equals(occupiedFlag)) {
                logger.info("开始通知BSS进行号码预约（预占）处理{}", reqNum);
                // BSS预占
                notifyBssPreOrder(essReqBody, reqRes, currentRspRes);
                bssResRsp.add(currentRspRes);
                continue;
            }
            else if ("2".equals(occupiedFlag) || "3".equals(occupiedFlag)) {
                logger.info("开始通知BSS进行号码预约（预定）处理{}", reqNum);
                // BSS预定
                notifyBssOrder(essReqBody, reqRes, currentRspRes);
                bssResRsp.add(currentRspRes);
                continue;
            }
            else {
                logger.error("预约标记PreOrderTag与操作标识OccupiedFlag冲突{}", reqNum);
                bssResRsp.add(currentRspRes);
                continue;
            }
        }
        // 带回Bss处理结果
        essRspBody.put(KeyConst.RESOURCE_RSP, bssResRsp);
        essMap.put(KeyConst.RSP_BODY, essRspBody);
        addRspXml(essMap);
        return essMap;
    }

    /**
     * 通知BSS进行号码预占。
     * @param essResRspMap
     * @return
     */
    private void notifyBssPreOrder(Map essReqBody, Map reqRes, Map rspRes) {
        // ESS已失败，直接返回
        if (!NumConst.RESP_CODE_SUCCESS.equals(rspRes.get(KeyConst.RSC_STATE_CODE))) {
            logger.info("ESS侧该号码预占已失败");
            return;
        }
        UniNumPreOrderReq numPreReq = new UniNumPreOrderReq();
        UNIBSSRequestHead bssReqHead = FaceUtils.buildBssReqHead(getParams(), NumConst.BSS_NUM_BIPCODE,
                NumConst.BSS_NUM_PRE_ACTCODE);
        numPreReq.setHead(bssReqHead);
        try {
            NumPreOrderRequest bssReqBody = new NumPreOrderRequest();
            bssReqBody.setSeqID(createSeqId(getStr(KeyConst.PROVINCE), (String) reqRes.get(KeyConst.PROKEY)));
            bssReqBody.setOrderChannel(NumConst.BSS_NUM_CHANNEL); // 网上营业厅
            bssReqBody.setNumID((String) reqRes.get(KeyConst.RESOURCE_CODE));
            bssReqBody.setProvCode((String) essReqBody.get(KeyConst.PROVINCE));
            bssReqBody.setCityCode((String) essReqBody.get(KeyConst.CITY));
            // 以下三节点不可为空
            String district = (String) essReqBody.get(KeyConst.DISTRICT);
            bssReqBody.setCountyCode(StringUtils.isEmpty(district) ? "无" : district);
            bssReqBody.setUserID("无");
            bssReqBody.setPara1("无");
            numPreReq.setRequest(bssReqBody);
            // 调用BSS
            ESaleBOImpl bo = new ESaleBOImpl();
            UniNumPreOrderRsp numPreRsp = bo.NumPreOrder(numPreReq);
            // 失败
            if (numPreRsp == null || numPreRsp.getResponse() == null
                    || !"00".equals(numPreRsp.getResponse().getRespCode())) {
                logger.error("BSS号码预占接口调用失败{}", reqRes.get(KeyConst.RESOURCE_CODE));
                // 回滚
                rollbackEss(reqRes);
                rspRes.put(KeyConst.RSC_STATE_CODE, NumConst.RESP_CODE_FAIL);
                rspRes.put(KeyConst.RSC_STATE_DESC, "BSS号码预占失败");
            }
        }
        catch (Exception e) {
            logger.error("BSS号码预占接口调用失败[" + reqRes.get(KeyConst.RESOURCE_CODE) + "]", e);
            // 回滚
            rollbackEss(reqRes);
            rspRes.put(KeyConst.RSC_STATE_CODE, NumConst.RESP_CODE_FAIL);
            rspRes.put(KeyConst.RSC_STATE_DESC, "BSS号码预占失败");
        }

    }

    /**
     * 通知BSS进行号码预定。
     * @return
     */
    private void notifyBssOrder(Map essReqBody, Map reqRes, Map rspRes) {
        // ESS已失败，直接返回
        if (!NumConst.RESP_CODE_SUCCESS.equals(rspRes.get(KeyConst.RSC_STATE_CODE))) {
            logger.info("ESS侧该号码预定已失败");
            return;
        }
        UniNumOrderReq numOrderReq = new UniNumOrderReq();
        UNIBSSRequestHead bssReqHead = FaceUtils.buildBssReqHead(getParams(), NumConst.BSS_NUM_BIPCODE,
                NumConst.BSS_NUM_ORDER_ACTCODE);
        numOrderReq.setHead(bssReqHead);
        try {
            NumOrderRequest bssReqBody = new NumOrderRequest();
            bssReqBody.setSeqID(createSeqId(getStr(KeyConst.PROVINCE), (String) reqRes.get(KeyConst.PROKEY)));
            Map essReqHead = (Map) get(KeyConst.REQ_HEAD);
            bssReqBody.setOrderId((String) essReqHead.get(KeyConst.TRANS_IDO));
            bssReqBody.setOrderChannel(NumConst.BSS_NUM_CHANNEL);
            bssReqBody.setNumID((String) reqRes.get(KeyConst.RESOURCE_CODE));
            bssReqBody.setProductID((String) reqRes.get(KeyConst.PRODUCT_ID));
            bssReqBody.setProvCode((String) essReqBody.get(KeyConst.PROVINCE));
            bssReqBody.setCityCode((String) essReqBody.get(KeyConst.CITY));

            // 以下五节点不可为空
            String district = (String) essReqBody.get(KeyConst.DISTRICT);
            bssReqBody.setCountyCode(StringUtils.isEmpty(district) ? "无" : district);
            // 业务上不允许该节点为空
            bssReqBody.setUserID((String) reqRes.get(KeyConst.CERT_NUM));
            String custName = (String) reqRes.get(KeyConst.CUST_NAME);
            bssReqBody.setUserName(StringUtils.isEmpty(custName) ? "无" : custName);
            String contactNum = (String) reqRes.get(KeyConst.CONTACT_NUM);
            bssReqBody.setContactNum(StringUtils.isEmpty(contactNum) ? "无" : contactNum);
            bssReqBody.setPara1("无");

            // 暂只支持18位身份证
            bssReqBody.setIDType("02");

            numOrderReq.setRequest(bssReqBody);
            // 调用BSS
            ESaleBOImpl bo = new ESaleBOImpl();
            UniNumOrderRsp numOrderRsp = bo.NumOrder(numOrderReq);
            // 失败
            if (numOrderRsp == null || numOrderRsp.getResponse() == null
                    || !"00".equals(numOrderRsp.getResponse().getRespCode())) {
                logger.error("BSS号码预定接口调用失败{}", reqRes.get(KeyConst.RESOURCE_CODE));
                // 回滚
                rollbackEss(reqRes);
                rspRes.put(KeyConst.RSC_STATE_CODE, NumConst.RESP_CODE_FAIL);
                rspRes.put(KeyConst.RSC_STATE_DESC, "BSS号码预占失败");
            }
        }
        catch (Exception e) {
            logger.error("BSS号码预定接口调用失败[" + reqRes.get(KeyConst.RESOURCE_CODE) + "]", e);
            // 回滚
            rollbackEss(reqRes);
            rspRes.put(KeyConst.RSC_STATE_CODE, NumConst.RESP_CODE_FAIL);
            rspRes.put(KeyConst.RSC_STATE_DESC, "BSS号码预占失败");
        }
    }

    /**
     * 通知BSS进行号码预占失败时，需通知ESS进行号码释放。
     * @param reqRes
     */
    private void rollbackEss(Map reqRes) {
        try {
            Map essReqMap = new HashMap();
            Map parmas = getParams();
            essReqMap.put(KeyConst.PROVINCE, parmas.get(KeyConst.PROVINCE));
            essReqMap.put(KeyConst.HEAD_ROOT, parmas.get(KeyConst.HEAD_ROOT));
            essReqMap.put(KeyConst.REQ_HEAD, parmas.get(KeyConst.REQ_HEAD));
            essReqMap.put(KeyConst.BODY_ROOT, parmas.get(KeyConst.BODY_ROOT));

            // 重组请求报文体
            Map reqBody = (Map) parmas.get(KeyConst.REQ_BODY);
            reqBody.remove(KeyConst.RESOURCES_INFO);
            reqBody.remove(KeyConst.PREORDER_TAG);
            reqBody.remove(KeyConst.REMARK);
            // 修改操作标识为4释放
            reqRes.put(KeyConst.OCCUPIED_FLAG, NumConst.OCCUPY_FLAG_RELEASE);
            reqBody.put(KeyConst.RESOURCES_INFO, reqRes);
            essReqMap.put(KeyConst.REQ_BODY, reqBody);
            // 调用ESS号码资源释放接口
            instance.doAction(essReqMap);
        }
        catch (Exception e) {
            logger.error("BSS侧号码预约失败后，调用ESS接口立即释放资源亦失败[" + reqRes.get(KeyConst.RESOURCE_CODE) + "]", e);
        }
    }

    /**
     * 生成序列号。
     * @param province
     * @return
     */
    private String createSeqId(String province, String prokey) {
        StringBuffer sb = new StringBuffer(NumConst.BSS_NUM_SEQ_PREFIX);
        sb.append(province);
        ThreadSafeSimpleDateFormat format = new ThreadSafeSimpleDateFormat("yyMMddHH");
        sb.append(format.format(new Date())).append("0000"); // 保证预占预定一致

        if (StringUtils.isEmpty(prokey)) {
            logger.warn("请求报文号码预占关键字为空");
            return sb.append("0000").toString();
        }
        int pad = prokey.length() - 4;
        if (pad < 0) {
            for (int i = pad; i < 0; i++) {
                sb.append("0");
            }
            return sb.append(prokey).toString();
        }
        return sb.append(prokey.substring(0, 4)).toString();
    }

    /**
     * 重新构建响应报文。
     * @param essMap
     */
    private void addRspXml(Map essMap) {
        Map rspHead = (Map) essMap.get(KeyConst.RSP_HEAD);
        // remove last svc_cont
        rspHead.remove(KeyConst.SVC_CONT);
        rspHead.put(KeyConst.SVC_CONT, "");

        Map rspBody = (Map) essMap.get(KeyConst.RSP_BODY);
        String headXml = XmlUtils.jsonToXml(JSON.toJSONString(rspHead), "UniBSS");
        String bodyXml = XmlUtils.jsonToXml(JSON.toJSONString(rspBody), "CheckNumRsp");
        headXml = Strings.replaceOnce(headXml, "<SvcCont>", "<SvcCont><![CDATA[");
        String rspXml = Strings.replaceOnce(headXml, "</SvcCont>", bodyXml + "]]></SvcCont>");
        essMap.put(KeyConst.RSP_XML, rspXml);
    }

    @Override
    public void setSystemParam(EopSystemParam sysParam) {
        this.sysParam = sysParam;
    }

}
