package com.ailk.ess.g3ess.ejb;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJBException;
import javax.naming.Context;
import javax.naming.NamingException;

import com.ailk.ecs.esf.base.utils.LocalUtils;
import com.linkage.base.ejb.IEjbAccessAgent;
import com.linkage.base.util.ClassUtils;

public class EjbCallAgent<T extends IEjbAccessAgent> {
    private String provinceCode;

    public EjbCallAgent(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    /**
     * 取得子类泛型的第一个参数类型。
     * @return 类型
     */
    protected Class<T> getClazz() {
        return ClassUtils.getGenericArgsType(getClass());
    }

    public Map call(String target) {
        return call(target, null);
    }

    public Map call(String target, Map dataMap) {
        IEjbAccessAgent ejb = EjbRouting.getRemoteEjb(provinceCode, getClazz());
        return ejbCall(target, dataMap, ejb);
    }

    protected Map ejbCall(String target, Map dataMap, IEjbAccessAgent ejb) {
        if (dataMap == null) {
            dataMap = new HashMap(3);
        }
        dataMap.put(IEjbAccessAgent.BizServiceTarget, target);
        try {
            return ejb.processBiz(null, dataMap);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("EJB调用失败:" + target, e);
        } finally {
            Map map = LocalUtils.getInputMap();
            if (map != null) {
                Context context = (Context) map.get("Context");
                try {
                    // SessionBean tmp = (SessionBean) ejb;
                    // tmp.ejbRemove();

                    //System.out.println("--------------释放-------------------");
                    context.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                    throw new RuntimeException("EJB关闭失败:" + target, e);
                } catch (EJBException e) {
                    e.printStackTrace();
                    throw new RuntimeException("EJB关闭失败:" + target, e);
                }
            }
        }
    }

}
