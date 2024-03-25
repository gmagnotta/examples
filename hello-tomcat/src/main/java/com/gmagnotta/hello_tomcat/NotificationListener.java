package com.gmagnotta.hello_tomcat;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.AsyncContext;

@ApplicationScoped
public class NotificationListener {

    private static final Logger LOGGER = Logger.getLogger(NotificationListener.class.getName());

    List<AsyncContext> listeners = new ArrayList<>();

    public void addListener(AsyncContext l) {
        listeners.add(l);
    }

    public void removeListener(AsyncContext l) {
        listeners.remove(l);
    }

    public void notify(String event) {

        for (AsyncContext asyncContext : listeners) {

            try {
                PrintWriter writer = asyncContext.getResponse().getWriter();
                writer.write("data: " + event + "\n\n");
                writer.flush();
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Exception", ex);
            }

        }
    }

}