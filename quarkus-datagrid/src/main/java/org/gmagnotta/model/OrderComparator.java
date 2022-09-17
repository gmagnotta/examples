package org.gmagnotta.model;

import java.util.Comparator;

import org.gmagnotta.model.event.OrderOuterClass.Order;

public class OrderComparator implements Comparator<Order> {

    @Override
    public int compare(Order order1, Order order2) {
        org.gmagnotta.model.event.OrderOuterClass.BigDecimal order1Amout = order1.getAmount();

        java.math.MathContext mc = new java.math.MathContext(order1Amout.getPrecision());
        java.math.BigDecimal order1Value = new java.math.BigDecimal(
                new java.math.BigInteger(order1Amout.getValue().toByteArray()),
                order1Amout.getScale(),
                mc);

        org.gmagnotta.model.event.OrderOuterClass.BigDecimal order2Amout = order2.getAmount();

        java.math.MathContext mc2 = new java.math.MathContext(order2Amout.getPrecision());
        java.math.BigDecimal order2Value = new java.math.BigDecimal(
                new java.math.BigInteger(order2Amout.getValue().toByteArray()),
                order2Amout.getScale(),
                mc2);

        return order2Value.compareTo(order1Value);
    }

}
