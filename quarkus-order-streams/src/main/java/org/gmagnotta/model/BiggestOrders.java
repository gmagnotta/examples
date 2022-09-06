package org.gmagnotta.model;

import java.util.Iterator;
import java.util.TreeSet;

import org.gmagnotta.OrderComparator;
import org.gmagnotta.model.event.OrderOuterClass.Order;

/**
 * Class that acts as a priority queue but has a fixed size.
 * When the maximum number of elements is reached the lowest/highest element
 * will be removed.
 */
public class BiggestOrders {

    private TreeSet<Order> inner;
    private int maxSize;


    public BiggestOrders(int maxSize) {
        this.inner = new TreeSet<>(new OrderComparator());
        this.maxSize = maxSize;
    }


    public BiggestOrders add(Order element) {
        inner.add(element);
        if (inner.size() > maxSize) {
            inner.pollLast();
        }
        return this;
    }

    public BiggestOrders remove(Order element) {
        if (inner.contains(element)) {
            inner.remove(element);
        }
        return this;
    }

    public Iterator<Order> iterator() {
        return inner.iterator();
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("BiggestOrders{");

        Iterator<Order> iterator = inner.iterator();
        while (iterator.hasNext()) {
            Order o = iterator.next();
            stringBuffer.append(o.getId() + " ,");
        }
        stringBuffer.append("}");
        return stringBuffer.toString();

    }

}