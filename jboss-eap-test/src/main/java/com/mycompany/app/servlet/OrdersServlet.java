package com.mycompany.app.servlet;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mycompany.app.Cart;
import com.mycompany.app.CartItem;
import com.mycompany.app.service.OrderService;
import com.mycompany.model.LineItem;
import com.mycompany.model.Order;

public class OrdersServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@EJB
	private OrderService orderService;
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String nextURL = "/WEB-INF/thanks.jsp";
		
		if (request.getParameter("addOrder") != null) {
			
			Cart cart = (Cart) request.getSession().getAttribute("cart");
			
			if (cart == null) {
				
				throw new ServletException("invalid cart");
				
			}
			
			Order order = new Order();
			BigDecimal sum = BigDecimal.ZERO; 
			for (CartItem cartItem : cart.getItems()) {
				
				LineItem line = new LineItem();
				line.setItem(cartItem.getItem());
				line.setOrder(order);
				order.addLineItem(line);
				line.setQuantity(cartItem.getQuantity());
				line.setPrice(cartItem.getItem().getPrice());
				sum = sum.add(line.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
				
			}
			
			order.setAmount(sum);
			order.setCreationDate(new Date());

			orderService.createOrder(order);
			
			request.getSession().invalidate();
			
			getServletContext().getRequestDispatcher(nextURL).forward(request,response);
			
		} else if (request.getParameter("cancelOrder") != null) {
		
			nextURL = "/itemcontroller";
		
			request.getSession().invalidate();
			
			response.sendRedirect(getServletContext().getContextPath().concat(nextURL));
			
		}

	}
	
}
