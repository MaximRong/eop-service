package org.phw.eop.utils;

public interface EopConst {
    String EOP_APPCODE = "eop_appcode";
    String EOP_ACTION = "eop_action";
    String EOP_REQTS = "eop_reqts";
    String EOP_APPTX = "eop_apptx";
    String EOP_FMT = "eop_fmt";
    String EOP_CIPHER = "eop_cipher";
    String EOP_MOCK = "eop_mock";
    String EOP_SIGN = "eop_sign";

    String PARAM_PREFIX_APPTIMELIMIT = "app.timeslimit.";
    String PARAM_PREFIX_ACTION_MIN_INTERVAL_SECONDS = "action.min.interval.seconds.";
    String PARAM_REQTIMEOUTMINUTES = "app.req.timeout.minutes";
    String PARAM_PREFIX_APPTX_REQUIRED = "app.apptx.required.";

    ThreadSafeSimpleDateFormat FMT_YYYYMMDDHHMMSS = new ThreadSafeSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    ThreadSafeSimpleDateFormat FMT_YYYYMMDD = new ThreadSafeSimpleDateFormat("yyyyMMdd");

}
