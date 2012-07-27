package org.phw.eop.utils.sec;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.phw.eop.utils.Base64;
import org.phw.eop.utils.ByteUtils;

public class AesUtils {
    private static final String AES_ALGRITHOM = "AES";
    private static Cipher aesCipher = null;
    static {
        try {
            aesCipher = Cipher.getInstance(AES_ALGRITHOM);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String decrypt(String paramValue, String key) throws Exception {
        return decrypt(paramValue, Base64.decode(key));
    }

    public static String decrypt(String paramValue, byte[] key) throws Exception {
        byte[] value = Base64.decode(ByteUtils.toBytes(paramValue));
        byte[] decrypt = decrypt(value, key);
        //        long ts = ByteUtils.toLong(decrypt);
        //        if (System.currentTimeMillis() - ts > 300000) { // 5 MINUTES EXPIRED
        //            return "";
        //        }

        return ByteUtils.toString(decrypt);
    }

    public static String encrypt(String message, String key) throws Exception {
        return encrypt(message, Base64.decode(key));
    }

    public static String encrypt(String message, byte[] key) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, AES_ALGRITHOM);
        aesCipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        // aesCipher.update(ByteUtils.toBytes(System.currentTimeMillis()));
        byte[] encrypted = aesCipher.doFinal(ByteUtils.toBytes(message));
        return Base64.encodeToString(encrypted, false);
    }

    public static byte[] decrypt(byte[] value, byte[] key) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, AES_ALGRITHOM);
        aesCipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = aesCipher.doFinal(value);
        return decrypted;
    }

}
