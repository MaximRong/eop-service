package com.ailk.jccard.action.validate;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.phw.core.lang.Pair;

import com.alibaba.fastjson.JSONObject;

public class JCBaseValidator implements JCValidator {

    private static String[] VALIDATE_AID_MISSIONTYPE = { "0x05", "0x06", "0x07", "0x08", "0x09" };

    @Override
    public Pair<Boolean, String> validate(JSONObject reqObject) {
        Pair<Boolean, String> staffInfoValidate = staffInfoValidate(reqObject);
        if (!staffInfoValidate.getFirst()) {
            return staffInfoValidate;
        }
        Pair<Boolean, String> cardInfoValidate = cardInfoValidate(reqObject);
        if (!cardInfoValidate.getFirst()) {
            return cardInfoValidate;
        }

        if (ArrayUtils.contains(VALIDATE_AID_MISSIONTYPE, reqObject.getString("missionType"))) {
            String reqData = reqObject.getString("reqData");
            if (StringUtils.isEmpty(reqData)) {
                return Pair.makePair(false, "reqData不可为空!");
            }
        }
        return Pair.makePair(true, "ok");
    }

    private Pair<Boolean, String> staffInfoValidate(JSONObject reqObject) {
        String operatorID = reqObject.getString("operatorID");
        if (StringUtils.isEmpty(operatorID)) {
            return Pair.makePair(false, "操作员工号不可为空!");
        }
        String provinceCode = reqObject.getString("province");
        if (StringUtils.isEmpty(provinceCode)) {
            return Pair.makePair(false, "省份不可为空!");
        }
        String cityCode = reqObject.getString("city");
        if (StringUtils.isEmpty(cityCode)) {
            return Pair.makePair(false, "地市不可为空!");
        }
        String areaCode = reqObject.getString("area");
        if (StringUtils.isEmpty(areaCode)) {
            return Pair.makePair(false, "区域不可为空!");
        }
        String channelCode = reqObject.getString("channel");
        if (StringUtils.isEmpty(channelCode)) {
            return Pair.makePair(false, "渠道Code不可为空!");
        }
        String channelType = reqObject.getString("channelType");
        if (StringUtils.isEmpty(channelType)) {
            return Pair.makePair(false, "渠道类型不可为空!");
        }
        String idCard = reqObject.getString("idCard");
        if (StringUtils.isEmpty(idCard)) {
            return Pair.makePair(false, "身份证号不可为空!");
        }
        return Pair.makePair(true, "ok");
    }

    private Pair<Boolean, String> cardInfoValidate(JSONObject reqObject) {
        String msisdn = reqObject.getString("msisdn");
        if (StringUtils.isEmpty(msisdn)) {
            return Pair.makePair(false, "msisdn不可为空!");
        }
        if (11 != msisdn.length()) {
            return Pair.makePair(false, "msisdn必须是11位!");
        }
        String iccid = reqObject.getString("iccid");
        if (StringUtils.isEmpty(iccid)) {
            return Pair.makePair(false, "iccid不可为空!");
        }
        if (!iccid.startsWith("89")) {
            return Pair.makePair(false, "iccid必须以89开头!");
        }
        if (10 != iccid.length()) {
            return Pair.makePair(false, "iccid必须10位!");
        }
        String imsi = reqObject.getString("imsi");
        if (!imsi.startsWith("46")) {
            return Pair.makePair(false, "imsi必须以46开头!");
        }
        if (8 != imsi.length()) {
            return Pair.makePair(false, "imsi必须8位!");
        }
        String missionType = reqObject.getString("missionType");
        if (StringUtils.isEmpty(missionType)) {
            return Pair.makePair(false, "missionType不可为空!");
        }
        return Pair.makePair(true, "ok");
    }
}
