package com.gmagnotta;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SqsRoute extends RouteBuilder {

    @ConfigProperty(name = "sqs.queue")
    String sqsQueue;

    @ConfigProperty(name = "k.sink")
    String destination;

    protected class UUIDExpression implements Expression {

        @Override
        public <String> String evaluate(Exchange exchange, Class<String> type) {
            return (String) UUID.randomUUID().toString();
        }

    }

    @Override
    public void configure() throws Exception {
        fromF("aws2-sqs://%s?useDefaultCredentialsProvider=true", sqsQueue)
        .setHeader(Exchange.HTTP_METHOD, constant("POST"))
        .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
        .setHeader("ce-id", new UUIDExpression())
        .setHeader("ce-source", constant("aws:s3"))
        .setHeader("ce-specversion", constant("1.0"))
        .setHeader("ce-type", constant("com.gmagnotta.events/s3upload"))
        .log("Sending ${body}")
        .toF("%s", destination);
    }
    
}
