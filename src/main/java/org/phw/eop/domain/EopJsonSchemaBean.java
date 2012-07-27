package org.phw.eop.domain;

import java.io.Serializable;

public class EopJsonSchemaBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String uri, schema, remark;
    private int seq;

    @Override
    public String toString() {
        return "EopJsonSchemaBean [uri=" + uri + ", schema=" + schema + ", remark=" + remark + ", seq=" + seq + "]";
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

}
