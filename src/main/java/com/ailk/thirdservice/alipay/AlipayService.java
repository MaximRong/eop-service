package com.ailk.thirdservice.alipay;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.phw.ibatis.engine.PDao;
import org.phw.ibatis.util.PDaoEngines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlipayService {
    private static final Logger logger = LoggerFactory.getLogger(AlipayService.class);
    public final static String ALIPAY_SRV_PATH = AlipayService.class.getCanonicalName() + ".";
    private static final String SQL = "com/ailk/sql/alipay/AlipayAccessSQL.xml";
    private PDao dao = PDaoEngines.getDao(SQL, "EcsStore");
    private static final int BATCH_SIZE = 100;

    /**
     * 帐务明细入库。
     * @param inMap
     * @return
     */
    public void recordTradeReport(Map inMap) {
        // 获取日志标识
        String logId = createLogId();
        inMap.put("log_id", logId);

        // 记录接口日志
        String csvData = (String) inMap.get("csv_data");
        try {
            inMap.put("csv_data", csvData.getBytes("GBK"));
        }
        catch (UnsupportedEncodingException e) {
            logger.error("CsvData Use GBK GetBytes Error, Try Without Charset!", e);
            inMap.put("csv_data", csvData.getBytes());
        }
        dao.insert("AlipayAccess.RecordLog", inMap);

        // 帐务明细
        String checkCode = (String) inMap.get("CheckCode");
        List<TradeDetailBean> csvList = (List<TradeDetailBean>) inMap.get("CsvList");
        if ("T".equals(checkCode) && csvList.size() > 0) {
            List<TradeDetailBean> tempList = new ArrayList<TradeDetailBean>();
            for (TradeDetailBean detail : csvList) {
                int count = dao.selectInteger("AlipayAccess.QueryTradeReportExists", detail);
                if (count < 1) {
                    tempList.add(detail);
                }
            }
            int index = 0;
            for (TradeDetailBean detail : tempList) {
                if (index == 0) {
                    dao.startBatch();
                }
                ++index;
                detail.setLogId(logId);
                dao.insert("AlipayAccess.RecordTradeReport", detail);
                if (index == BATCH_SIZE) {
                    dao.executeBatch();
                    index = 0;
                }
            }
            if (index > 0) {
                dao.executeBatch();
            }
        }
    }

    /**
     * 生成日志标识。
     * @return
     */
    private String createLogId() {
        try {
            return dao.selectString("AlipayAccess.GetAlipayLogId", null);
        }
        catch (Exception e) {
            logger.error("生成支付宝日志标识失败", e);
            throw new RuntimeException("生成支付宝日志标识失败");
        }
    }

    public static void main(String[] args) {
        //String s = "a,,b,c,, ";
        //System.out.println(s.split(",").length);
        //System.out.println((" " + s.trim()).split(",").length);
        //List csvList = Arrays.asList(s.split(","));
        //System.out.println(csvList.size());
        String s = "转账";
        System.out.println(s.contains("转账"));
    }

}
