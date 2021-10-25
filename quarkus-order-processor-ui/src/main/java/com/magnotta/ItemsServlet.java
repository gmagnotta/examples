package com.magnotta;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.gmagnotta.jaxb.Aggregationtype;

public class ItemsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        final String path = System.getenv("BACKEND_URL");

        getServletContext().log("Using backend " + path);

        ServicesInterface proxy = JAXRSClientFactory.create(path, ServicesInterface.class);

        Aggregationtype res = proxy.getTopItems();

        request.setAttribute("items", res);

        getServletContext().getRequestDispatcher("/WEB-INF/topItems.jsp").forward(request, response);

    }

}