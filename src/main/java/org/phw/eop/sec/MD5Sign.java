package org.phw.eop.sec;

import java.security.MessageDigest;

import org.phw.eop.domain.EopAppSecurityBean;
import org.phw.eop.sec.support.SecuritySignSupport;
import org.phw.eop.utils.Base64;
import org.phw.eop.utils.ByteUtils;
import org.phw.eop.utils.Strings;

public class MD5Sign implements SecuritySignSupport {
    private static final long serialVersionUID = 1L;
    private EopAppSecurityBean bean;

    @Override
    public String sign(String info) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(ByteUtils.toBytes(bean.getPubkey() + info + bean.getPubkey()));
            return Base64.encodeToString(digest, false);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setEopAppSecurityBean(EopAppSecurityBean bean) {
        this.bean = bean;
    }

    @Override
    public boolean verify(String info, String signed) {
        final String computedSign = sign(info);
        return Strings.equals(signed, computedSign);
    }

}
