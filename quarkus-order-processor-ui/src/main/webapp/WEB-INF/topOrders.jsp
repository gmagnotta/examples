<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <!DOCTYPE html>
    <html>

    <head>
        <title>Order Management System</title>
    </head>

    <body>
        <p>This is the list of orders with most quantity ordered</p>

        <table>

            <tr>
                <th>Order ID</th>
                <th>Items</th>
            </tr>

            <c:forEach items="${orders.aggregatedorder}" var="item">
                <tr>
                    <td>${item.orderid}</td>
                    <td>${item.items}</td>
                </tr>
            </c:forEach>

        </table>

    </body>

    </html>