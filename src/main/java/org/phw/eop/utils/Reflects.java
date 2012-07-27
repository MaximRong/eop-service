package org.phw.eop.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class Reflects {
    public static List<FieldInfo> computeSetters(Class<?> clazz) {
        ArrayList<FieldInfo> arrayList = new ArrayList<FieldInfo>();
        for (Method method : clazz.getMethods()) {
            String methodName = method.getName();
            if (methodName.length() < 4) {
                continue;
            }

            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            if (!method.getReturnType().equals(Void.TYPE)) {
                continue;
            }

            if (method.getParameterTypes().length != 1) {
                continue;
            }

            if (methodName.startsWith("set") && Character.isUpperCase(methodName.charAt(3))) {
                String propertyName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);

                arrayList.add(new FieldInfo(propertyName, method, null));
            }
        }

        for (Field field : clazz.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            if (!Modifier.isPublic(field.getModifiers())) {
                continue;
            }

            boolean contains = false;
            for (FieldInfo item : arrayList) {
                if (item.getName().equals(field.getName())) {
                    contains = true;
                    continue;
                }
            }

            if (contains) {
                continue;
            }

            arrayList.add(new FieldInfo(field.getName(), null, field));
        }

        return arrayList;
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        }
        catch (Exception e) {
            return null;
        }
    }
}
