<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <title>Order Management System</title>
        </head>

        <body>
            <p>This is the list of orders with most quantity ordered</p>

            <table>
                <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>Items</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach items="${orders.aggregatedorder}" var="item">
                        <tr>
                            <td>${item.orderid}</td>
                            <td>${item.items}</td>
                        </tr>
                    </c:forEach>
                </tbody>

            </table>

        </body>

        </html>