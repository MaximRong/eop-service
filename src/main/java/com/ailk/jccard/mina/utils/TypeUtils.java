package com.ailk.jccard.mina.utils;

import java.util.HashMap;

import org.phw.core.lang.Collections;

public class TypeUtils {

    private static HashMap<Byte, String> essCodeToTypeMap = (HashMap<Byte, String>) Collections.asMap(
            (byte) 0, "IF1", (byte) 1, "IF2", (byte) 2, "IF3", (byte) 3, "IF5");

    private static HashMap<String, Byte> essTypeToCodeMap = (HashMap<String, Byte>) Collections.asMap(
            "IF1", (byte) 0, "IF2", (byte) 1, "IF3", (byte) 2, "IF5", (byte) 3);

    private static HashMap<Byte, String> jcCodeToTypeMap = (HashMap<Byte, String>) Collections.asMap(
            (byte) 0, "IF1", (byte) 1, "IF2", (byte) 2, "IF3", (byte) 3, "IF4", (byte) 4, "IF5");

    private static HashMap<String, Byte> jcTypeToCodeMap = (HashMap<String, Byte>) Collections.asMap(
            "IF1", (byte) 0, "IF2", (byte) 1, "IF3", (byte) 2, "IF4", (byte) 3, "IF5", (byte) 4);

    public static String getEssType(byte typeFlag) {
        return essCodeToTypeMap.get(typeFlag);
    }

    public static byte getEssCode(String type) {
        return essTypeToCodeMap.get(type);
    }

    public static String getJcType(byte typeFlag) {
        return jcCodeToTypeMap.get(typeFlag);
    }

    public static byte getJcCode(String type) {
        return jcTypeToCodeMap.get(type);
    }

}
