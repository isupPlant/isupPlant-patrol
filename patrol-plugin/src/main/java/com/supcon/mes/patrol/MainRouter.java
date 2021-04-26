package com.supcon.mes.patrol;

import com.supcon.common.com_router.api.IRouter;
import com.supcon.common.com_router.util.RouterManager;
import com.supcon.common.view.util.LogUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangshizhan on 2021/3/25
 * Email:wangshizhan@supcom.com
 */
public class MainRouter implements IRouter {

    public static final String[] MODULES = {
            "module_xj","module_xj_temp"
    };



    public static void setup(){
        List<String> classPaths = new ArrayList<>();
        for (String module : MODULES) {
            classPaths.add("com.supcon.mes." + module + ".IntentRouter");
//            classPaths.add("com.supcon.mes." + module + ".WidgetWapper");
        }

        RouterManager routerManager = RouterManager.getInstance();

        for (String module : classPaths) {

            try {
                Class clazz = Class.forName(module);

                Method method = clazz.getMethod("getRoutes", new Class[]{});
                Map<String,Class<?>> routes = (Map<String, Class<?>>) method.invoke(null);
                routerManager.register(routes);

            } catch (Exception e) {
                LogUtil.e(e.getMessage());
            }

        }
    }

    public static Map<String, Class<?>> getRoutes(){
        List<String> classPaths = new ArrayList<>();
        Map<String, Class<?>> routers = new HashMap<>();
        for (String module : MODULES) {
            classPaths.add("com.supcon.mes." + module + ".IntentRouter");
//            classPaths.add("com.supcon.mes." + module + ".WidgetWapper");
        }


        for (String module : classPaths) {

            try {
                Class clazz = Class.forName(module);

                Method method = clazz.getMethod("getRoutes", new Class[]{});
                Map<String,Class<?>> routes = (Map<String, Class<?>>) method.invoke(null);
                routers.putAll(routes);

            } catch (Exception e) {
                LogUtil.e(e.getMessage());
            }

        }

        return routers;
    }
}
