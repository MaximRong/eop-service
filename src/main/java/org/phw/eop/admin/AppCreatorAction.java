package org.phw.eop.admin;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.UUID;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.phw.eop.support.EopAction;
import org.phw.eop.support.EopActionException;
import org.phw.eop.utils.Base64;

public class AppCreatorAction extends EopAction {

    @Override
    public Object doAction() throws EopActionException {
        String type = getStr("type");
        Object ret = null;
        try {
            if ("appcode".equals(type)) {
                String s = UUID.randomUUID().toString();
                String appcode = s.substring(0, 8) + s.substring(9, 13)
                        + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
                ret = appcode.toUpperCase();
            }
            else if ("aeskey".equals(type)) {
                // Get the KeyGenerator
                KeyGenerator kgen = KeyGenerator.getInstance("AES");
                kgen.init(128); // 192 and 256 bits may not be available

                // Generate the secret key specs.
                SecretKey skey = kgen.generateKey();
                byte[] raw = skey.getEncoded();
                ret = Base64.encodeToString(raw, false);
            }
            else if ("hmackey".equals(type)) {
                //  HmacMD5  HmacSHA1 HmacSHA256 HmacSHA384 HmacSHA512 
                String algorithm = getStr("algorithm", "HmacMD5");

                KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
                SecretKey secretKey = keyGenerator.generateKey();
                ret = Base64.encodeToString(secretKey.getEncoded(), false);
            }
            else if ("rsa".equals(type) || "dsa".equals(type)) {
                int keysize = getInt("keysize", 1024);
                if (keysize < 512 || keysize > 1024 || keysize % 64 != 0) {
                    keysize = 1024;
                }
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance(type.toUpperCase());
                keyGen.initialize(keysize);
                KeyPair keyPair = keyGen.generateKeyPair();
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("pubkey", Base64.encodeToString(keyPair.getPublic().getEncoded(), false));
                hashMap.put("prikey", Base64.encodeToString(keyPair.getPrivate().getEncoded(), false));

                ret = hashMap;
            }
        }
        catch (NoSuchAlgorithmException e) {
            throw new EopActionException("B001", "NoSuchAlgorithmException", e);
        }

        return ret;
    }
}
