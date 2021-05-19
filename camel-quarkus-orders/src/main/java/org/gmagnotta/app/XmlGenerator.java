package org.gmagnotta.app;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.gmagnotta.jaxb.Lineitemtype;
import org.gmagnotta.jaxb.ObjectFactory;
import org.gmagnotta.jaxb.Ordertype;

public class XmlGenerator {

    public static String generate(int targetStringLength) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 58; // letter '9'
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

        return generatedString;
    }

    public void generateFile(int iterations, String outputDir) throws Exception {

        System.out.println("Generating " + iterations + " files in " + outputDir);

        Random r = new Random();

        JAXBContext jaxbContext = JAXBContext.newInstance(Ordertype.class);
        Marshaller mar = jaxbContext.createMarshaller();

        for (int i = 0; i < iterations; i++) {

            String generatedString = generate(6);

            Ordertype ordertype = new Ordertype();
            ordertype.setOrderid(generatedString);

            int random = r.nextInt(5);

            Lineitemtype lineItemtype = new Lineitemtype();
            lineItemtype.setItemid(String.valueOf(random));
            lineItemtype.setNote("none");
            lineItemtype.setQuantity(new BigInteger("1"));
            lineItemtype.setPrice(new BigDecimal(random));

            ordertype.getLineitem().add(lineItemtype);

            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            mar.marshal(new ObjectFactory().createOrder(ordertype), new File(outputDir, generatedString));

        }

    }
}
