package com.ailk.thirdservice.taobao.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.phw.ibatis.engine.PDao;
import org.phw.ibatis.util.PDaoEngines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogDbUnit {

    private static final Logger logger = LoggerFactory.getLogger(LogDbUnit.class);
    private static final String SQL = "com/ailk/sql/taobao/TaobaoAccessSQL.xml";
    private static PDao dao = PDaoEngines.getDao(SQL, "EcsStore");

    /**
     * 记录日志。
     * @param logtype
     * @param begin
     * @param end
     * @param status
     * @param all
     * @param succ
     * @param fail
     * @param al
     * @param page_num
     */
    public static String setRecord(String logtype, String begin, String end, String status, Long all, Long succ,
            Long fail, List al, Long page_num) {

        al = al == null ? new ArrayList() : al;

        String logId = createLogId();

        Map params = new HashMap();
        params.put("logid", logId);
        params.put("logtype", logtype);
        params.put("begin", begin);
        params.put("end", end);
        params.put("status", status);
        params.put("page_num", page_num);
        params.put("all", all);
        params.put("succ", succ);
        params.put("fail", fail);
        params.put("invalid", all - succ - fail);

        boolean transactionStart = false;
        try {
            transactionStart = dao.tryStart();
            dao.insert("TaobaoAccess.RecordLog", params);
            for (LogBean logBean : (ArrayList<LogBean>) al) {
                params.put("sub_logid", logId);
                params.put("tid", logBean.getTid());
                params.put("order_status", logBean.getOrder_status());
                params.put("status", logBean.getStatus());
                params.put("logdesc", logBean.getDesc());
                dao.insert("TaobaoAccess.RecordSubLog", params);
            }
            dao.commit(transactionStart);
        }
        finally {
            dao.end(transactionStart);
        }

        return logId;
    }

    /**
     * 记录日志，暂未使用。
     * @param logtype
     * @param begin
     * @param end
     * @param status
     * @param all
     * @param succ
     * @param fail
     * @param al
     * @param page_num
     */
    public static String setRecord(String logtype, String begin, String end, String status, int all, int succ,
            int fail, ArrayList al, int page_num) {
        return setRecord(logtype, begin, end, status, new Integer(all).longValue(), new Integer(succ).longValue(),
                new Integer(fail).longValue(), al, new Integer(succ).longValue());
    }

    /**
     * 生成淘宝日志标识。
     * @return
     */
    private static String createLogId() {
        try {
            return dao.selectString("TaobaoAccess.GetTaobaoLogId", null);
        }
        catch (Exception e) {
            logger.error("生成淘宝日志标识失败", e);
            throw new RuntimeException("生成淘宝日志标识失败");
        }
    }

}
