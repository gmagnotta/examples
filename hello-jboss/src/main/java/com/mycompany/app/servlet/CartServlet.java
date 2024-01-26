package com.mycompany.app.servlet;
import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mycompany.app.Cart;
import com.mycompany.app.service.ItemService;
import com.mycompany.model.Item;

public class CartServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@EJB
	private ItemService itemService;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String nextURL = "/WEB-INF/cart.jsp";
		
		getServletContext().getRequestDispatcher(nextURL).forward(request,response);
	}
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Cart cart = (Cart) request.getSession().getAttribute("cart");
		if (cart == null) {
			cart = new Cart();
			request.getSession().setAttribute("cart", cart);
		}
		
		if (request.getParameter("addItem") != null) {
			
			String id = request.getParameter("itemId");
			String quantity = request.getParameter("quantity");
			
			Item item = itemService.getItemById(Integer.valueOf(id));
			
			if (item == null) {
				throw new ServletException("Invalid item with id " + id);
			}
			
			cart.addToCart(item, Integer.valueOf(quantity));

			String nextURL = "/WEB-INF/cart.jsp";

			getServletContext().getRequestDispatcher(nextURL).forward(request,response);

		} else if (request.getParameter("editQuantity") != null) {
			
			String id = request.getParameter("itemId");
			
			String quantity = request.getParameter("quantity");
			
			Item item = itemService.getItemById(Integer.valueOf(id));
			
			if (item == null) {
				throw new ServletException("Invalid item with id " + id);
			}
			
			if (Integer.valueOf(quantity) == 0) {
				cart.deleteFromCart(item);
			} else {
				cart.updateItemQuantity(item, Integer.valueOf(quantity));
			}
			
			if (cart.getItems().size() == 0) {
				request.getSession().removeAttribute("cart");
			}

			String nextURL = "/WEB-INF/cart.jsp";

			getServletContext().getRequestDispatcher(nextURL).forward(request,response);
			
		} else if (request.getParameter("emptyCart") != null) {

			String nextURL = "/itemcontroller";

			request.getSession().invalidate();

			response.sendRedirect(getServletContext().getContextPath().concat(nextURL));

		}

	}
	
}
