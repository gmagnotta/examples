# quarkus-order-streams Project

Example application to show kafka streams running on Quarkus.

This application requires the following Kafka topics

dbserver1.public.items
dbserver1.public.line_items
dbserver1.public.orders
outbox.event.OrderCreated
topItems
topOrders
debezium_configs
debezium_offsets
debezium_statuses

```
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8083/connectors/ -d @debezium.json
```

To check that debezium is populating correctly the topics, use the following command:

```
./bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --from-beginning --property print.key=true  --topic dbserver1.public.items

./bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --from-beginning --property print.key=true  --topic dbserver1.public.line_items

./bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --from-beginning --property print.key=true  --topic dbserver1.public.orders
```


## Deploy in K8s

To make it working in K8s the following secrets are needed:

oc create secret generic quarkus-order-streams-certs -n project --from-file=ca.p12=/tmp/ca.p12 --from-file=user.p12=/tmp/streams.p12

oc create secret generic quarkus-order-streams-password -n project --from-file=ca.password=/tmp/ca.password --from-file=streams.password=/tmp/streams.password

The files and the password should be extracted from KafkaUser created by Strimzi
