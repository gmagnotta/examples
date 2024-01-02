<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Add new Items</title>
</head>
<body>
	<div>
		
		<form action="itemcontroller" method="post">

			<label>description</label>
			<input type="text" name="description" value="" /><br>
			<label>price</label>
			<input type="text" name="price" value="" />
			<input type="submit" name="createItem" value="Add" />
		
		</form>
	</div>
</body>
</html>