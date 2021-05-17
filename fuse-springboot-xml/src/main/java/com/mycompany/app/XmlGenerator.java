package com.mycompany.app;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.model.Itemtype;
import com.model.ObjectFactory;
import com.model.Shipordertype;
import com.model.Shiptotype;

public class XmlGenerator {
	
	private static String[] titles = { "Microservices Architecture", "Alice in wonderland", "Enterprise Integration Patterns" };
	private static BigDecimal[] prices = new BigDecimal[] { new BigDecimal("10.90"), new BigDecimal("9.90"), new BigDecimal("20") };

	public String outputDir;
	
	public XmlGenerator(String outputDir) {
		this.outputDir = outputDir;
	}

	public static String generate(int targetStringLength) {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 58; // letter '9'
		Random random = new Random();

		String generatedString = random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

		return generatedString;
	}
	
	public void generateFile(int iterations) throws Exception {
		
		Random r = new Random();
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Shipordertype.class);
		Marshaller mar= jaxbContext.createMarshaller();
	    
		for (int i = 0; i < iterations; i++) {
			
			String generatedString = generate(6);
		    
		    Shipordertype shipordertype = new Shipordertype();
		    shipordertype.setOrderid(generatedString);
		    shipordertype.setOrderperson("Unknown");
		    
		    Shiptotype shiptotype = new Shiptotype();
		    shiptotype.setName("unknown");
		    shiptotype.setAddress("unknown");
		    shiptotype.setCity("unknown");
		    shiptotype.setCountry("unknown");
		    
		    shipordertype.setShipto(shiptotype);
		    
		    int position = r.nextInt(titles.length);
		    
		    Itemtype itemtype = new Itemtype();
		    itemtype.setTitle(titles[position]);
		    itemtype.setNote("");
		    itemtype.setQuantity(new BigInteger("1"));
		    itemtype.setPrice(prices[position]);
		    
		    shipordertype.getItem().add(itemtype);
			
			mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		    mar.marshal(new ObjectFactory().createShiporder(shipordertype), new File(outputDir, generatedString));
		    
		}
		
	}
}
