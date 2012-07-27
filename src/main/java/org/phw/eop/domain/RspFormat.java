package org.phw.eop.domain;

import org.phw.eop.utils.Strings;

public enum RspFormat {
    JSON("json"), XML("xml"), UNSET("unset");

    private String abbreviation = ""; //缩写

    //定义自己的构造器
    private RspFormat(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public String toString() {
        return this.abbreviation;
    }

    public static RspFormat from(String sFormat) {
        if (Strings.equalsIgnoreCase(sFormat, "xml")) {
            return RspFormat.XML;
        }
        if (Strings.equalsIgnoreCase(sFormat, "json")) {
            return RspFormat.JSON;
        }

        return RspFormat.UNSET;
    }
}
