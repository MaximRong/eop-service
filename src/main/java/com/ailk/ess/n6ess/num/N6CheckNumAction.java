package com.ailk.ess.n6ess.num;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.phw.core.lang.Collections;
import org.phw.eop.support.EopAction;
import org.phw.eop.support.EopActionException;
import org.phw.ibatis.engine.PDao;
import org.phw.ibatis.util.PDaoEngines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.base.KeyConst;
import com.ailk.base.NumConst;
import com.ailk.base.utils.FaceUtils;
import com.ailk.ecs.esf.base.utils.StrUtils;
import com.ailk.ess.n6ess.utils.N6FaceUtils;

/**
 * 北六号码资源状态变更。
 *
 * @author wanglei
 *
 * 2012-3-7
 */
public class N6CheckNumAction extends EopAction {
    private static final Logger logger = LoggerFactory.getLogger(N6CheckNumAction.class);
    private static final String RSP_TEMPLATE = "com/ailk/face/g3ess/num/CheckNum_Rsp.xml";
    // 号码省份DAO
    private static final String NUM_P_SQL = "com/ailk/sql/NumProvinceSQL.xml";
    private PDao numPDao = null;

    @Override
    public Object doAction() throws EopActionException {
        logger.info("原N6ESS号码状态变更请求参数：" + getParams());
        try {
            // 初始化号码数据源
            initNumDataSource();
            // 循环处理号码列表
            List resRspLst = iterateNumCheck();
            return buildRspMsg(resRspLst);
        }
        catch (Exception e) {
            logger.error("N6ESS号码状态变更失败", e);
            Map retMap = N6FaceUtils.buildRspMsg(e);
            return buildRspMsg(retMap);
        }
    }

    /**
     * 循环处理接口号码信息。
     * @return
     */
    private List iterateNumCheck() {
        Map reqBody = get(KeyConst.REQ_BODY);
        Object resObj = reqBody.get(KeyConst.RESOURCES_INFO);
        if (resObj == null) {
            logger.error("N6ESS号码状态变更请求报文缺失ResourcesInfo节点");
            throw new RuntimeException("N6ESS号码状态变更请求报文缺失ResourcesInfo节点");
        }
        List<Map> resLst = new ArrayList<Map>();
        if (resObj instanceof List) {
            if (Collections.isEmpty((List) resObj)) {
                logger.error("N6ESS号码状态变更请求报文缺失ResourcesInfo节点");
                throw new RuntimeException("N6ESS号码状态变更请求报文缺失ResourcesInfo节点");
            }
            resLst.addAll((List<Map>) resObj);
        }
        else if (resObj instanceof Map) {
            resLst.add((Map) resObj);
        }
        if (Collections.isEmpty(resLst)) {
            logger.error("N6ESS号码状态变更请求报文ResourcesInfo节点为空");
            throw new RuntimeException("N6ESS号码状态变更请求报文ResourcesInfo节点为空");
        }

        // 响应资源列表
        List<Map> rspLst = new ArrayList<Map>();
        for (Map res : resLst) {
            try {
                IN6NumProcessor processor = getNumProcessor(res);
                if (processor == null) {
                    throw new RuntimeException("OccupiedFlag节点异常" + res.get(KeyConst.RESOURCE_CODE));
                }
                // 获取北六号码归属信息
                Map numInfo = getNumberInfo(res);
                // 重构参数
                Map reparams = rebuildParams(numInfo, res);
                Map retMap = processor.process(reparams);
                retMap.put(KeyConst.RESOURCE_CODE, res.get(KeyConst.RESOURCE_CODE));
                rspLst.add(retMap);
            }
            catch (Exception e) {
                Map retMap = new HashMap();
                retMap.put(KeyConst.RESP_CODE, NumConst.RESP_CODE_FAIL);
                retMap.put(KeyConst.RESP_DESC, e.getMessage());
                rspLst.add(retMap);
            }
        }
        return rspLst;
    }

    /**
     * 获取号码接口处理类。
     * @return
     */
    private IN6NumProcessor getNumProcessor(Map res) {
        String occupyFlag = (String) res.get(KeyConst.OCCUPIED_FLAG);
        if (NumConst.OCCUPY_FLAG_NO.equals(occupyFlag) || NumConst.OCCUPY_FLAG_CHG.equals(occupyFlag)) {
            logger.error("N6ESS号码状态变更不支持不预占操作0、变更号码操作5");
            return null;
        }
        else if (NumConst.OCCUPY_FLAG_YES.equals(occupyFlag)) {
            logger.info("N6ESS号码状态变更：预占1");
            return new N6PreOccupyNumProcessor();
        }
        else if (NumConst.OCCUPY_FLAG_UNPAY.equals(occupyFlag) || NumConst.OCCUPY_FLAG_PAIED.equals(occupyFlag)) {
            logger.info("N6ESS号码状态变更：未付费预定2/付费预定3");
            return new N6OrderNumProcessor();
        }
        else if (NumConst.OCCUPY_FLAG_RELEASE.equals(occupyFlag)) {
            logger.info("N6ESS号码状态变更：释放号码4");
            return new N6DelOrderNumProcessor();
        }
        logger.error("N6ESS号码状态变更请求报文OccupiedFlag节点传值异常");
        return null;
    }

