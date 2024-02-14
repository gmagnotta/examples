<%@ page import="org.gmagnotta.model.*" language="java" %>
<%@ page import="org.gmagnotta.app.*"%>
<%@ page import="com.mycompany.app.service.*"%>
<%@ page import="com.mycompany.app.servlet.*"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Cart</title>
	<link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
	<div id="body">
		<div id="content">
		
		<c:choose>
		
		 <c:when test="${cart != null}">
		 
		   <table>
			 <tr>
			   <th>Description</th>
			   <th>Quantity</th>
			 </tr>
		   
		   <c:forEach var="item" items="${cart.items}">
			<tr>
				<td>${item.item.description}</td>
				<td><form action="cartcontroller" method="post">
					<input type="text" name="quantity" value="${item.quantity}" />
					<input type="hidden" name="itemId" value="${item.item.id}" />
					<input type="submit" name="editQuantity" value="Update Quantity" />
					</form>
				</td>
			</tr>
			</c:forEach>
		   
		   </table>
		   
		   <p>Total is: ${cart.total}</p>
		 
		    <form action="ordercontroller" method="post">
	
				<input type="submit" name="addOrder" value="Place Order" />
			
			</form>

			<form action="cartcontroller" method="post">

				<input type="submit" name="emptyCart" value="Empty Cart" />

			</form>
		 
		 </c:when>
		 
		 <c:otherwise>
		 	<span>The cart is empty!</span>
		 </c:otherwise>
		</c:choose>
		
		</div>
	</div>
</body>
</html>