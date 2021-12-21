package com.mycompany.app.servlet;
import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		
		String nextURL = "/WEB-INF/items.jsp";
		
		getServletContext().getRequestDispatcher(nextURL).forward(request,response);
		
	}
	
}
