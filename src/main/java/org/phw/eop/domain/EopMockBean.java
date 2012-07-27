package org.phw.eop.domain;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bsh.EvalError;
import bsh.Interpreter;

public class EopMockBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String appid, actionid, expr, rsp;
    private int pri;
    // private transient CompiledScript exprScript;
    private static transient Logger logger = LoggerFactory.getLogger(EopMockBean.class);

    @Override
    public String toString() {
        return "EopMockBean [appid=" + appid + ", actionid=" + actionid + ", expr=" + expr + ", rsp="
                + rsp + ", pri=" + pri + "]";
    }

    public String getAppid() {
        return appid;
    }

    public boolean matches(Map<String, Object> parameters) {
        Interpreter interpreter = new Interpreter();
        // System.out.println(JSON.toJSONString(parameters));
        // System.out.println(expr);

        try {
            for (Entry<String, Object> entry : parameters.entrySet()) {
                interpreter.set(entry.getKey(), entry.getValue());
            }
            return (Boolean) interpreter.eval(expr);
        }
        catch (EvalError e) {
            logger.warn("eval failed", e);
        }

        /*
         * ScriptContext scriptContext = new SimpleScriptContext(); Bindings
         * engineScope = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
         * engineScope.putAll(parameters); try { return (Boolean)
         * exprScript.eval(engineScope); } catch (ScriptException e) {
         * logger.warn("eval failed", e); }
         */

        return false;
    }

    public String getActionid() {
        return actionid;
    }

    public String getExpr() {
        return expr;
    }

    public String getRsp() {
        return rsp;
    }

    public int getPri() {
        return pri;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public void setActionid(String actionid) {
        this.actionid = actionid;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public void setRsp(String rsp) {
        this.rsp = rsp;
    }

    public void setPri(int pri) {
        this.pri = pri;
    }

}
