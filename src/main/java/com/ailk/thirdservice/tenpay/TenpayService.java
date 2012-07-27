package com.ailk.thirdservice.tenpay;

import java.util.Map;

import org.phw.ibatis.engine.PDao;
import org.phw.ibatis.util.PDaoEngines;

public class TenpayService {
    public final static String  TENPAY_SRV_PATH   = TenpayService.class.getCanonicalName() + ".";
    public final static String  RECORD_REFUND_LOG = TENPAY_SRV_PATH + "recordRefundLog";
    private static final String SQL               = "com/ailk/face/thirdservice/tenpay/TenpayRefundSQL.xml";
    private PDao                dao               = PDaoEngines.getDao(SQL, "EcsStore");

    /**
     * 记录财付通退款日志
     * 
     * @param inMap
     * @return
     */
    public void recordRefundLog(Map inMap) {
        // 记录接口日志
        dao.insert("Tenpay.RecordRefundLog", inMap);

    }

}
