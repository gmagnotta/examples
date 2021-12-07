package com.mycompany.app.servlet;
import java.io.IOException;
import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mycompany.app.Cart;
import com.mycompany.app.CartItem;
import com.mycompany.app.service.OrderService;
import com.mycompany.app.service.ShopService;
import com.mycompany.model.LineItem;
import com.mycompany.model.Order;
import com.mycompany.model.Shop;

public class OrdersServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@EJB
	private OrderService orderService;
	
	@EJB
	private ShopService shopService;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String nextURL = "/WEB-INF/thanks.jsp";
		
		if (request.getParameter("addOrder") != null) {
			
			Cart cart = (Cart) request.getSession().getAttribute("cart");
			
			if (cart == null) {
				
				throw new ServletException("invalid cart");
				
			}
			
			
			Shop shop = shopService.getShops().get(0);
			Order order = new Order();
			order.setShop(shop);
			int sum = 0; 
			for (CartItem i : cart.getItems()) {
				
				LineItem line = new LineItem();
				line.setItem(i.getItem());
				line.setOrder(order);
				line.setQuantity(i.getQuantity());
				order.getLineItems().add(line);
				sum += (i.getItem().getPrice().intValue() * i.getQuantity());
				
			}
			
			order.setAmount(new BigDecimal(sum));
			orderService.createOrderOnJms(order);
			
			request.getSession().invalidate();
			
			getServletContext().getRequestDispatcher(nextURL).forward(request,response);
			
		} else if (request.getParameter("cancelOrder") != null) {
		
			nextURL = "/itemcontroller";
		
			request.getSession().invalidate();
			
			response.sendRedirect(getServletContext().getContextPath().concat(nextURL));
			
		}

	}
	
}
