package com.ailk.ess.common;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.phw.core.lang.Collections;
import org.phw.eop.mgr.EopParamMgr;
import org.phw.eop.support.EopAction;
import org.phw.eop.support.EopActionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.base.KeyConst;
import com.ailk.base.NumConst;
import com.ailk.ess.g3ess.G3EssCmnAction;
import com.ailk.ess.n6ess.cust.N6CheckCustAction;

/**
 * 客户资料校验统一访问入口。
 *
 * @author wanglei
 *
 * 2012-3-31
 */
public class CheckCustAccessible extends EopAction {
    private static final Logger logger = LoggerFactory.getLogger(CheckCustAccessible.class);
    private EopAction instance = null;

    @Override
    public Object doAction() throws EopActionException {
        initInstance();
        return instance.doAction(getParams());
    }

    /**
     * 初始化客户资料校验处理类。
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
            instance = new N6CheckCustAction();
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

}
