<%@ page import="org.gmagnotta.model.*" language="java" %>
<%@ page import="org.gmagnotta.app.*"%>
<%@ page import="com.mycompany.app.service.*"%>
<%@ page import="com.mycompany.app.servlet.*"%>
<%@ page import="java.util.List"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Items list</title>
	<link rel="stylesheet" href="styles/main.css">
</head>
<body>
	<table>
	 <tr>
	   <th>Description</th>
	   <th>Price</th>
	 </tr>
	 <c:forEach var="item" items="${items}">
	 <tr>
			<td><a href="itemdetailcontroller?itemId=${item.id}">${item.description}</a></td>
			<td>${item.price}</td>
	 </tr>
	</c:forEach>
	
	</table>

	<p>Search item by description</p>
	<form action="itemcontroller" method="get">
	
		<input type="text" name="description" value="" />
		<input type="submit" name="getItem" value="Search Items" />
	
	</form>
</body>
</html>