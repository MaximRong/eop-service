package com.ailk.ess.g3ess.ejb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.ailk.ecs.esf.base.utils.LocalUtils;

/**
 * EJB路由表。
 *
 * @author wanglei
 *
 * 2011-12-23
 */
public final class EjbRouting {

    private static class Route {
        String provinceCode; // 省分代码
        String zoneCode; // 区域编码
        String remoteClass; // EJB远程类名
        String providerUrl; // 提供者URL
        Properties env; // 环境变量
        Context context; // JNDI上下文环境。
    }

    private static List<Route> routes;

    /**
     * 从ejbrouting.conf中加载EJB路由信息。
     * @throws IOException
     */
    private static void loadConfig() throws IOException {
        ArrayList<Route> routings = new ArrayList<Route>();
        Resource res = new ClassPathResource("appconfig/ess/ejbrouting.conf");
        if (res.exists()) {

            List<String> lines = IOUtils.readLines(res.getInputStream());
            for (String line : lines) {
                line = line.trim();

                if (line.length() == 0 || line.startsWith("#")) { // 忽略空行或者注释
                    continue;
                }

                String[] fields = line.split(",");
                if (fields.length < 3) { // 一行以逗号分隔的字段数目小于5，忽略该行
                    continue;
                }

                Route route = new Route();
                int pos = 0;
                route.provinceCode = fields[pos++].trim();
                route.zoneCode = fields[pos++].trim();
                route.remoteClass = fields[pos++].trim();

                route.providerUrl = fields.length > pos ? fields[pos++].trim() : "";
                createContextProperty(route);

                routings.add(route);
            }
        }

        Route route = new Route();
        routings.add(route);

        route.provinceCode = "*";
        route.zoneCode = "*";
        route.remoteClass = "*";
        route.providerUrl = null;
        route.env = new Properties();

        routes = Collections.unmodifiableList(routings);
    }

    static {
        try {
            loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HashMap<Route, Object> ejbMap = new HashMap<Route, Object>();

    private static void createContextProperty(Route route) {
        Properties env = new Properties();
        if (!StringUtils.isBlank(route.providerUrl)) {
            env.setProperty(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
            env.setProperty(Context.PROVIDER_URL, route.providerUrl);
        }
        route.env = env;
    }

    public static <T> T getRemoteEjb(String provinceCode, Class<T> clazz) {
        Route route = getRoute(provinceCode, provZoneMap.get(provinceCode), clazz);
        createContext(clazz, route);
        return lookupEJB(clazz, route);
    }

    private static Route getRoute(String provinceCode, String zoneCode, Class<?> clazz) {
        for (Route route : routes) {
            if (FilenameUtils.wildcardMatch(provinceCode, route.provinceCode)
                        && FilenameUtils.wildcardMatch(zoneCode, route.zoneCode)
                        && FilenameUtils.wildcardMatch(clazz.getName(), route.remoteClass)) {
                createContextProperty(route);
                return route;
            }
        }

        // 不应该到达此处
        throw new RuntimeException("No routing for EJB call");
    }

    private static <T> T lookupEJB(Class<T> clazz, Route route) {
        Object ejbObject = null;
        //Object ejbObject = ejbMap.get(route);
        //if (ejbObject != null) {
        //    return (T) ejbObject;
        //}

        synchronized (route) {
            //ejbObject = ejbMap.get(route);
            ///if (ejbObject != null) {
            //    return (T) ejbObject;
            //}
            try {
                String ejbName = StringUtils.substringBefore(clazz.getSimpleName(), "Remote");

                //System.out.println("&&&&&&[" + route.context + "]&&&&&");
                ejbObject = route.context.lookup(ejbName + "#" + clazz.getName());
                ejbMap.put(route, ejbObject);
                Map map = new HashMap();
                map.put("Context", route.context);
                LocalUtils.setInputMap(map);
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
        }

        return (T) ejbObject;
    }

    private static <T> void createContext(Class<T> clazz, Route route) {
        //if (route.context != null) {
        //    return;
        //}

        synchronized (route) {
            // if (route.context != null) {
            //     return;
            // }

            try {
                route.context = new InitialContext(route.env);
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final Map<String, String> provZoneMap = new HashMap<String, String>();
    static {
        /**
         * D11北京, E13天津, C18河北, B19山西, E10内蒙古,D91辽宁, B90吉林, A97黑龙江, C31上海, A34江苏, D36浙江, 
         * F30安徽, B38福建, C75江西, A17山东, A76河南, E71湖北, F74湖南, B51广东, C59广西, F50海南, F83重庆, 
         * C81四川, E85贵州, D86云南, F79西藏, C84陕西, A87甘肃, B70青海, B88宁夏, F89新疆, F98联通总部
         */
        String provZoneStr = "A97,A34,A17,A76,A87,B19,B90,B38,B51,B70,B88,C18,C31,C75,C59,C81,C84,D11,D91,D36,D86,E13,E10,E71,E85,F30,F74,F50,F83,F79,F89,F98";
        String[] provZoneArr = provZoneStr.split(",");
        for (String provZone : provZoneArr) {
            String zone = provZone.substring(0, 1);
            String prov = provZone.substring(1);
            provZoneMap.put(prov, zone);
        }
    }

    /**
     * 根据省份编码查找大区编码。
     * @param provCode 省份编码。
     * @return 大区编码。
     */
    public static String getZoneCode(String provCode) {
        String zoneCode = provZoneMap.get(provCode);
        if (StringUtils.isNotEmpty(zoneCode)) {
            return zoneCode;
        }
        throw new RuntimeException("省份编码: " + provCode + "异常!");
    }
}
