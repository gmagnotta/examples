package org.gmagnotta;

import java.util.Objects;

import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.gmagnotta.model.BiggestOrders;
import org.gmagnotta.model.event.OrderOuterClass.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiggestOrderTransformer implements ValueTransformer<Order, BiggestOrders>{
    
    public static final String MAX_ORDER = "MAX_ORDER";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BiggestOrderTransformer.class);

    private KeyValueStore<String, BiggestOrders> stateStore;
    private final String storeName;
    private ProcessorContext context;
    private int maxSize;

    public BiggestOrderTransformer(String storeName, int maxSize) {
        Objects.requireNonNull(storeName,"Store Name can't be null");
        this.storeName = storeName;
        this.maxSize = maxSize;
    }

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
        stateStore = (KeyValueStore) this.context.getStateStore(storeName);
    }

    @Override
    public BiggestOrders transform(Order value) {

        BiggestOrders biggest = stateStore.get(MAX_ORDER);

        if (biggest == null) {
            LOGGER.debug("Empty Store. Adding new biggestOrder");
            biggest = new BiggestOrders(maxSize);
        }

        biggest.add(value);

        stateStore.put(MAX_ORDER, biggest);

        return biggest;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

    
    
}
