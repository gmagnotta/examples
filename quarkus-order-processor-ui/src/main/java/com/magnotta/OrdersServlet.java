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

public class OrdersServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // https://www.jesperdj.com/2018/09/30/jaxb-on-java-9-10-11-and-beyond/
        // https://stackoverflow.com/questions/43574426/how-to-resolve-java-lang-noclassdeffounderror-javax-xml-bind-jaxbexception
        // https://wiki.eclipse.org/EclipseLink/Examples/MOXy
        final String path = System.getenv("BACKEND_URL");

        getServletContext().log("Using backend " + path);

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath(path));

        ServicesInterface proxy = target.proxy(ServicesInterface.class);

        Aggregationtype res = proxy.getTopOrders();

        client.close();

        request.setAttribute("orders", res);

        getServletContext().getRequestDispatcher("/WEB-INF/topOrders.jsp").forward(request, response);

    }

}