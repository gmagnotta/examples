<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <title>Order Management System</title>
        </head>

        <body>
            <p>This is the list of most requested items</p>

            <table>
                <thead>
                    <tr>
                        <th>Item</th>
                        <th>Quantity</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach items="${items.aggregateditem}" var="item">
                        <tr>
                            <td>${item.item}</td>
                            <td>${item.quantity}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

        </body>

        </html>