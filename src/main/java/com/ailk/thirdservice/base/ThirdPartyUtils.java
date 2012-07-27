package com.ailk.thirdservice.base;

import java.util.HashMap;
import java.util.Map;

import org.phw.core.exception.BusinessException;

/**
 * 第三方接入工具类。
 * 
 * @author wanglei 2012-3-8
 */
public class ThirdPartyUtils {

    /**
     * 构建响应信息。
     * 
     * @param e
     * @return
     */
    public static Map buildRspMsg(Exception e) {
        if (e instanceof BusinessException) {
            return buildRspMsg(((BusinessException)e).getMessageCode(), e.getMessage());

        } else {
            return buildRspMsg(NumConstants.RESP_CODE_FAIL, e.getMessage());
        }
    }

    /**
     * 构建响应信息。
     * 
     * @param e
     * @return
     */
    public static Map buildRspMsg(String respCode, String respDesc) {
        Map retMap = new HashMap();
        retMap.put(KeyConstants.RESP_CODE, respCode);
        retMap.put(KeyConstants.RESP_DESC, respDesc);
        return retMap;
    }

}
