package org.phw.eop.sec;

import org.phw.eop.domain.EopAppSecurityBean;
import org.phw.eop.sec.support.SecurityBaseSupport;

public class SecurityFactory {
    public static SecurityBaseSupport getSecurityBean(EopAppSecurityBean bean) {
        SecurityBaseSupport ret = null;
        if ("MD5".equalsIgnoreCase(bean.getAlgorithm())) {
            ret = new MD5Sign();
        }
        else if ("HMAC".equalsIgnoreCase(bean.getAlgorithm())) {
            ret = new HMacSign();
        }
        else if ("DSA".equalsIgnoreCase(bean.getAlgorithm())) {
            ret = new DSASign();
        }
        else if ("AES".equalsIgnoreCase(bean.getAlgorithm())) {
            ret = new AESCipher();
        }
        else if ("RSA".equalsIgnoreCase(bean.getAlgorithm())) {
            ret = new RSACipher();
        }

        if (ret != null) {
            ret.setEopAppSecurityBean(bean);
        }
        return ret;
    }
}
