# quarkus-order-streams Project

Example application to show kafka streams running on Quarkus.

Setup kafka connect and then use the configuration in "debezium.json" as example:

```
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8083/connectors/ -d @debezium.json
```