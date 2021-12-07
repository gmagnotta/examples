package com.mycompany.app.servlet;
import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mycompany.app.service.OrderService;
import com.mycompany.app.service.ShopService;
import com.mycompany.model.Order;

public class AdminOrdersServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@EJB
	private OrderService orderService;
	
	@EJB
	private ShopService shopService;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String shop = request.getParameter("shop");
		
		List<Order> orders = null;
		
		if (shop != null) {
			orders = orderService.getOrders(Integer.valueOf(shop));
		} else {
			orders = orderService.getOrders();
		}
		
		request.getSession().setAttribute("orders", orders);
		
		String nextURL = "/WEB-INF/admin/orders.jsp";
		
		getServletContext().getRequestDispatcher(nextURL).forward(request,response);
	}
	
	
}
