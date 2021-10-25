<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <!DOCTYPE html>
    <html>

    <head>
        <title>Order Management System</title>
    </head>

    <body>
        <p>This is the list of most requested items</p>

        <table>

            <tr>
                <th>Item</th>
                <th>Quantity</th>
            </tr>

            <c:forEach items="${items.aggregateditem}" var="item">
                <tr>
                    <td>${item.item}</td>
                    <td>${item.quantity}</td>
                </tr>
            </c:forEach>

        </table>

    </body>

    </html>