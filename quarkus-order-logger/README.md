# quarkus-order-logger

Example Quarkus application that:

* receives Order events from Kafka and send them to Telegram

Reset offsets

./kafka-consumer-groups.sh --bootstrap-server kafka:9092 --group quarkus-order-logger  --describe

./kafka-consumer-groups.sh --bootstrap-server kafka:9092 --group quarkus-order-logger --topic outbox.event.OrderCreated --reset-offsets --to-earliest --execute
