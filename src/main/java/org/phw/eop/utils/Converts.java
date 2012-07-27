package org.phw.eop.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Convert String to primitives.
 * @author BingooHuang
 *
 */
public class Converts {
    private static Map<Class, Class> primitiveMap = new HashMap<Class, Class>();
    static {
        primitiveMap.put(boolean.class, Boolean.class);
        primitiveMap.put(byte.class, Byte.class);
        primitiveMap.put(char.class, Character.class);
        primitiveMap.put(short.class, Short.class);
        primitiveMap.put(int.class, Integer.class);
        primitiveMap.put(long.class, Long.class);
        primitiveMap.put(float.class, Float.class);
        primitiveMap.put(double.class, Double.class);
    }

    public static Class parseType(String type) {

        Class destClass = null;
        if (Strings.isEmpty(type)) {
            destClass = String.class;
        }
        else if ("boolean".equalsIgnoreCase(type)) {
            destClass = Boolean.class;
        }
        else if ("char".equalsIgnoreCase(type) || "Character".equalsIgnoreCase(type)) {
            destClass = Character.class;
        }
        else if ("short".equalsIgnoreCase(type)) {
            destClass = Short.class;
        }
        else if ("int".equalsIgnoreCase(type) || "Integer".equalsIgnoreCase(type)) {
            destClass = Integer.class;
        }
        else if ("long".equalsIgnoreCase(type)) {
            destClass = Long.class;
        }
        else if ("float".equalsIgnoreCase(type)) {
            destClass = Float.class;
        }
        else if ("double".equalsIgnoreCase(type)) {
            destClass = Double.class;
        }
        else if ("string".equalsIgnoreCase(type)) {
            destClass = String.class;
        }
        else if ("Decimal".equalsIgnoreCase(type)) {
            destClass = BigDecimal.class;
        }
        else if ("date".equalsIgnoreCase(type)) {
            destClass = Date.class;
        }
        else if (Clazz.classExists(type)) {
            destClass = Clazz.forClass(type);
        }
        else {
            destClass = String.class;
        }

        return destClass;
    }

    /**
     * Best try to convert string to destination class.
     * If destClass is one of
     * the supported primitive classes, an object of that type is returned. 
     * Otherwise, the original string is returned.
     * @param value String value
     * @param destClass Destination Class
     * @return converted value
     */
    public static Object convert(String value, Class<?> destClass) {
        if (value == null) {
            return null;
        }

        if (destClass == String.class) {
            return value;
        }

        if ("".equals(value)) {
            return null;
        }

        if (destClass.isPrimitive()) {
            destClass = primitiveMap.get(destClass);
        }

        try {
            Method m = destClass.getMethod("valueOf", String.class);
            int mods = m.getModifiers();
            if (Modifier.isStatic(mods) && Modifier.isPublic(mods)) {
                return m.invoke(null, value);
            }
        }
        catch (NoSuchMethodException e) {
            if (destClass == Character.class) {
                return Character.valueOf(value.charAt(0));
            }
        }
        catch (IllegalAccessException e) {
            // this won't happen
        }
        catch (InvocationTargetException e) {
            // when this happens, the string cannot be converted to the intended type
            // we are ignoring it here - the original string will be returned.
            // But it can be re-thrown if desired!
        }
        catch (Throwable e) {
            // Silently.
        }
        return null;
    }
}
