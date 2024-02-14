#!/bin/bash

podman network create --ignore dev

podman run -d --rm --name jbossdb --net dev \
 -e POSTGRESQL_USER="jboss" \
 -e POSTGRESQL_PASSWORD="jboss" \
 -e POSTGRESQL_DATABASE="jboss" \
 localhost/hello-jbossdb

sleep 15

podman run --name jboss --net dev \
 -v ./sso.jks:/etc/sso-secret-volume/sso.jks:z \
 -e KEYCLOAK_PROVIDER_URL="https://keycloak:8443/realms/jboss" \
 -e KEYCLOAK_TRUSTSTORE="/etc/sso-secret-volume/sso.jks" \
 -e KEYCLOAK_TRUSTSTORE_PASSWORD="ssopassword" \
 -e KEYCLOAK_CLIENTID="hello" \
 -e KEYCLOAK_SECRET="g1aP3w9uvS6yQWfxnvTgKynZYylwF8uZ" \
 -e POSTGRESQL_SERVICE_HOST="jbossdb" \
 -e POSTGRESQL_SERVICE_PORT="5432" \
 -e POSTGRESQL_USER="jboss" \
 -e POSTGRESQL_PASSWORD="jboss" \
 -e POSTGRESQL_DATABASE="jboss" \
 -e POSTGRESQL_DATASOURCE="ocp_postgresql" \
 -e ENABLE_GENERATE_DEFAULT_DATASOURCE="" \
 -e MQ_USERNAME="amq" \
 -e MQ_PASSWORD="amq" \
 -e MQ_QUEUES="getTopItemsCommand,getTopOrdersCommand,createOrderCommand,invalidMessage" \
 -e MQ_TOPICS="" \
 -e MQ_SERVICE_PREFIX_MAPPING="test-amq7=MQ" \
 -e MQ_PROTOCOL="tcp" \
 -e JBOSS_EAP_AMQ_TCP_SERVICE_HOST="amqbroker" \
 -e JBOSS_EAP_AMQ_TCP_SERVICE_PORT="61616" \
 -e TEST_AMQ_TCP_SERVICE_HOST="amqbroker" \
 -e TEST_AMQ_TCP_SERVICE_PORT="61616" \
 -e MQ_SERIALIZABLE_PACKAGES="" \
 -e MQ_JNDI="java:jboss/DefaultJMSConnectionFactory" \
 -p 8080:8080 \
 localhost/hello-jboss