    /**
     * 构建（错误）响应信息。
     * @param retMap
     * @return
     */
    private Map buildRspMsg(Map retMap) {
        Map inMap = this.get(KeyConst.REQ_HEAD);

        inMap.put(KeyConst.RESP_CODE, retMap.get(KeyConst.RESP_CODE));
        String respDesc = (String) retMap.get(KeyConst.RESP_DESC);
        inMap.put(KeyConst.RESP_DESC, StringUtils.isNotEmpty(respDesc) ? StrUtils.substrb(respDesc, 100) : respDesc);

        Map reqBody = this.get(KeyConst.REQ_BODY);
        List resList = (List) reqBody.get(KeyConst.RESOURCES_INFO);
        if (Collections.isEmpty(resList)) {
            fillDefaultResourcesRsp(inMap);
        }
        else {
            Map resMap = (Map) resList.get(0);
            fillResourcesRsp(inMap, (String) resMap.get(KeyConst.RESOURCE_CODE));
        }

        String rspXml = FaceUtils.createRspXml(inMap, RSP_TEMPLATE);
        logger.info("N6ESS号码状态变更响应报文：" + rspXml);
        return FaceUtils.parseRspXml2EssMap(rspXml);
    }

    /**
     * 构建（业务成功）响应报文。
     * @param resRspLst
     * @return
     */
    private Map buildRspMsg(List resRspLst) {
        Map inMap = this.get(KeyConst.REQ_HEAD);
        inMap.put(KeyConst.RESP_CODE, NumConst.RESP_CODE_SUCCESS);
        inMap.put(KeyConst.RESP_DESC, "成功");

        if (Collections.isEmpty(resRspLst)) {
            fillDefaultResourcesRsp(inMap);
        }
        else {
            fillResourcesRsp(inMap, resRspLst);
        }

        String rspXml = FaceUtils.createRspXml(inMap, RSP_TEMPLATE);
        logger.info("N6ESS号码状态变更响应报文：" + rspXml);
        return FaceUtils.parseRspXml2EssMap(rspXml);
    }

    /**
     * 填充ResourcesRsp节点缺省信息。
     * @param inMap
     */
    private void fillDefaultResourcesRsp(Map inMap) {
        Map rspRes = new HashMap();
        rspRes.put(KeyConst.RESOURCE_TYPE, NumConst.RESOURCE_TYPE_SERIAL);
        rspRes.put(KeyConst.RESOURCE_CODE, "null");
        rspRes.put(KeyConst.RSC_STATE_CODE, NumConst.RESP_CODE_FAIL);
        rspRes.put(KeyConst.RSC_STATE_DESC, inMap.get(KeyConst.RESP_DESC));
        rspRes.put(KeyConst.NUM_ID, "null");
        List<Map> rspResList = new ArrayList<Map>();
        rspResList.add(rspRes);
        inMap.put(KeyConst.RESOURCE_RSP, rspResList);
    }

    /**
     * 填充ResourcesRsp节点信息。
     * @param inMap
     */
    private void fillResourcesRsp(Map inMap, String resouceCode) {
        Map rspRes = new HashMap();
        rspRes.put(KeyConst.RESOURCE_TYPE, NumConst.RESOURCE_TYPE_SERIAL);
        rspRes.put(KeyConst.RESOURCE_CODE, resouceCode);
        rspRes.put(KeyConst.RSC_STATE_CODE, inMap.get(KeyConst.RESP_CODE));
        rspRes.put(KeyConst.RSC_STATE_DESC, inMap.get(KeyConst.RESP_DESC));
        rspRes.put(KeyConst.NUM_ID, resouceCode);
        List<Map> rspResList = new ArrayList<Map>();
        rspResList.add(rspRes);
        inMap.put(KeyConst.RESOURCE_RSP, rspResList);
    }

