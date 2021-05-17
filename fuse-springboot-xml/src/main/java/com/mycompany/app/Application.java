package com.mycompany.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {
	
	public static void main(String[] args) throws Exception {
		
		if (args.length == 2 && args[0].equals("generate")) {
			
			int iterations = Integer.valueOf(args[1]);
			
			XmlGenerator xmlGenerator = new XmlGenerator("/tmp/books");
			
			xmlGenerator.generateFile(iterations);
			
		} else {

			ApplicationContext ctx = SpringApplication.run(Application.class, args);

		}
	}
	
}
