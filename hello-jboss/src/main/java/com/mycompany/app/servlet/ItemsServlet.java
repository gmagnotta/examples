package com.mycompany.app.servlet;
import java.io.IOException;
import java.util.List;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.mycompany.app.service.ItemService;
import com.mycompany.model.Item;

public class ItemsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@EJB
	private ItemService itemService;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String description = request.getParameter("description");
		List<Item> items;
		
		if (description != null) {
			items = itemService.getItemsByDescription(description);
		} else {
			items = itemService.getAllItems();
		}
		
		request.setAttribute("items", items);

		response.setHeader("cache-control", "no-cache");
		
		String nextURL = "/WEB-INF/items.jsp";
		
		getServletContext().getRequestDispatcher(nextURL).forward(request,response);
		
	}
	
}
