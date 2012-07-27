package org.phw.eop.utils;

import java.io.UnsupportedEncodingException;

public class ByteUtils {

    public static byte[] toBytes(long val) {
        byte[] b = new byte[8];
        for (int i = 7; i > 0; i--) {
            b[i] = (byte) val;
            val >>>= 8;
        }
        b[0] = (byte) val;
        return b;
    }

    /**
     * Size of long in bytes
     */
    public static final int SIZEOF_LONG = Long.SIZE / Byte.SIZE;

    public static long toLong(byte[] bytes) {
        if (SIZEOF_LONG > bytes.length) {
            return 0;
        }

        long l = 0;
        for (int i = 0; i < SIZEOF_LONG; i++) {
            l <<= 8;
            l ^= bytes[i] & 0xFF;
        }
        return l;
    }

    public static byte[] toBytes(String s) {
        return toBytes(s, "UTF-8");
    }

    public static byte[] toBytes(String s, String charsetName) {
        if (s == null) {
            return null;
        }

        try {
            return s.getBytes(charsetName);
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String toString(final byte[] b) {
        return toString(b, 0);
    }

    public static String toString(final byte[] b, int off) {
        if (b == null) {
            return null;
        }
        try {
            return new String(b, off, b.length - off, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

}
