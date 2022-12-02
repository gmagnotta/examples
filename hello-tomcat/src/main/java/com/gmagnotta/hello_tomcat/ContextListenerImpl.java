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

public class ContextListenerImpl implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent event) {

    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {

    }

    @Produces
    private BrokerService produce() {
        // https://www.jesperdj.com/2018/09/30/jaxb-on-java-9-10-11-and-beyond/
        // https://stackoverflow.com/questions/43574426/how-to-resolve-java-lang-noclassdeffounderror-javax-xml-bind-jaxbexception
        // https://wiki.eclipse.org/EclipseLink/Examples/MOXy

        final String path = System.getenv("BROKER_URL");

        List<Object> providers = new ArrayList<>();
        providers.add(new JacksonJaxbJsonProvider());
        
        BrokerService proxy = JAXRSClientFactory.create(path, BrokerService.class, providers);

        ClientConfiguration config = WebClient.getConfig(proxy);

        HTTPConduit conduit = (HTTPConduit) config.getConduit();
        conduit.getClient().setConnectionTimeout(1000 * 30);
        conduit.getClient().setReceiveTimeout(1000 * 30);

        return proxy;
    }
}
