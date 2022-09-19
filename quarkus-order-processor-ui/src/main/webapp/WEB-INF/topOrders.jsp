<%@ page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <title>Order Management System</title>
            <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
            <link rel="stylesheet" href="https://code.getmdl.io/1.3.0/material.cyan-light_blue.min.css">
            <script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>
        </head>

        <body>

            <!-- Always shows a header, even in smaller screens. -->
            <div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
                <header class="mdl-layout__header">
                    <div class="mdl-layout__header-row">
                        <!-- Title -->
                        <span class="mdl-layout-title">Quarkus Order Processor UI</span>
                        <!-- Add spacer, to align navigation to the right -->
                        <div class="mdl-layout-spacer"></div>
                    </div>
                </header>
                <div class="mdl-layout__drawer">
                    <span class="mdl-layout-title">Operations</span>
                    <nav class="mdl-navigation">
                        <a class="mdl-navigation__link" href="topOrders">Top Orders</a>
                        <a class="mdl-navigation__link" href="topItems">Top Items</a>
                    </nav>
                </div>
                <main class="mdl-layout__content">
                    <div class="page-content">
                        <div class="mdl-grid">
                            <!-- Your content goes here -->
                            <div>This is the list of orders with most quantity ordered coming from ${orders.status}
                            </div>

                        </div>
                        <div class="mdl-grid">
                            <div>
                                <!-- <style>
                                    .demo-list-item {
                                        width: 300px;
                                    }
                                </style>-->
                                <table class="mdl-data-table mdl-js-data-table mdl-shadow--2dp">
                                    <thead>
                                        <tr>
                                            <th>Order ID</th>
                                            <th>Items</th>
                                        </tr>
                                    </thead>

                                    <tbody>
                                        <c:forEach items="${orders.topvalue}" var="item">
                                            <tr>
                                                <td>${item.id}</td>
                                                <td>${item.value}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>

                                </table>
                            </div>
                        </div>
                    </div>
                </main>
            </div>

        </body>

        </html>