package org.phw.eop.sec;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.phw.eop.domain.EopAppSecurityBean;
import org.phw.eop.sec.support.SecuritySignSupport;
import org.phw.eop.utils.Base64;
import org.phw.eop.utils.ByteUtils;
import org.phw.eop.utils.Strings;

public class HMacSign implements SecuritySignSupport {
    private static final long serialVersionUID = 1L;
    /**
     * MAC算法可选以下多种算法 。
     * 
     * <pre>
     * HmacMD5  
     * HmacSHA1  
     * HmacSHA256  
     * HmacSHA384  
     * HmacSHA512
     * </pre>
     */
    private static final String KEY_MAC = "HmacMD5";
    private String pubKey;

    @Override
    public void setEopAppSecurityBean(EopAppSecurityBean bean) {
        pubKey = bean.getPubkey();
    }

    @Override
    public String sign(String value) {
        try {
            SecretKey secretKey = new SecretKeySpec(Base64.decode(pubKey), KEY_MAC);
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            byte[] ret = mac.doFinal(ByteUtils.toBytes(value));
            return Base64.encodeToString(ret, false);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verify(String info, String signed) {
        final String computedSign = sign(info);
        return Strings.equals(signed, computedSign);
    }
}
