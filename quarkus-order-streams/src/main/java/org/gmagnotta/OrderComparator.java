package org.gmagnotta;

import java.util.Comparator;

import org.gmagnotta.model.connect.Order;

public class OrderComparator implements Comparator<Order> {

    @Override
    public int compare(Order order1, Order order2) {

        org.gmagnotta.model.event.OrderOuterClass.BigDecimal order1Amout =
            ProtoUtils.convertToProtobuf(ProtoUtils.bigDecimalFromString(order1.getAmount()));

        java.math.MathContext mc = new java.math.MathContext(order1Amout.getPrecision());
        java.math.BigDecimal order1Value = new java.math.BigDecimal(
                new java.math.BigInteger(order1Amout.getValue().toByteArray()),
                order1Amout.getScale(),
                mc);

        org.gmagnotta.model.event.OrderOuterClass.BigDecimal order2Amout =
            ProtoUtils.convertToProtobuf(ProtoUtils.bigDecimalFromString(order2.getAmount()));

        java.math.MathContext mc2 = new java.math.MathContext(order2Amout.getPrecision());
        java.math.BigDecimal order2Value = new java.math.BigDecimal(
                new java.math.BigInteger(order2Amout.getValue().toByteArray()),
                order2Amout.getScale(),
                mc2);

        int retVal = order2Value.compareTo(order1Value);

        if (retVal == 0) {

            return Long.valueOf(order2.getCreation_date()).compareTo(Long.valueOf(order1.getCreation_date()));

        } else {

            return retVal;

        }
    }

}
