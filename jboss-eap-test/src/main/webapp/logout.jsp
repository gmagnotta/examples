<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Giuseppe's Online Store</title>
</head>
<body>
	<div>
		<h1>Welcome to Giuseppe's Online Store!</h1>
		<a href="<%= System.getProperty("SSO_URL") %>/realms/<%= System.getProperty("SSO_REALM") %>/protocol/openid-connect/logout">Click here to logout</a>
	</div>
</body>
</html>