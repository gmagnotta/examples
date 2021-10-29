package com.magnotta;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.gmagnotta.jaxb.Aggregationtype;

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

        ServicesInterface proxy = JAXRSClientFactory.create(path, ServicesInterface.class);

        ClientConfiguration config = WebClient.getConfig(proxy);

        HTTPConduit conduit = (HTTPConduit)config.getConduit();
        conduit.getClient().setConnectionTimeout(1000 * 30);
        conduit.getClient().setReceiveTimeout(1000 * 30);

        Aggregationtype res = proxy.getTopOrders();

        request.setAttribute("orders", res);

        getServletContext().getRequestDispatcher("/WEB-INF/topOrders.jsp").forward(request, response);

    }

}