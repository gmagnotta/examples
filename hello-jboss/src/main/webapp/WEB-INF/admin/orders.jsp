<%@ page import="org.gmagnotta.model.*" language="java" %>
<%@ page import="org.gmagnotta.app.*"%>
<%@ page import="com.mycompany.app.service.*"%>
<%@ page import="com.mycompany.app.servlet.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Order created</title>
	<link rel="stylesheet" href="<c:url value='/styles/main.css'/>">
</head>
<body>
			
			<c:choose>
		
		 <c:when test="${orders != null}">
		 
		   <table>
			 <tr>
			   <th>Id</th>
			   <th>Amount</th>
			   <th>Content</th>
			   <th>Creation Date</th>
			   <th>User</th>
			 </tr>
		   
		   <c:forEach var="order" items="${orders}">
			<tr>
				<td>${order.id}</td>
				<td>${order.amount}</td>
				<td>
				<c:forEach var="item" items="${order.lineItems}">
					<ul>
					<li>${item.quantity} x ${item.item.description}</li>
					</ul>
				</c:forEach>
				<td>${order.creationDate}</td>
				<td>${order.user}</td>
				</td>
			</tr>
			</c:forEach>
		   
		   </table>
		 
		 </c:when>
		 
		  <c:otherwise>
		 	<span>No orders</span>
		 </c:otherwise>
		</c:choose>
			
</body>
</html>