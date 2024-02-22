#!/bin/bash

podman network create --ignore dev

podman run --rm --name camel-quarkus-order-processor-api --net dev \
 -e QUARKUS_HTTP_PORT=8090 \
 -e APP_WEBSERVICE_SOAP_URL="http://jboss:8080/soap" \
 -p 8090:8090 \
 localhost/camel-quarkus-order-processor-api