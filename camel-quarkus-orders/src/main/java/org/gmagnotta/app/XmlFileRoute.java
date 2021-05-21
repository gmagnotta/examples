package org.gmagnotta.app;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.bind.JAXBContext;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class XmlFileRoute extends RouteBuilder {

    private static final Logger LOG = Logger.getLogger(XmlFileRoute.class);

    @ConfigProperty(name = "orders.dir")
    String ordersDirectory;

    @Override
    public void configure() throws Exception {

        LOG.info("Reading from " + ordersDirectory);

        JAXBContext jaxbContext = JAXBContext.newInstance("org.gmagnotta.jaxb");
        JaxbDataFormat jaxbDataFormat = new JaxbDataFormat(jaxbContext);
        jaxbDataFormat.setSchema("classpath:order.xsd");

        from("file:" + ordersDirectory + "?moveFailed=.error").routeId("fileRoute").unmarshal(jaxbDataFormat)
                .to("bean://orderprocessor").to("activemq:queue:ordercreated");

    }

}
