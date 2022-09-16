package org.gmagnotta.protobuf;

import org.gmagnotta.model.Item;
import org.gmagnotta.model.LineItem;
import org.gmagnotta.model.Order;
import org.infinispan.protostream.SerializationContextInitializer;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import org.infinispan.protostream.types.java.math.BigDecimalAdapter;
import org.infinispan.protostream.types.java.math.BigIntegerAdapter;

@AutoProtoSchemaBuilder(includeClasses = {Order.class, LineItem.class, Item.class, BigDecimalAdapter.class, BigIntegerAdapter.class },  schemaFileName = "order.proto", schemaFilePath = "proto", schemaPackageName = "library")
public interface OrderInitializer extends SerializationContextInitializer {
}
