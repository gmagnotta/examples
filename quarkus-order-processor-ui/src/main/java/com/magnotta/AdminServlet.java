package com.magnotta;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.inject.Inject;

public class AdminServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Inject
    private ServicesInterface servicesInterface;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("reset".equals(action)) {

            servicesInterface.doReset();

        } else if ("rebuild".equals(action)) {

            servicesInterface.rebuild();

        } else if ("generate".equals(action)) {

            String amount = request.getParameter("amount");

            servicesInterface.generate(Integer.valueOf(amount));
        }

        response.sendRedirect("/admin.html");

    }

}