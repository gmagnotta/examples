<%@ page import="org.gmagnotta.model.*" language="java" %>
<%@ page import="org.gmagnotta.app.*"%>
<%@ page import="com.mycompany.app.service.*"%>
<%@ page import="com.mycompany.app.servlet.*"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Item detail</title>
</head>
<body>
	<div id="body">
		
				<span>${item.description}</span>
				<span>${item.price}</span>
			
				<form action="cartcontroller" method="post">
	
					Quantity <input type="text" name="quantity" value="1" />
					<input type="hidden" name="itemId" value="${item.id}" />
					
					<input type="submit" name="addItem" value="Add To Cart" />
				
				</form>
				
	</div>
</body>
</html>