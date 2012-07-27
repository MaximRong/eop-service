package com.ailk.thirdparty.action.carddata;

import java.util.HashMap;
import java.util.Map;

import org.phw.eop.api.bean.qrycarddata.GetDataReq;
import org.phw.eop.api.bean.qrycarddata.GetDataRsp;
import org.phw.eop.support.EopAction;
import org.phw.eop.support.EopActionException;

import com.ailk.base.KeyConst;
import com.ailk.base.NumConst;
import com.ailk.base.utils.FaceUtils;
import com.ailk.thirdparty.utils.EssEopCaller;
import com.ailk.thirdparty.utils.ReqProcessor;

/**
 * 写卡数据查询Action。
 *
 * @author wanglei
 *
 * 2012-2-21
 */
public class QueryCardDataAction extends EopAction {

    public static final String REQ_TEMPLATE = "com/ailk/face/thirdparty/carddata/QueryCardData_Req.xml";
    public static final String RSP_TEMPLATE = "com/ailk/face/thirdparty/carddata/QueryCardData_Rsp.xml";

    @Override
    public Object doAction() throws EopActionException {
        Map inMap = new HashMap();
        try {
            // 校验请求报文
            inMap = ReqProcessor.checkReqXml(getStr(KeyConst.REQ_XML), REQ_TEMPLATE);
            // 转换EopBean
            String routeValue = (String) inMap.get(KeyConst.ROUTE_VALUE);
            GetDataReq req = new GetDataReq();
            ReqProcessor.parse2EopBean(req, getStr(KeyConst.REQ_XML), routeValue);
            // 调用Ess-Eop
            GetDataRsp rsp = EssEopCaller.doCall(req);

            if (NumConst.EOP_RSP_OK.equals(rsp.getRspcode())) {
                // TODO：rsp.getRspXml()
                return rsp.getRspmsg();
            }
            throw new Exception(rsp.getRspdesc());
        }
        catch (Throwable t) {
            inMap.put(KeyConst.RESP_CODE, NumConst.RESP_CODE_FAIL);
            inMap.put(KeyConst.RESP_DESC, t.getMessage());
            return buildFailRspMsg(inMap);
        }
    }

    /**
     * 构建返回失败报文。
     * @param inMap
     * @return
     */
    private String buildFailRspMsg(Map inMap) {
        // 响应报文非空节点
        fillNotEmptyRspNode(inMap);
        // 构建响应报文
        return FaceUtils.createRspXml(inMap, RSP_TEMPLATE);
    }

    /**
     * 填充返回报文非空节点。
     */
    private void fillNotEmptyRspNode(Map inMap) {
        // 大卡卡号
        inMap.put(KeyConst.ICCID, "");
        // 读卡序列
        inMap.put(KeyConst.PROC_ID, "");
        // 交易流水（ESS生成返回）
        inMap.put(KeyConst.ACTIVE_ID, "");
        // 制卡脚本
        inMap.put(KeyConst.SCRIPT_SEQ, "");
    }
}
