package com.mycompany.app.servlet;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.app.service.OrderService;

public class TopOrdersServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(TopItemsServlet.class);

	@EJB
	private OrderService ordersService;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Map<Integer, Double> topOrders = ordersService.getTopOrders();

		Set<Integer> keys = topOrders.keySet();

		Iterator<Integer> iterator = keys.iterator();
		
		response.setContentType("text/plain");
		
		while (iterator.hasNext()) {
			
			Integer k = iterator.next();
			
			Double value = topOrders.get(k);
			
			response.getOutputStream().println("Order: " + k + " amount: " + value);

		}

		response.flushBuffer();
		
	}
	
}
