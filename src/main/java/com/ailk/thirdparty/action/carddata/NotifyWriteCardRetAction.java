package com.ailk.thirdparty.action.carddata;

import java.util.HashMap;
import java.util.Map;

import org.phw.eop.api.bean.writecard.WriteCardReq;
import org.phw.eop.api.bean.writecard.WriteCardRsp;
import org.phw.eop.support.EopAction;
import org.phw.eop.support.EopActionException;

import com.ailk.base.KeyConst;
import com.ailk.base.NumConst;
import com.ailk.base.utils.FaceUtils;
import com.ailk.thirdparty.utils.EssEopCaller;
import com.ailk.thirdparty.utils.ReqProcessor;

/**
 * 写卡结果通知Action。
 *
 * @author wanglei
 *
 * 2012-2-21
 */
public class NotifyWriteCardRetAction extends EopAction {

    public static final String REQ_TEMPLATE = "com/ailk/face/thirdparty/carddata/NotifyWriteCardRet_Req.xml";
    public static final String RSP_TEMPLATE = "com/ailk/face/thirdparty/carddata/NotifyWriteCardRet_Rsp.xml";

    @Override
    public Object doAction() throws EopActionException {
        Map inMap = new HashMap();
        try {
            // 校验请求报文
            inMap = ReqProcessor.checkReqXml(getStr(KeyConst.REQ_XML), REQ_TEMPLATE);
            // 转换EopBean
            String routeValue = (String) inMap.get(KeyConst.ROUTE_VALUE);
            WriteCardReq req = new WriteCardReq();
            ReqProcessor.parse2EopBean(req, getStr(KeyConst.REQ_XML), routeValue);
            // 调用Ess-Eop
            WriteCardRsp rsp = EssEopCaller.doCall(req);

            if (NumConst.EOP_RSP_OK.equals(rsp.getRspcode())) {
                // TODO:rsp.getRspXml()
                return rsp.getRspmsg();
            }
            throw new Exception(rsp.getRspdesc());
        }
        catch (Throwable t) {
            inMap.put(KeyConst.RESP_CODE, NumConst.RESP_CODE_FAIL);
            inMap.put(KeyConst.RESP_DESC, t.getMessage());
            return FaceUtils.createRspXml(inMap, RSP_TEMPLATE);
        }
    }

}
