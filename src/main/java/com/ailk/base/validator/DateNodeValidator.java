package com.ailk.base.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.ArrayUtils;

import com.ailk.ecs.esf.service.eface.base.Direction;
import com.ailk.ecs.esf.service.eface.base.EfaceException;
import com.ailk.ecs.esf.service.eface.base.ExtraInfoSetter;
import com.ailk.ecs.esf.service.eface.base.ValueValidator;
import com.ailk.ecs.esf.service.eface.tag.EfaceTag;

/**
 * 报文日期格式校验器。
 * 
 * 若配置了type="Date:yyyyMMddHHmmss"，则报文引擎会将节点解析成Date类型。
 * 增加此校验器，对日期节点进行校验，最终解析结果是String类型。
 *
 * @author wanglei
 *
 * 2012-3-9
 */
public class DateNodeValidator implements ValueValidator, ExtraInfoSetter {
    private String fmtStr;

    @Override
    public void validate(Object root, Object currValue, EfaceTag tag, Direction dir) {
        SimpleDateFormat format = new SimpleDateFormat(fmtStr);
        try {
            format.parse((String) currValue);
        }
        catch (ParseException e) {
            throw new EfaceException("Date节点" + tag.getTagName() + "格式异常，应为" + fmtStr + "，实为" + currValue);
        }
    }

    @Override
    public void setExtraInfo(String[] extraInfos) {
        fmtStr = ArrayUtils.isEmpty(extraInfos) ? "yyyyMMddHHmmss" : extraInfos[0];
    }

}
