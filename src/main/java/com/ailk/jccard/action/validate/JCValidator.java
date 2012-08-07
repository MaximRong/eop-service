package com.ailk.jccard.action.validate;

import org.phw.core.lang.Pair;

import com.alibaba.fastjson.JSONObject;

public interface JCValidator {

    Pair<Boolean, String> validate(JSONObject reqObject);

}
