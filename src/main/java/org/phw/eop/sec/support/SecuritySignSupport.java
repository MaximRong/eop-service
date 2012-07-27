package org.phw.eop.sec.support;


public interface SecuritySignSupport extends SecurityBaseSupport {
    String sign(String info);

    boolean verify(String info, String signed);
}
