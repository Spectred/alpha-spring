package com.spectred.starter;

import com.spectred.beans.BeanFactory;
import com.spectred.core.ClassScanner;
import com.spectred.web.handler.HandlerManager;
import com.spectred.web.server.TomcatServer;


import java.util.List;

public class AlphaApplication {

    public static void run(Class<?> clazz, String[] args) {
        System.out.println("Hello Alpha Spring Framework");
        TomcatServer tomcatServer = new TomcatServer(args);
        try {
            tomcatServer.startServer();

            List<Class<?>> classList = ClassScanner.scanClasses(clazz.getPackage().getName());

            BeanFactory.initBean(classList);
            HandlerManager.resolveMappingHandler(classList);
            classList.forEach(cl -> System.out.println(cl.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
