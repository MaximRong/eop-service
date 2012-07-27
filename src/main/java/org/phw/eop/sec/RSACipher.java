package org.phw.eop.sec;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.phw.eop.domain.EopAppSecurityBean;
import org.phw.eop.sec.support.SecurityCipherSupport;
import org.phw.eop.utils.Base64;
import org.phw.eop.utils.ByteUtils;

public class RSACipher implements SecurityCipherSupport {
    private static final long serialVersionUID = 1L;
    private static final String ALGORITHM = "RSA";
    private static final String RSA_ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";
    private PrivateKey privateKey;
    private PublicKey publicKey;

    @Override
    public void setEopAppSecurityBean(EopAppSecurityBean bean) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            String prikey = bean.getPrikey();
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decode(prikey));
            privateKey = keyFactory.generatePrivate(privateKeySpec);

            String pubkey = bean.getPubkey();
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decode(pubkey));
            publicKey = keyFactory.generatePublic(publicKeySpec);

        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String decrypt(String value) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return ByteUtils.toString(cipher.doFinal(Base64.decode(value)));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String encrypt(String value) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeToString(cipher.doFinal(ByteUtils.toBytes(value)), false);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
