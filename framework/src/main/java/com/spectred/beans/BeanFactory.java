package com.spectred.beans;

import com.spectred.web.mvc.Controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class BeanFactory {

    private static Map<Class<?>, Object> CLASS_BEAN_MAP = new ConcurrentHashMap<>();


    public static Object getBean(Class<?> clazz) {
        return CLASS_BEAN_MAP.get(clazz);
    }

    public static void initBean(List<Class<?>> classList) throws Exception {
        List<Class<?>> toCreateList = new ArrayList<>(classList);
        while (toCreateList.size() != 0) {
            int remainSize = toCreateList.size();
            for (int i = 0; i < toCreateList.size(); i++) {
                if (finishCreate(toCreateList.get(i))) {
                    toCreateList.remove(i);
                }
            }

            if (remainSize == toCreateList.size()) {
                throw new Exception("循环依赖");
            }
        }
    }

    private static boolean finishCreate(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        if (!clazz.isAnnotationPresent(Bean.class) && !clazz.isAnnotationPresent(Controller.class)) {
            return true;
        }

        Object bean = clazz.newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Class<?> type = field.getType();
                Object reliantBean = BeanFactory.getBean(type);
                if (Objects.isNull(reliantBean)) {
                    return false;
                }

                field.setAccessible(true);
                field.set(bean, reliantBean);
            }
        }

        CLASS_BEAN_MAP.put(clazz, bean);
        return true;
    }
}
