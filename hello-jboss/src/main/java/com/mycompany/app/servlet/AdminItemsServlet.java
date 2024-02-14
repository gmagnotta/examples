package com.mycompany.app.servlet;
import java.io.IOException;
import java.math.BigDecimal;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.mycompany.app.service.ItemService;
import com.mycompany.model.Item;

public class AdminItemsServlet extends ItemsServlet {

	private static final long serialVersionUID = 1L;
	
	@EJB
	private ItemService itemService;
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if (request.getParameter("createItem") != null) {
			
			String description = request.getParameter("description");
			String price = request.getParameter("price");
			
			Item item = new Item();
			item.setDescription(description);
			item.setPrice(new BigDecimal(price));
			
			itemService.createItem(item);
			
		} else if (request.getParameter("editItem") != null) {
			
			String description = request.getParameter("description");
			String price = request.getParameter("price");
			String itemId = request.getParameter("itemId");
			
			Item item = itemService.getItemById(Integer.valueOf(itemId));
			
			item.setDescription(description);
			item.setPrice(new BigDecimal(price));
			
			itemService.updateItem(item);
			
		} else if (request.getParameter("deleteItem") != null) {
			
			String itemId = request.getParameter("itemId");
			
			Item item = itemService.getItemById(Integer.valueOf(itemId));
			
			itemService.deleteItem(item);
		}
		
		String nextURL = "/admin";
		
		response.sendRedirect(getServletContext().getContextPath().concat(nextURL));
		
	}
	
}
