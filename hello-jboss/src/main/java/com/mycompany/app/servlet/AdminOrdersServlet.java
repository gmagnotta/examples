package com.mycompany.app.servlet;
import java.io.IOException;
import java.util.List;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.mycompany.app.service.OrderService;
import com.mycompany.model.Order;

public class AdminOrdersServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@EJB
	private OrderService orderService;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		List<Order> orders = orderService.getOrders();
		
		request.getSession().setAttribute("orders", orders);
		
		String nextURL = "/WEB-INF/admin/orders.jsp";
		
		getServletContext().getRequestDispatcher(nextURL).forward(request,response);
	}
	
	
}
