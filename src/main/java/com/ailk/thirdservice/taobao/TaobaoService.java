package com.ailk.thirdservice.taobao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.phw.core.lang.Collections;
import org.phw.core.lang.Dates;
import org.phw.eop.utils.Strings;
import org.phw.ibatis.engine.PDao;
import org.phw.ibatis.util.PDaoEngines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.base.utils.msg.MallNtfplatService;
import com.ailk.thirdservice.taobao.log.LogDbUnit;

public class TaobaoService {
    private static final Logger logger = LoggerFactory.getLogger(TaobaoService.class);
    public final static String TAOBAO_SRV_PATH = TaobaoService.class.getCanonicalName() + ".";
    private static final int BATCH_SIZE = 100;
    private static final String EXORDER_CATECODE = "0010";
    public final static String PARSE2_TAOBAO_LGTS = TAOBAO_SRV_PATH + "parse2TaobaoLgts";
    public final static String REFRESH_SENDSTATUS = TAOBAO_SRV_PATH + "refreshSendStatus";
    public final static String RECORD_SYNCEX_ORDER = TAOBAO_SRV_PATH + "recordSyncExOrder";
    public final static String LOG = TAOBAO_SRV_PATH + "log";
    private static final String SQL = "com/ailk/sql/taobao/TaobaoAccessSQL.xml";
    private PDao dao = PDaoEngines.getDao(SQL, "EcsStore");

    /**
     * 网厅订单入库。
     * @param inMap
     * @return
     */
    public String recordOrder(Map inMap) {
        dao.update("TaobaoAccess.RecordOrder", inMap);
        return (String) inMap.get("p_retCode");
    }

    /**
     * 上网卡订单入库。
     * @param inMap
     * @return
     */
    public String recordSwkOrder(Map inMap) {
        dao.update("TaobaoAccess.RecordSwkOrder", inMap);
        return (String) inMap.get("p_retCode");
    }

    /**
     * 网厅退款入库。
     * @param inMap
     * @return
     */
    public String recordRefund(Map inMap) {
        dao.update("TaobaoAccess.RecordRefund", inMap);
        return (String) inMap.get("p_retCode");
    }

    /**
     * 上网卡退款入库。
     * @param inMap
     * @return
     */
    public String recordSwkRefund(Map inMap) {
        dao.update("TaobaoAccess.RecordSwkRefund", inMap);
        return (String) inMap.get("p_retCode");
    }

    /**
     * 日志入库。
     */
    public String log(String logtype, String begin, String end, String status, Long all, Long succ, Long fail, List al,
            Long page_num) {
        return LogDbUnit.setRecord(logtype, begin, end, status, all, succ, fail, al, page_num);
    }

    /**
     * 转换为淘宝物流公司编码。
     * @param mallLogsId
     * @return
     */
    public Map parse2TaobaoLgts(String mallLogsId) {
        return dao.selectMap("TaobaoAccess.Parse2TaobaoLgtsCode", mallLogsId);
    }

    /**
     * 更新订单配送状态。
     * @param tid
     * @param status
     */
    public int refreshSendStatus(String tid, String status) {
        return dao.update("TaobaoAccess.RefreshSendStatus", Collections.asMap("Tid", tid, "Status", status));
    }

    public void recordSyncExOrder(List tidList, Map inMap) {
        List tempList = new ArrayList();
        // 排除正常订单
        for (String tid : (ArrayList<String>) tidList) {
            int count = dao.selectInteger("TaobaoAccess.IsTidExists", tid);
            if (count < 1) {
                tempList.add(tid);
            }
        }

        inMap.putAll(Collections.asMap("ex_type", "00", "ex_desc", "未同步", "status", "0"));
        inMap.put("time", Dates.format(Calendar.getInstance().getTime(), "yyyyMMdd HHmmss"));

        // 异常订单入库
        int index = 0;
        for (String tid : (ArrayList<String>) tempList) {
            if (index == 0) {
                dao.startBatch();
            }
            ++index;
            inMap.put("tid", tid);
            dao.insert("TaobaoAccess.RecordExOrder", inMap);
            if (index == BATCH_SIZE) {
                dao.executeBatch();
                index = 0;
            }
        }
        if (index > 0) {
            dao.executeBatch();
        }

        // 更新日志数量信息
        if (!Collections.isEmpty(tempList)) {
            logger.warn("淘宝订单同步监控-{}未同步淘宝订单数：" + tempList.size(), inMap.get("batch_id"));
            inMap.put("fail", tempList.size());
            dao.update("TaobaoAccess.RefreshLogFailRecord", inMap);
            exOrderSendWarnMsg(tempList, inMap);
        }
    }

    private void exOrderSendWarnMsg(List tempList, Map inMap) {
        /*StringBuffer phoneList = new StringBuffer("");*/
        String template = "淘宝漏单告警短信模板为空！";

        List configList = dao.select("TaobaoAccess.QueryMsgConfigByCateCode", EXORDER_CATECODE);
        if (Collections.isEmpty(configList)) {
            logger.warn("淘宝订单同步监控-缺失淘宝漏单短信告警配置");
            return;
        }
        Map firstConfig = (Map) configList.get(0);
        String content = (String) firstConfig.get("MESSAGE_CONTENT");
        if (!Strings.isEmpty(content)) {
            /*template = content;*/
            template = content.replace("[DATE]", (String) inMap.get("batch_id")).replace("[COUNT]",
                    Integer.toString(tempList.size()));
        }

        for (Map config : (List<Map>) configList) {
            String configId = (String) config.get("CONFIG_ID");
            List recList = dao.select("TaobaoAccess.QueryMsgReceiverByConfig", configId);
            if (Collections.isEmpty(recList)) {
                continue;
            }
            for (Map rec : (List<Map>) recList) {
                /*phoneList.append(((String) rec.get("RECEIVER_PHONE")).trim()).append(",");*/
                String phone = ((String) rec.get("RECEIVER_PHONE")).trim();
                try {
                    MallNtfplatService.sendSingle((String) firstConfig.get("SMS_BIZCODE"),
                            (String) firstConfig.get("SMS_BIZNAME"), (String) firstConfig.get("SMS_TASKNAME"),
                            template, phone);
                }
                catch (Exception e) {
                    logger.error("exOrderSendWarnMsg发送短信失败：" + phone, e);
                }
            }
        }

        // EMQ发送短信
        /*SmsMsg sms = new SmsMsg();
        String smsContent = template.replace("[DATE]", (String) inMap.get("batch_id")).replace("[COUNT]",
                Integer.toString(tempList.size()));

        sms.setContent(smsContent);
        sms.setType(SmsMsgType.DIRECT);
        sms.setTo(phoneList.toString().split(","));
        EmsSenderUtils.send("mall.req.sms", sms);*/
    }
}
