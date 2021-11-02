package com.magnotta;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gmagnotta.jaxb.Aggregationtype;

import javax.inject.Inject;

public class OrdersServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Inject
    private ServicesInterface servicesInterface;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Aggregationtype res = servicesInterface.getTopOrders();

        request.setAttribute("orders", res);

        response.setHeader("cache-control", "no-cache");

        getServletContext().getRequestDispatcher("/WEB-INF/topOrders.jsp").forward(request, response);

    }

}