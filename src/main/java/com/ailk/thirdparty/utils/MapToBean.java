package com.ailk.thirdparty.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.phw.eop.api.ApiException;
import org.phw.eop.api.Constants;
import org.phw.eop.api.internal.mapping.Reader;
import org.phw.eop.api.internal.mapping.annotation.EopBean;
import org.phw.eop.api.internal.mapping.annotation.ReqField;
import org.phw.eop.api.internal.mapping.propmapping.IPropMapping;
import org.phw.eop.api.internal.util.EopUtils;
import org.phw.eop.api.internal.util.StringUtils;
import org.phw.eop.api.internal.util.Strings;

public class MapToBean {

    public static boolean isCheckType = false;

    public static Map<Class, Class> primitiveMap = new HashMap<Class, Class>();
    static {
        primitiveMap.put(boolean.class, Boolean.class);
        primitiveMap.put(byte.class, Byte.class);
        primitiveMap.put(char.class, Character.class);
        primitiveMap.put(short.class, Short.class);
        primitiveMap.put(int.class, Integer.class);
        primitiveMap.put(long.class, Long.class);
        primitiveMap.put(float.class, Float.class);
        primitiveMap.put(double.class, Double.class);
        primitiveMap.put(String.class, String.class);
    }

    public <T> void convert(final Map<?, ?> inMap, T result) {
        convert(result, new Reader() {
            @Override
            public boolean hasReturnField(Object name) {
                return inMap.containsKey(name);
            }

            @Override
            public Object getPrimitiveObject(Object name) {
                return inMap.get(name);
            }

            @Override
            public Object getObject(Object name, Class<?> type) throws ApiException {
                Object tmp = inMap.get(name);
                if (tmp instanceof Map<?, ?>) {
                    Map<?, ?> map = (Map<?, ?>) tmp;
                    Object newInstance;
                    try {
                        newInstance = type.newInstance();
                    }
                    catch (Exception e) {
                        throw new ApiException(e);
                    }
                    convert(map, newInstance);
                    return newInstance;
                }
                return null;
            }

            @Override
            public Object getMappedOutObject(Class<?> type) throws ApiException {
                Object newInstance;
                try {
                    newInstance = type.newInstance();
                }
                catch (Exception e) {
                    throw new ApiException(e);
                }
                convert(inMap, newInstance);
                return newInstance;
            }

            @Override
            public List<?> getListObjects(Object itemName, Class<?> subType) throws ApiException {
                List<Object> listObjs = null;

                Object listTmp = inMap.get(itemName);
                if (listTmp == null) {
                    return null;
                }
                List<Object> tmpList = new ArrayList<Object>();
                if (!(listTmp instanceof List<?>)) {
                    tmpList.add(listTmp);
                }
                else {
                    tmpList = (List<Object>) listTmp;
                }

                listObjs = new ArrayList<Object>();
                for (Object subTmp : tmpList) {
                    if (subTmp == null) {
                        continue;
                    }
                    if (subTmp instanceof Map<?, ?>) {// object
                        Map<?, ?> subMap = (Map<?, ?>) subTmp;
                        Object subObj = null;
                        try {
                            subObj = subType.newInstance();
                        }
                        catch (Exception e) {
                            throw new ApiException(e);
                        }
                        convert(subMap, subObj);
                        listObjs.add(subObj);
                    }
                    else if (subTmp instanceof List<?>) {// array
                        // TODO not support yet
                    }
                    else {// boolean, long, double, string
                        listObjs.add(subTmp);
                    }
                }
                return listObjs;
            }
        });
    }

