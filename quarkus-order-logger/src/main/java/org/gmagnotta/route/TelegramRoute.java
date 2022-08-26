package org.gmagnotta.route;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.gmagnotta.converter.OrderStringConverter;
import org.gmagnotta.model.event.OrderOuterClass.Order;

@ApplicationScoped
public class TelegramRoute extends RouteBuilder {

    @ConfigProperty(name = "telegram.token")
	String telegramToken;

	@ConfigProperty(name = "telegram.chatid")
	String telegramChatId;

    @ConfigProperty(name = "kafka.broker")
    String kafkaBroker;

    @Inject
    CamelContext camelContext;

    @Override
    public void configure() throws Exception {

        KafkaComponent kafka = new KafkaComponent();
        kafka.getConfiguration().setBrokers(kafkaBroker);
        kafka.getConfiguration().setValueDeserializer("org.gmagnotta.deserializer.OrderDeserializer");
        kafka.getConfiguration().setAutoOffsetReset("earliest");
        kafka.getConfiguration().setGroupId("quarkus-order-logger");

        camelContext.addComponent("kafka", kafka);

        getContext().getTypeConverterRegistry().addTypeConverter(String.class, Order.class, new OrderStringConverter());

        from("kafka:outbox.event.Order")
            .log("Message received from Kafka!")
            .convertBodyTo(String.class)
            .to("direct:telegram");

        from("direct:telegram")
         .log("${body}")
		 .setHeader(org.apache.camel.component.telegram.TelegramConstants.TELEGRAM_CHAT_ID, constant(telegramChatId))
		 .to("telegram:bots?authorizationToken=" + telegramToken);
    }
    
}
