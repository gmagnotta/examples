#!/bin/bash

podman network create --ignore dev

podman run -d --name jbossdb --net dev \
 -e POSTGRESQL_USER="jboss" \
 -e POSTGRESQL_PASSWORD="jboss" \
 -e POSTGRESQL_DATABASE="jboss" \
 localhost/hello-jbossdb

podman run --name jboss --net dev \
 -e SSO_URL="http://keycloak:8081/" \
 -e SSO_REALM="jboss" \
 -e SSO_SECRET="h301iicLztkcTeuIxOHyNtk8VViCrGp4" \
 -e SSO_CLIENT="jboss" \
 -e HOSTNAME_HTTP="localhost" \
 -e SSO_DISABLE_SSL_CERTIFICATE_VALIDATION="true" \
 -e DB_SERVICE_PREFIX_MAPPING="ocp-postgresql=DS1" \
 -e OCP_POSTGRESQL_SERVICE_HOST="jbossdb" \
 -e OCP_POSTGRESQL_SERVICE_PORT="5432" \
 -e DS1_CONNECTION_CHECKER="org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker" \
 -e DS1_DATABASE="jboss" \
 -e DS1_DRIVER="postgresql" \
 -e DS1_EXCEPTION_SORTER="org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter" \
 -e DS1_USERNAME="jboss" \
 -e DS1_PASSWORD="jboss" \
 -e DS1_MAX_POOL_SIZE="20" \
 -e DS1_MIN_POOL_SIZE="20" \
 -e DS1_NONXA="true" \
 -e DS1_DATABASE="jboss" \
 -e DS1_USERNAME="jboss" \
 -e DS1_PASSWORD="jboss" \
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