package com.spectred.web.handler;

import com.spectred.web.mvc.Controller;
import com.spectred.web.mvc.RequestMapping;
import com.spectred.web.mvc.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class HandlerManager {

    public static List<MappingHandler> MAPPING_HANDLER_LIST = new ArrayList<>();

    public static void resolveMappingHandler(List<Class<?>> classList) {
        classList.stream()
                .filter(clazz -> clazz.isAnnotationPresent(Controller.class))
                .forEach(HandlerManager::parseHandlerFromController);
    }

    private static void parseHandlerFromController(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (!method.isAnnotationPresent(RequestMapping.class)) {
                continue;
            }
            String uri = method.getDeclaredAnnotation(RequestMapping.class).value();

            List<String> paramNameList = new ArrayList<>();
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {
                if (parameter.isAnnotationPresent(RequestParam.class)) {
                    String paramName = parameter.getDeclaredAnnotation(RequestParam.class).value();
                    paramNameList.add(paramName);
                }
            }
            String[] params = paramNameList.toArray(new String[paramNameList.size()]);

            MappingHandler mappingHandler = new MappingHandler(uri, method, clazz, params);
            MAPPING_HANDLER_LIST.add(mappingHandler);
        }
    }

}
