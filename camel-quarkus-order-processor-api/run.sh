#!/bin/bash

podman network create --ignore dev

podman run --rm --name camel-quarkus-order-processor-api --net dev \
 -e QUARKUS_HTTP_PORT=8080 \
 -e APP_WEBSERVICE_SOAP_URL="http://jboss:8080/soap" \
 -e QUARKUS_ARTEMIS_URL=tcp://amqbroker:61616 \
 -e QUARKUS_ARTEMIS_USERNAME=amq \
 -e QUARKUS_ARTEMIS_PASSWORD=amq \
 -p 8090:8080 \
 localhost/camel-quarkus-order-processor-api
