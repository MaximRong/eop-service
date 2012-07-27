package org.phw.eop.domain;

import java.io.Serializable;

import org.phw.eop.utils.Converts;

public class EopParamBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String paramcode, paramname, paramvalue, remark;

    @Override
    public String toString() {
        return "EopParamBean [paramcode=" + paramcode + ", paramname=" + paramname + ", paramvalue="
                + paramvalue + ", remark=" + remark + "]";
    }

    public String getParamcode() {
        return paramcode;
    }

    public <T> T valueOf(Class<T> destClass) {
        return (T) Converts.convert(paramvalue, destClass);
    }

    public String getParamname() {
        return paramname;
    }

    public String getParamvalue() {
        return paramvalue;
    }

    public String getRemark() {
        return remark;
    }

    public void setParamcode(String paramcode) {
        this.paramcode = paramcode;
    }

    public void setParamname(String paramname) {
        this.paramname = paramname;
    }

    public void setParamvalue(String paramvalue) {
        this.paramvalue = paramvalue;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
