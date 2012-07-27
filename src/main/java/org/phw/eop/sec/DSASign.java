package org.phw.eop.sec;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.phw.eop.domain.EopAppSecurityBean;
import org.phw.eop.sec.support.SecuritySignSupport;
import org.phw.eop.utils.Base64;
import org.phw.eop.utils.ByteUtils;

public class DSASign implements SecuritySignSupport {
    private static final long serialVersionUID = 1L;
    /**
     * 算法名称。
     */
    protected static final String ALGORITHM = "DSA";
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
    public String sign(String info) {
        try {
            Signature signet = Signature.getInstance(ALGORITHM);
            signet.initSign(privateKey);
            // 更新要由字节签名或验证的数据
            signet.update(ByteUtils.toBytes(info));
            // 签署或验证所有更新字节的签名，返回签名
            return Base64.encodeToString(signet.sign(), false);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verify(String info, String signed) {
        try {
            Signature signet = Signature.getInstance(ALGORITHM);
            signet.initVerify(publicKey);
            // 更新要由字节签名或验证的数据
            signet.update(ByteUtils.toBytes(info));
            // 签署或验证所有更新字节的签名，返回签名
            return signet.verify(Base64.decode(signed));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
