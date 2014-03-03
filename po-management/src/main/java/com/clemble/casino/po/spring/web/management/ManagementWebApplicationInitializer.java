package com.clemble.casino.po.spring.web.management;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.clemble.casino.server.spring.web.AbstractWebApplicationInitializer;

public class ManagementWebApplicationInitializer extends AbstractWebApplicationInitializer {

    @Override
    protected void doInit(ServletContext container) throws ServletException {
        // Step 1. Create the 'root' Spring application context
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(PoManagementWebSpringConfiguration.class);
        // Step 2. Registering appropriate Dispatcher
        ServletRegistration.Dynamic dispatcher = container.addServlet("po-management", new DispatcherServlet(rootContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }

}
