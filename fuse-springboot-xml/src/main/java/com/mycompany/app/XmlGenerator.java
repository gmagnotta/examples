package com.mycompany.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Random;

public class XmlGenerator {

	public static final String FILE_TEMPLATE =

			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<shiporder orderid=\"%s\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"shiporder.xsd\">"
			 + "<orderperson>%s</orderperson>" 
			 + "<shipto>"
			  + "<name>Ola Nordmann</name>"
			  + "<address>Langgt 23</address>"
			  + "<city>4000 Stavanger</city>"
			  + "<country>Norway</country>"
			 + "</shipto>"
			  + "<item>"
			   + "<title>Empire Burlesque</title>"
			   + "<note>Special Edition</note>"
			   + "<quantity>%d</quantity>"
			   + "<price>10.90</price>"
			  + "</item>"
			 + "<item>"
			  + "<title>Hide your heart</title>"
			  + "<quantity>%d</quantity>"
			  + "<price>9.90</price>"
			 + "</item>"
			+ "</shiporder>";
	
	public String outputDir;
	
	public XmlGenerator(String outputDir) {
		this.outputDir = outputDir;
	}

	private static int getRandomNumberInRange(int min, int max) {

		Random r = new Random();
		return r.ints(min, (max + 1)).limit(1).findFirst().getAsInt();

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
		
		for (int i = 0; i < iterations; i++) {
			
			String generatedString = generate(6);
			
			PrintStream p = new PrintStream(new FileOutputStream(new File(outputDir, generatedString)));
			p.print(String.format(FILE_TEMPLATE, generatedString, generatedString, getRandomNumberInRange(1, 10), getRandomNumberInRange(1, 10)));
			
			System.out.println("Generated test file " + generatedString);
			
		}
		
	}
}
