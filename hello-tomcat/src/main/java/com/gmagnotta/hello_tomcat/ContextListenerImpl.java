package com.gmagnotta.hello_tomcat;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.gmagnotta.hello_tomcat.service.BrokerService;
import com.gmagnotta.hello_tomcat.service.FileLabellerService;

public class ContextListenerImpl implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {

    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {

    }
}