    private <T> void convert(T result, Reader reader) {
        try {
            Class<T> clazz = (Class<T>) result.getClass();

            BeanInfo beanInfo = Introspector.getBeanInfo(clazz, Object.class);
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor pd : pds) {
                Method method = pd.getWriteMethod();
                if (method == null) { // ignore read-only fields
                    continue;
                }

                String itemName = pd.getName();
                Field field = EopUtils.getDeclaredField(clazz, itemName);
                if (field == null) {
                    continue;
                }

                itemName = getFieldKey(clazz, field);
                if (Strings.isEmpty(itemName)) {
                    continue;
                }

                Class<?> typeClass = field.getType();
                if (typeClass.isPrimitive()) {
                    typeClass = primitiveMap.get(typeClass);
                }

                // 目前
                if (String.class.isAssignableFrom(typeClass)) {
                    Object value = reader.getPrimitiveObject(itemName);
                    if (value instanceof String) {
                        method.invoke(result, value.toString());
                    }
                    else {
                        if (isCheckType && value != null) {
                            throw new ApiException(itemName + " is not a String");
                        }
                        if (value != null) {
                            method.invoke(result, value.toString());
                        }
                        else {
                            method.invoke(result, "");
                        }
                    }
                }
                else if (Long.class.isAssignableFrom(typeClass)) {
                    Object value = reader.getPrimitiveObject(itemName);
                    if (value instanceof Long) {
                        method.invoke(result, (Long) value);
                    }
                    else {
                        if (isCheckType && value != null) {
                            throw new ApiException(itemName + " is not a Number(Long)");
                        }
                        if (StringUtils.isNumeric(value)) {
                            method.invoke(result, Long.valueOf("" + value));
                        }
                    }
                }
                else if (Integer.class.isAssignableFrom(typeClass)) {
                    Object value = reader.getPrimitiveObject(itemName);
                    if (value instanceof Integer) {
                        method.invoke(result, (Integer) value);
                    }
                    else {
                        if (isCheckType && value != null) {
                            throw new ApiException(itemName + " is not a Number(Integer)");
                        }
                        if (StringUtils.isNumeric(value)) {
                            method.invoke(result, Integer.valueOf("" + value));
                        }
                    }
                }
                else if (Boolean.class.isAssignableFrom(typeClass)) {
                    Object value = reader.getPrimitiveObject(itemName);
                    if (value instanceof Boolean) {
                        method.invoke(result, (Boolean) value);
                    }
                    else {
                        if (isCheckType && value != null) {
                            throw new ApiException(itemName + " is not a Boolean");
                        }
                        if (value != null) {
                            method.invoke(result, Boolean.valueOf(value.toString()));
                        }
                    }
                }
                else if (Double.class.isAssignableFrom(typeClass)) {
                    Object value = reader.getPrimitiveObject(itemName);
                    if (value instanceof Double) {
                        method.invoke(result, (Double) value);
                    }
                    else {
                        if (isCheckType && value != null) {
                            throw new ApiException(itemName + " is not a Double");
                        }
                    }
                }
                else if (Number.class.isAssignableFrom(typeClass)) {
                    Object value = reader.getPrimitiveObject(itemName);
                    if (value instanceof Number) {
                        method.invoke(result, (Number) value);
                    }
                    else {
                        if (isCheckType && value != null) {
                            throw new ApiException(itemName + " is not a Number");
                        }
                    }
                }
                else if (Date.class.isAssignableFrom(typeClass)) {
                    DateFormat format = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
                    format.setTimeZone(TimeZone.getTimeZone(Constants.DATE_TIMEZONE));
                    Object value = reader.getPrimitiveObject(itemName);
                    if (value instanceof String) {
                        method.invoke(result, format.parse(value.toString()));
                    }
                }
                else if (List.class.isAssignableFrom(typeClass)) {
                    Type fieldType = field.getGenericType();
                    if (fieldType instanceof ParameterizedType) {
                        ParameterizedType paramType = (ParameterizedType) fieldType;
                        Type[] genericTypes = paramType.getActualTypeArguments();
                        if (genericTypes != null && genericTypes.length > 0) {
                            if (genericTypes[0] instanceof Class<?>) {
                                Class<?> subType = (Class<?>) genericTypes[0];
                                List<?> listObjs = reader.getListObjects(itemName, subType);
                                if (listObjs != null) {
                                    method.invoke(result, listObjs);
                                }
                            }
                        }
                    }
                }
                else {
                    // getMappedOutObject
                    Object obj = null;
                    ReqField reqField = field.getAnnotation(ReqField.class);
                    if (reqField != null && reqField.mappedOut()) {
                        obj = reader.getMappedOutObject(typeClass);
                    }
                    else {
                        obj = reader.getObject(itemName, typeClass);
                    }
                    if (obj != null) {
                        method.invoke(result, obj);
                    }
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> String getFieldKey(Class<T> clazz, Field field) {
        EopBean eopBean = clazz.getAnnotation(EopBean.class);
        ReqField reqField = field.getAnnotation(ReqField.class);

        if (reqField != null) {
            if (reqField.mappedOut()) {
                return field.getName();
            }
            if (!Strings.isEmpty(reqField.tagName())) {
                return reqField.tagName();
            }
        }
        if (eopBean != null) {
            Class<? extends IPropMapping> mappingClazz = eopBean.propMapping();
            try {
                return mappingClazz.newInstance().convert(field.getName());
            }
            catch (InstantiationException e) {
            }
            catch (IllegalAccessException e) {
            }
        }
        return null;
    }

}
