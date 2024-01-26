package org.gmagnotta.app.processor;

import java.io.File;
import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.gmagnotta.jaxb.Lineitemtype;
import org.gmagnotta.jaxb.ObjectFactory;
import org.gmagnotta.jaxb.Ordertype;
import org.jboss.logging.Logger;

@ApplicationScoped
@Named("generateprocessor")
/**
 * This class is a Camel Processor used to generate random Orders as incoming xml.
 * It simulates receiving incoming orders.
 */
public class GenerateProcessor implements Processor {
	
	private static final Logger LOG = Logger.getLogger(GenerateProcessor.class);

	@ConfigProperty(name = "orders.dir")
    String ordersDirectory;
	
	private Random random = new Random();
    
    public void process(Exchange exchange) throws Exception {

    	String amount = (String) exchange.getIn().getHeader("amount");
    	
    	generateFile(Integer.valueOf(amount), ordersDirectory);
    	
    }
    
    private void generateFile(int iterations, String outputDir) throws Exception {

        LOG.info("Generating " + iterations + " orders in " + outputDir);

        JAXBContext jaxbContext = JAXBContext.newInstance(Ordertype.class);
        Marshaller mar = jaxbContext.createMarshaller();

        for (int i = 0; i < iterations; i++) {

        	UUID uuid = UUID.randomUUID();

            Ordertype ordertype = new Ordertype();
            ordertype.setOrderid(uuid.toString());

            int randomLineItems = random.nextInt(10 - 1) + 1;
            
            for (int j = 0; j < randomLineItems; j++) {
            
            	int randomid = ThreadLocalRandom.current().nextInt(1, 5);
            	int randomqty = random.nextInt(100 - 1) + 1;

            	Lineitemtype lineItemtype = new Lineitemtype();
	            lineItemtype.setItemid(String.valueOf(randomid));
	            lineItemtype.setNote("none");
	            lineItemtype.setQuantity(BigInteger.valueOf(randomqty));
	
	            ordertype.getLineitem().add(lineItemtype);
            
            }

            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            mar.marshal(new ObjectFactory().createOrder(ordertype), new File(outputDir, uuid.toString()));

        }
        
    }

}
