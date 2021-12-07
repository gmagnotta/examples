package com.mycompany.app.servlet;
import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mycompany.app.service.ShopService;
import com.mycompany.model.Shop;

@WebServlet(urlPatterns = "/shops", name = "shopServlet")
public class ShopsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@EJB
	private ShopService shopService;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String action = request.getParameter("action");
		
		if ("addShop".equals(action)) {
			
			String description = request.getParameter("description");
			String address = request.getParameter("address");
			
			Shop shop = new Shop();
			shop.setDescription(description);
			shop.setAddress(address);
			
			shopService.createShop(shop);
			
		} else if ("deleteShop".equals(action)) {
			
			String id = request.getParameter("id");
			
			shopService.deleteShop(Integer.valueOf(id));
			
		}
		
		String nextURL = "/shops.jsp";
		
		//getServletContext().getRequestDispatcher(nextURL).forward(request,response);
		response.sendRedirect(request.getContextPath() + nextURL);
		
	}
	
}
