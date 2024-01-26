#!/bin/bash

podman network create --ignore dev

podman run --rm --name camel-quarkus-order-processor-api --net dev \
 -e QUARKUS_ARTEMIS_URL="tcp://amqbroker:61616" \
 -e QUARKUS_ARTEMIS_USERNAME="amq" \
 -e QUARKUS_ARTEMIS_PASSWORD="amq" \
 -e QUARKUS_HTTP_PORT=8090 \
 -p 8090:8090 \
 localhost/camel-quarkus-order-processor-api