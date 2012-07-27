package org.phw.eop.sec;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.phw.eop.domain.EopAppSecurityBean;
import org.phw.eop.sec.support.SecurityCipherSupport;
import org.phw.eop.utils.Base64;
import org.phw.eop.utils.ByteUtils;

public class AESCipher implements SecurityCipherSupport {
    private static final long serialVersionUID = 1L;
    private static final String AES_ALGRITHOM = "AES";
    private String pubKey;

    @Override
    public void setEopAppSecurityBean(EopAppSecurityBean bean) {
        pubKey = bean.getPubkey();
    }

    @Override
    public String decrypt(String value) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(Base64.decode(pubKey), AES_ALGRITHOM);
            Cipher aesCipher = Cipher.getInstance(AES_ALGRITHOM);
            aesCipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] decrypted = aesCipher.doFinal(Base64.decode(value));
            return ByteUtils.toString(decrypted);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String encrypt(String value) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(Base64.decode(pubKey), AES_ALGRITHOM);
            Cipher aesCipher = Cipher.getInstance(AES_ALGRITHOM);
            aesCipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = aesCipher.doFinal(ByteUtils.toBytes(value));
            return Base64.encodeToString(encrypted, false);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
