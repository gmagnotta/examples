package org.gmagnotta.route;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.gmagnotta.converter.OrderStringConverter;
import org.gmagnotta.model.event.OrderOuterClass.Order;

@ApplicationScoped
public class TelegramRoute extends RouteBuilder {

    @ConfigProperty(name = "telegram.token")
    String telegramToken;

    @ConfigProperty(name = "telegram.chatid")
    String telegramChatId;

    @ConfigProperty(name = "telegram.enabled")
    boolean telegramEnabled;

    @Inject
    CamelContext camelContext;

    @Override
    public void configure() throws Exception {

        getContext().getTypeConverterRegistry().addTypeConverter(String.class, Order.class, new OrderStringConverter());

        from("direct:telegram")
                .convertBodyTo(String.class)
                .log("Sending to telegram: ${body}")
                .setHeader(org.apache.camel.component.telegram.TelegramConstants.TELEGRAM_CHAT_ID,
                        constant(telegramChatId))
                .to("telegram:bots?authorizationToken=" + telegramToken);
    }

}
