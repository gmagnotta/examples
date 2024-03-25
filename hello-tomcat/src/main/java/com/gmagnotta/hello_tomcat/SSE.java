package com.gmagnotta.hello_tomcat;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

import jakarta.inject.Inject;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//  https://gist.github.com/chRyNaN/59a525276e7b148b1ce6

// https://stackoverflow.com/questions/31768349/how-does-server-sent-events-work

@WebServlet(urlPatterns = "/sse", asyncSupported = true)
public class SSE extends HttpServlet {

    @Inject
    NotificationListener listener;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        // initialize for server-sent events
        response.setContentType("text/event-stream");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");

        // to clear threads and allow for asynchronous execution
        final AsyncContext asyncContext = request.startAsync(request, response);
        asyncContext.setTimeout(60 * 60 * 1000);

        listener.addListener(asyncContext);

        asyncContext.addListener(new AsyncListener() {

            private Logger LOGGER = Logger.getLogger(this.getClass().getName());

            @Override
            public void onComplete(AsyncEvent event) throws IOException {
                LOGGER.log(Level.INFO, "onComplete", event.getThrowable());
                listener.removeListener(asyncContext);
            }

            @Override
            public void onTimeout(AsyncEvent event) throws IOException {
                LOGGER.log(Level.INFO, "onTimeout", event.getThrowable());
                listener.removeListener(asyncContext);
            }

            @Override
            public void onError(AsyncEvent event) throws IOException {
                LOGGER.log(Level.INFO, "onError", event.getThrowable());
                listener.removeListener(asyncContext);
            }

            @Override
            public void onStartAsync(AsyncEvent event) throws IOException {
                LOGGER.log(Level.INFO, "onStartAsync", event.getThrowable());
            }

        });

    }

}