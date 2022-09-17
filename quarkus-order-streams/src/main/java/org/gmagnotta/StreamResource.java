package org.gmagnotta;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.gmagnotta.jaxb.TopItemsResponse;
import org.gmagnotta.jaxb.TopOrdersResponse;
import org.gmagnotta.jaxb.TopValue;
import org.gmagnotta.model.BiggestOrders;
import org.gmagnotta.model.event.OrderOuterClass.Order;
import org.jboss.logging.Logger;

@Path("/api")
public class StreamResource {
    @Inject
    KafkaStreams streams;

    @Inject
	Logger logger;

    @GET
    @Path("topItems")
    @Produces(MediaType.APPLICATION_XML)
    public TopItemsResponse getTopItems() {

        ReadOnlyKeyValueStore<Integer, Integer> keyValueStore =
             //streams.store("itemsQuantity", QueryableStoreTypes.keyValueStore());
             streams.store(StoreQueryParameters.fromNameAndType("itemsQuantity", QueryableStoreTypes.keyValueStore()));

        KeyValueIterator<Integer, Integer> all = keyValueStore.all();

        Map<Integer, Integer> values = new LinkedHashMap<>();
        List<TopValue> result = new ArrayList<TopValue>();

        while (all.hasNext()) {
            KeyValue<Integer, Integer> element = all.next();

            Integer item = (Integer) element.key;
            Integer qty = (Integer) element.value;

            //values.put(item, Integer.valueOf(qty.intValue()));

            TopValue top = new TopValue();

            top.setId(item.intValue());
            top.setValue(qty.intValue());
            result.add(top);
        }
        
        /*
        StringBuffer stringBuffer = new StringBuffer();

        LinkedHashMap<Integer, Integer> sortedMap = new LinkedHashMap<>();
        values.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        //.forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
        .forEachOrdered(x -> stringBuffer.append("Item: " + x.getKey() + " quantity: " + x.getValue() + "\n"));

        all.close();

        */

        //return stringBuffer.toString();


        TopItemsResponse response = new TopItemsResponse();

        response.setStatus("KAFKA");
        response.getTopvalue().addAll(result);

        return response;
    }

    @GET
    @Path("topOrders")
    @Produces(MediaType.APPLICATION_XML)
    public TopOrdersResponse getTopOrders() {

        ReadOnlyKeyValueStore<String, BiggestOrders> keyValueStore =
             //streams.store("orderStateStore", QueryableStoreTypes.keyValueStore());
             streams.store(StoreQueryParameters.fromNameAndType("orderStateStore", QueryableStoreTypes.keyValueStore()));

        BiggestOrders biggest = keyValueStore.get(BiggestOrderTransformer.MAX_ORDER);

        Iterator<Order> iterator = biggest.iterator();

        StringBuffer stringBuffer = new StringBuffer();
        List<TopValue> result = new ArrayList<TopValue>();

        while (iterator.hasNext()) {
            Order order = iterator.next();

            Integer id = (Integer) order.getId();
            BigDecimal qty = fromProtoBuf(order.getAmount());

            //values.put(item, Integer.valueOf(qty.intValue()));

            TopValue top = new TopValue();

            top.setId(id.intValue());
            top.setValue(qty.intValue());
            result.add(top);

            // stringBuffer.append("Order: " + order.getId() + " amount: " + fromProtoBuf(order.getAmount()) + "\n");
        }

        TopOrdersResponse response = new TopOrdersResponse();

        response.setStatus("KAFKA");
        response.getTopvalue().addAll(result);

        return response;

        // return stringBuffer.toString();

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