package com.mycompany.app.servlet;
import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mycompany.app.service.ItemService;
import com.mycompany.model.Item;

public class ItemDetailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@EJB
	private ItemService itemService;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String itemId = request.getParameter("itemId");
		Item item;
		
		if (itemId != null) {
			item = itemService.getItem(Integer.valueOf(itemId));
		} else {
			throw new ServletException("Itemid " + itemId + "not found!");
		}
		
		request.setAttribute("item", item);
		
		String nextURL = "/WEB-INF/itemdetail.jsp";
		
		getServletContext().getRequestDispatcher(nextURL).forward(request,response);
		
	}
	
}
