package org.phw.eop.domain;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.phw.eop.mgr.EopJsonSchemaMgr;
import org.phw.eop.support.ValueValidator;
import org.phw.eop.utils.Clazz;
import org.phw.eop.utils.Converts;
import org.phw.eop.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

public class EopActionParamBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static transient Logger logger = LoggerFactory.getLogger(EopActionParamBean.class);
    private String actionid, name, type, remark, range, pattern, validator;
    private boolean optional, encrypted, multi, json;
    private int minlen, maxlen;
    private ValueValidator validatorBean;
    private Range[] ranges;
    private Class valueType;
    private Pattern regexPattern;
    private Range[] multiItemsRange;
    private String itemsRange;

    @Override
    public String toString() {
        return "EopActionParamBean [actionid=" + actionid + ", name=" + name + ", type=" + type + ", remark=" + remark
                + ", range=" + range + ", pattern=" + pattern + ", validator=" + validator + ", optional=" + optional
                + ", encrypted=" + encrypted + ", multi=" + multi + ", json=" + json + ", minlen=" + minlen
                + ", maxlen=" + maxlen + ", validatorBean=" + validatorBean + ", ranges=" + Arrays.toString(ranges)
                + ", valueType=" + valueType + ", regexPattern=" + regexPattern + "]";
    }

    public void afterPropertiesSet() {
        // 带有[]形式的是数组。
        valueType = Converts.parseType(Strings.substringBefore(type, "["));
        itemsRange = Strings.substringBetween(type, "[", "]");
        if (itemsRange != null) {
            setMulti(true);
            if (!Strings.isBlank(itemsRange)) {
                multiItemsRange = RangeParser.parse(itemsRange, Integer.class);
            }
        }

        // 如果type是JSON或者JSON:org.phw.eop.DemoBean，那么
        // 参数类型为json，并且可以从pattern定义的json schema来校验参数值。
        // 如果校验通过，则尝试转换到冒号后面指定的POJO类型。
        json = Strings.equalsIgnoreCase("JSON", Strings.substringBefore(type, ":"));
        if (json) {
            String beanType = Strings.substringAfter(type, ":");
            if (!Strings.isBlank(beanType)) {
                if (Clazz.classExists(beanType)) {
                    valueType = Clazz.forClass(beanType);
                }
                else {
                    logger.warn("{} is not a valid class name", beanType);
                }
            }
        }

        if (!Strings.isEmpty(range)) {
            ranges = RangeParser.parse(range, valueType);
        }

        if (!Strings.isEmpty(validator)) {
            if (!Clazz.classExists(validator)) {
                return;
            }

            Class<?> clz = Clazz.forClass(validator);
            if (!clz.isAssignableFrom(ValueValidator.class)) {
                return;
            }

            validatorBean = (ValueValidator) Clazz.newInstance(clz);
        }

        if (!Strings.isEmpty(pattern) && !json) {
            regexPattern = Pattern.compile(pattern);
        }
    }

    public Object isValid(String paramValue) {
        Object returnObject = null;
        if (!isMulti()) {
            returnObject = isSingleValid(paramValue);
        }
        else {
            String[] multiValues = Strings.split(paramValue, "^", false);
            if (!Range.inRange(multiItemsRange, multiValues.length)) {
                throw new RuntimeException(getName() + "'s items numb should be in range of " + itemsRange);
            }

            Object[] ret = (Object[]) Array.newInstance(valueType, multiValues.length);
            for (int i = 0; i < multiValues.length; ++i) {
                ret[i] = isSingleValid(multiValues[i]);
            }

            returnObject = ret;
        }

        return returnObject;
    }

    public Object isSingleValid(String paramValue) {
        Object val = null;
        if (!isJson()) {
            val = Converts.convert(paramValue, valueType);

            if (val == null) {
                throw new RuntimeException(getName() + "'s type is not " + type);
            }

            if (minlen > 0 && paramValue.length() < minlen) {
                throw new RuntimeException(getName() + " is shorter than " + minlen);
            }
            if (maxlen > 0 && paramValue.length() > maxlen) {
                throw new RuntimeException(getName() + " is longer than " + maxlen);
            }
            if (regexPattern != null && !regexPattern.matcher(paramValue).matches()) {
                throw new RuntimeException(getName() + " does not match " + pattern);
            }

            if (!Range.inRange(ranges, val)) {
                throw new RuntimeException(getName() + " is not in range of " + range);
            }
        }
        else {
            if (!Strings.isEmpty(pattern)) {
                String ret = EopJsonSchemaMgr.validateJson(paramValue, pattern);
                if (ret != null) {
                    throw new RuntimeException(getName() + " is not valid " + ret);
                }
            }
            try {
                val = valueType != String.class ? JSON.parseObject(paramValue, valueType) : paramValue;
            }
            catch (JSONException e) {
                throw new RuntimeException(getName() + "'s value cannot converted to " + valueType, e);
            }
        }

        if (validatorBean != null) {
            validatorBean.validate(val);
        }

        return val;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getActionid() {
        return actionid;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getRemark() {
        return remark;
    }

    public String getRange() {
        return range;
    }

    public String getValidator() {
        return validator;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public boolean isMulti() {
        return multi;
    }

    public int getMinlen() {
        return minlen;
    }

    public int getMaxlen() {
        return maxlen;
    }

    public void setActionid(String actionid) {
        this.actionid = actionid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public void setValidator(String validator) {
        this.validator = validator;

    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }

    public void setMinlen(int minlen) {
        this.minlen = minlen;
    }

    public void setMaxlen(int maxlen) {
        this.maxlen = maxlen;
    }

    public boolean isJson() {
        return json;
    }

    public void setJson(boolean json) {
        this.json = json;
    }

}
