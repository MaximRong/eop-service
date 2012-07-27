package org.phw.eop.support;

import java.util.Map;

import org.phw.eop.utils.Converts;

public abstract class EopAction implements EopActionSupport {
    private Map<String, Object> params;

    public Map<String, Object> getParams() {
        return params;
    }

    public <T> T get(String key) {
        return (T) params.get(key);
    }

    public <T> T get(String key, T def) {
        Object value = params.get(key);
        return value != null ? (T) value : def;
    }

    public <T> T getVar(String key, Class<T> clz) {
        Object value = params.get(key);
        if (value == null) {
            return null;
        }

        return (T) Converts.convert("" + value, clz);
    }

    public <T> T getVar(String key, T def) {
        Object value = params.get(key);
        if (value == null) {
            return def;
        }

        return (T) Converts.convert("" + value, def.getClass());
    }

    public String getStr(String key) {
        return (String) params.get(key);
    }

    public String getStr(String key, String def) {
        Object value = params.get(key);
        return value != null ? (String) value : def;
    }

    public int getInt(String key, int def) {
        Object value = params.get(key);
        return value != null ? (Integer) value : def;
    }

    public boolean getBool(String key, boolean def) {
        Object value = params.get(key);
        return value != null ? (Boolean) value : def;
    }

    @Override
    public Object doAction(Map<String, Object> params) throws EopActionException {
        this.params = params;
        return doAction();
    }

    public abstract Object doAction() throws EopActionException;

}
