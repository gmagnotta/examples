package com.mycompany.event.impl;

import java.util.Date;

import com.mycompany.app.service.utils.OrderUtils;
import com.mycompany.event.ExportedEvent;
import com.mycompany.model.Order;

public class OrderCreatedEvent implements ExportedEvent {

    private final long id;
    private final Order order;
    private final Date timeStamp;

    private OrderCreatedEvent(long id, Order order) {
        this.id = id;
        this.order = order;
        this.timeStamp = order.getCreationDate();
    }

    public static OrderCreatedEvent of(Order order) throws Exception {

        return new OrderCreatedEvent(order.getId(), order);
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(id);
    }

    @Override
    public String getAggregateType() {
        return "Order";
    }

    @Override
    public String getType() {
        return "OrderCreated";
    }

    @Override
    public Date getTimestamp() {
        return timeStamp;
    }

    @Override
    public byte[] getPayload() {

        org.gmagnotta.model.event.OrderOuterClass.Order protoOrder = OrderUtils.convertToProtobuf(order);
        
        return protoOrder.toByteArray();
    }
}