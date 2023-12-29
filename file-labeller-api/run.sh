#!/usr/bin/env bash
set -e

podman run --rm -ti -p 8082:8080 \
    -e QUARKUS_DATASOURCE_USERNAME=jboss \
    -e QUARKUS_DATASOURCE_PASSWORD=jboss \
    -e QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://192.168.251.11:5432/jboss \
    localhost/file-labeller-api
