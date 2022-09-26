package org.gmagnotta.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Class that acts as a priority queue but has a fixed size.
 * When the maximum number of elements is reached the lowest/highest element
 * will be removed.
 */
public class BiggestOrders {

    private List<Order> orders ;


    public BiggestOrders() {
        this.orders = new ArrayList<Order>();
    }


    public BiggestOrders add(Order element) {
        orders.add(element);
        return this;
    }

    public Iterator<Order> iterator() {
        return orders.iterator();
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("BiggestOrders{");

        Iterator<Order> iterator = orders.iterator();
        while (iterator.hasNext()) {
            Order o = iterator.next();
            stringBuffer.append(o.getId() + " ,");
        }
        stringBuffer.append("}");
        return stringBuffer.toString();

    }

}