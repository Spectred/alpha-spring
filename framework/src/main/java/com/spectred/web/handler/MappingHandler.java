package com.spectred.web.handler;

import com.spectred.beans.BeanFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MappingHandler {

    private String uri;

    private Method method;

    private Class<?> controller;

    private String[] args;

    public MappingHandler(String uri, Method method, Class<?> controller, String[] args) {
        this.uri = uri;
        this.method = method;
        this.controller = controller;
        this.args = args;
    }


    public boolean handle(ServletRequest request, ServletResponse response) throws Exception {
        String requestUri = ((HttpServletRequest) request).getRequestURI();
        if (!uri.equals(requestUri)) {
            return false;
        }
        Object[] parameters = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            parameters[i] = request.getParameter(args[i]);
        }

        Object ctl = BeanFactory.getBean(controller);
        Object invoke = method.invoke(ctl, parameters);
        response.getWriter().println(invoke.toString());
        return true;
    }
}
