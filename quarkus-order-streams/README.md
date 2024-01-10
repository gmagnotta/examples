# quarkus-order-streams Project

Example application to show kafka streams running on Quarkus.

Setup kafka connect and then use the configuration in "debezium.json" as example:

Create the following topics

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


## Create required secrets in K8s

oc create secret generic quarkus-order-streams-certs --from-file=ca.p12=/tmp/ca.p12 --from-file=user.p12=/tmp/user.p12

The files and the password should be extracted from KafkaUser created by Strimzi