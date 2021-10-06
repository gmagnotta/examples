package org.gmagnotta.utils;

import java.io.StringWriter;

import javax.ws.rs.core.Request;
import javax.xml.bind.JAXBException;

import org.gmagnotta.jaxb.ObjectFactory;
import org.gmagnotta.jaxb.OrderCommandRequest;
import org.gmagnotta.jaxb.OrderCommandRequestEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TestUtils {

    public static final String emptyCommandRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
    "<ns2:orderCommandRequest xmlns:ns2=\"http://gmagnotta.org/CommandRequestTypes\">" +
    "<orderCommandEnum>ORDER_RECEIVED</orderCommandEnum>" +
    "</ns2:orderCommandRequest>";

    @Test
    public void testMarshall() throws JAXBException {

        OrderCommandRequest request = new OrderCommandRequest();
        request.setOrderCommandEnum(OrderCommandRequestEnum.ORDER_RECEIVED);
        
        StringWriter s = Utils.marshall(new ObjectFactory().createOrderCommandRequest(request));

        Assertions.assertEquals(emptyCommandRequest, s.toString());
    }

    @Test
    public void testUnmarshall() throws JAXBException {
        
        OrderCommandRequest request = new OrderCommandRequest();
        request.setOrderCommandEnum(OrderCommandRequestEnum.ORDER_RECEIVED);
        
        OrderCommandRequest orderCommandRequest = Utils.unmarshall(OrderCommandRequest.class, emptyCommandRequest);

        Assertions.assertEquals(request.getOrderCommandEnum(), orderCommandRequest.getOrderCommandEnum());
        Assertions.assertEquals(request.getParameter(), orderCommandRequest.getParameter());
        Assertions.assertEquals(request.getOrder(), orderCommandRequest.getOrder());
    }
    
}
