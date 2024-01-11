# quarkus-order-logger

Example Quarkus application that:

* receives Order events from Kafka and send them to Telegram

Reset offsets

./kafka-consumer-groups.sh --bootstrap-server kafka:9092 --group quarkus-order-logger  --describe

./kafka-consumer-groups.sh --bootstrap-server kafka:9092 --group quarkus-order-logger --topic outbox.event.OrderCreated --reset-offsets --to-earliest --execute

## Create required secrets in K8s

`oc create secret generic quarkus-order-logger-certs --from-file=ca.p12=/tmp/ca.p12 --from-file=user.p12=/tmp/consumer.p12`

The files and the password should be extracted from KafkaUser created by Strimzi