    /**
     * 填充ResourcesRsp节点信息。
     * @param resRspLst
     */
    private void fillResourcesRsp(Map inMap, List<Map> resRspLst) {
        List<Map> retLst = new ArrayList<Map>();
        for (Map res : resRspLst) {
            Map rspRes = new HashMap();
            rspRes.put(KeyConst.RESOURCE_TYPE, NumConst.RESOURCE_TYPE_SERIAL);
            rspRes.put(KeyConst.RESOURCE_CODE, res.get(KeyConst.RESOURCE_CODE));
            rspRes.put(KeyConst.RSC_STATE_CODE, res.get(KeyConst.RESP_CODE));
            rspRes.put(KeyConst.RSC_STATE_DESC, res.get(KeyConst.RESP_DESC));
            rspRes.put(KeyConst.NUM_ID, res.get(KeyConst.RESOURCE_CODE));
            retLst.add(rspRes);
        }
        inMap.put(KeyConst.RESOURCE_RSP, retLst);
    }

    /**
     * 初始化号码数据源信息。
     */
    private void initNumDataSource() {
        String dsName = getStr(KeyConst.DS_NAME);
        numPDao = PDaoEngines.getDao(NUM_P_SQL, dsName);
        if (numPDao == null) {
            logger.error("根据DsName未查找到北六的数据源[" + dsName + "]");
            throw new RuntimeException("根据DsName未查找到北六的数据源[" + dsName + "]");
        }
    }

    /**
     * 获取号码信息。
     * @return
     */
    private Map getNumberInfo(Map res) {
        Map reqBody = get(KeyConst.REQ_BODY);
        // 查询号码信息
        String serialNumber = (String) res.get(KeyConst.RESOURCE_CODE);
        if (StringUtils.isEmpty(serialNumber)) {
            logger.error("请求报文资源信息中的手机号码为空");
            throw new RuntimeException("请求报文资源信息中的手机号码为空");
        }
        Map numInfo = numPDao.selectMap("NumProvince.getN6NumberInfo", serialNumber);
        if (Collections.isEmpty(numInfo)) {
            logger.error("未查询到号码信息{}", serialNumber);
            Map temp = numPDao.selectMap("NumProvince.getThirdPartyN6CityCode", serialNumber);
            numInfo = Collections
                    .asMap(KeyConst.DISTRICT, Collections.isEmpty(temp) ? "" : temp.get(KeyConst.DISTRICT));
        }
        String numProv = (String) numInfo.get(KeyConst.PROVINCE);
        if (StringUtils.isEmpty(numProv)) {
            logger.warn("查询到的号码归属省份信息为空{}", serialNumber);
            numProv = getStr(KeyConst.PROVINCE);
        }
        /*String numCity = (String) numInfo.get(KeyConst.CITY);
        if (StringUtils.isEmpty(numCity)) {
            logger.warn("查询到的号码归属地市信息为空[" + serialNumber + "]");
            numCity = (String) reqBody.get(KeyConst.CITY);
        }*/
        String numDistrict = (String) numInfo.get(KeyConst.DISTRICT);
        if (StringUtils.isEmpty(numDistrict)) {
            logger.warn("查询到的号码归属业务区信息为空[" + serialNumber + "]");
            numDistrict = (String) reqBody.get(KeyConst.DISTRICT);
        }
        String numChannel = (String) numInfo.get(KeyConst.CHANNEL_ID);
        if (StringUtils.isEmpty(numChannel)) {
            logger.warn("查询到的号码归属渠道信息为空[" + serialNumber + "]");
            numChannel = (String) reqBody.get(KeyConst.CHANNEL_ID);
        }

        numInfo.put(KeyConst.PROVINCE, numProv);
        //numInfo.put(KeyConst.CITY, numCity);
        numInfo.put(KeyConst.DISTRICT, numDistrict);
        numInfo.put(KeyConst.CHANNEL_ID, numChannel);
        return numInfo;
    }

    /**
     * 重新拼装参数信息。
     * @param numInfo
     * @return
     */
    private Map rebuildParams(Map numInfo, Map res) {
        Map reparams = getParams();
        reparams.put(KeyConst.PROVINCE, numInfo.get(KeyConst.PROVINCE));
        Map reqBody = (Map) reparams.get(KeyConst.REQ_BODY);
        reqBody.put(KeyConst.PROVINCE, numInfo.get(KeyConst.PROVINCE));
        //reqBody.put(KeyConst.CITY, numInfo.get(KeyConst.CITY));
        reqBody.put(KeyConst.DISTRICT, numInfo.get(KeyConst.DISTRICT));
        reqBody.put(KeyConst.CHANNEL_ID, numInfo.get(KeyConst.CHANNEL_ID));
        List resLst = new ArrayList();
        resLst.add(res);
        reqBody.put(KeyConst.RESOURCES_INFO, resLst);
        reparams.put(KeyConst.REQ_BODY, reqBody);
        return reparams;
    }
}
