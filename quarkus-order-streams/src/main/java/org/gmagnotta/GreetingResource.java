package org.gmagnotta;

import java.lang.Thread.State;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.gmagnotta.model.event.OrderOuterClass.Order;
import org.gmagnotta.serde.BiggestOrders;
import org.jboss.logging.Logger;

@Path("/api")
public class GreetingResource {
    @Inject
    KafkaStreams streams;

    @Inject
	Logger logger;

    @GET
    @Path("topItems")
    @Produces(MediaType.TEXT_PLAIN)
    public String getTopItems() {

        ReadOnlyKeyValueStore<Integer, Integer> keyValueStore =
             streams.store("itemsQuantity", QueryableStoreTypes.keyValueStore());

        KeyValueIterator<Integer, Integer> all = keyValueStore.all();

        Map<Integer, Integer> values = new LinkedHashMap<>();

        while (all.hasNext()) {
            KeyValue<Integer, Integer> element = all.next();

            Integer item = (Integer) element.key;
            Integer qty = (Integer) element.value;

            values.put(item, Integer.valueOf(qty.intValue()));
        }
        
        StringBuffer stringBuffer = new StringBuffer();

        LinkedHashMap<Integer, Integer> sortedMap = new LinkedHashMap<>();
        values.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        //.forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
        .forEachOrdered(x -> stringBuffer.append("Item: " + x.getKey() + " quantity: " + x.getValue() + "\n"));

        return stringBuffer.toString();
    }

    @GET
    @Path("topOrders")
    @Produces(MediaType.TEXT_PLAIN)
    public String getTopOrders() {

        ReadOnlyKeyValueStore<String, BiggestOrders> keyValueStore =
             streams.store("orderStateStore", QueryableStoreTypes.keyValueStore());

        BiggestOrders biggest = keyValueStore.get("MAX_ORDER");

        Iterator<Order> iterator = biggest.iterator();

        StringBuffer stringBuffer = new StringBuffer();

        while (iterator.hasNext()) {
            Order order = iterator.next();

            stringBuffer.append("Order: " + order.getId() + " amount: " + fromProtoBuf(order.getAmount()) + "\n");
        }

        return stringBuffer.toString();

    }

    private static BigDecimal fromProtoBuf(org.gmagnotta.model.event.OrderOuterClass.BigDecimal proto) {

        java.math.MathContext mc2 = new java.math.MathContext(proto.getPrecision());
        java.math.BigDecimal value = new java.math.BigDecimal(
                new java.math.BigInteger(proto.getValue().toByteArray()),
                proto.getScale(),
                mc2);

        return value;
    }
}