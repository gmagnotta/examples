package com.gmagnotta.hello_tomcat;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextListenerImpl implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        while (true) {
            ;
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {

    }
}
