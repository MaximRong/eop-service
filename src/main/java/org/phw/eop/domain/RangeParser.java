package org.phw.eop.domain;

import java.util.ArrayList;

import org.phw.eop.utils.Converts;
import org.phw.eop.utils.Strings;

/**
 * 解析起始结束范围字符串值。
 * @author BingooHuang
 *
 */
public abstract class RangeParser {
    /**
     * 解析范围取值字符串.<br/>
     * 例如:<br/>
     * <ul>
     * <li>1) 1~3,4~7</li>
     * <li>2) ~5,7</li>
     * <li>3) ~ (不允许)</li>
     * <li>4) 3~1 (不允许) </li>
     * </ul>
     * 
     * 支持空白的范围无取值，例如：
     * 
     * @param rangeString 范围字符串表达式
     * @param converter 值转换器
     * @return 范围值数组
     */
    public static Range[] parse(String rangeString, Class type) {
        String[] tokens = Strings.split(rangeString, ",", false);
        ArrayList<Range> arr = new ArrayList<Range>();

        for (String token : tokens) {
            token = Strings.trim(token);
            if ("~".equals(token)) {
                throw new RuntimeException(rangeString + "范围表达式非法，不允许独立的~符号存在");
            }

            int hyphenPos = token.indexOf('~');

            if (hyphenPos < 0) {
                Object value = Converts.convert(token, type);
                arr.add(Range.createSingleValueRange(token, value));
            }
            else {
                String startStr = Strings.trim(token.substring(0, hyphenPos));
                String endStr = Strings.trim(token.substring(hyphenPos + 1));
                Range range = createRange(rangeString, type, token, startStr, endStr);

                arr.add(range);
            }
        }

        return arr.toArray(new Range[arr.size()]);
    }

    private static Range createRange(String rangeString, Class type, String token,
            String startStr, String endStr) {
        Range range = null;

        if (Strings.isEmpty(startStr)) {
            Object end = Converts.convert(endStr, type);
            range = Range.createOnlyEndRange(token, end);
        }
        else if (Strings.isEmpty(endStr)) {
            Object start = Converts.convert(startStr, type);
            range = Range.createOnlyStartRange(token, start);
        }
        else {
            Object start = Converts.convert(startStr, type);
            Object end = Converts.convert(endStr, type);

            range = Range.createStartEndRange(token, start, end);
        }

        return range;
    }
}
