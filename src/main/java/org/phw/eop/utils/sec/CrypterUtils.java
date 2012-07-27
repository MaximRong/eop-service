package org.phw.eop.utils.sec;

import org.apache.commons.lang.StringUtils;

/**
 * 3DES, AES加密解密小函数.
 *
 * @author wanglei
 *
 * 2012-3-16
 */
public class CrypterUtils {

    public static enum Algrithom {
        TripleDES, AES
    }

    public static final String DES_PREFIX = "{3DES}";
    public static final String AES_PREFIX = "{AES}";

    /**
     * 加密操作。
     * @param key 密钥。
     * @param data 数据。
     * @param algrithom 算法。
     * @return 密文。
     */
    public static String encrypt(String key, String data, Algrithom algrithom) {
        try {
            switch (algrithom) {
            case AES:
                return AesUtils.encrypt(data, key);
            default:
                throw new RuntimeException("Unsupported algrithom: " + algrithom);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    /**
     * 解密操作。
     * @param key 密钥。
     * @param data 数据。
     * @param algrithom 算法。
     * @return 原文。
     */
    public static String decrypt(String key, String data, Algrithom algrithom) {
        try {
            switch (algrithom) {
            case AES:
                return AesUtils.decrypt(data, key);
            default:
                throw new RuntimeException("Unsupported algrithom: " + algrithom);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 解密操作。
     * @param key 密钥。
     * @param data 密文。
     * @return 原文。
     */
    public static String decrypt(String key, String data) {
        if (StringUtils.isEmpty(data)) {
            return "";
        }
        if (data.startsWith(DES_PREFIX)) {
            return decrypt(key, data.replace(DES_PREFIX, ""), Algrithom.TripleDES);
        }
        else if (data.startsWith(AES_PREFIX)) {
            return decrypt(key, data.replace(AES_PREFIX, ""), Algrithom.AES);
        }
        return data;
    }

    /**
     * 加密操作。
     * @param key 密钥。
     * @param data 原文。
     * @return 密文。
     */
    public static String encrypt(String key, String data) {
        if (data == null) {
            return data;
        }
        if (data.startsWith(DES_PREFIX)) {
            return encrypt(key, data.replace(DES_PREFIX, ""), Algrithom.TripleDES);
        }
        else if (data.startsWith(AES_PREFIX)) {
            return encrypt(key, data.replace(AES_PREFIX, ""), Algrithom.AES);
        }
        return data;
    }
}
