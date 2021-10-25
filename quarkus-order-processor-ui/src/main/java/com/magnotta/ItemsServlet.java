package com.magnotta;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;

import org.gmagnotta.jaxb.Aggregationtype;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

public class ItemsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        final String path = System.getenv("BACKEND_URL");
        
        getServletContext().log("Using backend " + path);

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath(path));

        ServicesInterface proxy = target.proxy(ServicesInterface.class);

        Aggregationtype res = proxy.getTopItems();

        client.close();

        request.setAttribute("items", res);

        getServletContext().getRequestDispatcher("/WEB-INF/topItems.jsp").forward(request, response);

    }

}