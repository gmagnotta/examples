package org.gmagnotta.route;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class TelegramRoute extends RouteBuilder {

    @ConfigProperty(name = "telegram.token")
	String telegramToken;

	@ConfigProperty(name = "telegram.chatid")
	String telegramChatId;

    @Override
    public void configure() throws Exception {

        from("direct:telegram")
         .log("${body}")
		 .setHeader(org.apache.camel.component.telegram.TelegramConstants.TELEGRAM_CHAT_ID, constant(telegramChatId))
		 .setBody(simple("${body}"))
		 .to("telegram:bots?authorizationToken=" + telegramToken);
    }
    
}
