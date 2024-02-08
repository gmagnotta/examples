#!/bin/bash

podman network create --ignore dev

podman run -d --rm --name jbossdb --net dev \
 -e POSTGRESQL_USER="jboss" \
 -e POSTGRESQL_PASSWORD="jboss" \
 -e POSTGRESQL_DATABASE="jboss" \
 localhost/hello-jbossdb

podman run --name jboss --net dev \
 -e OIDC_PROVIDER_NAME="rh-sso" \
 -e OIDC_PROVIDER_URL="http://keycloak:8081/realms/jboss" \
 -e OIDC_SECURE_DEPLOYMENT_SECRET="5UizIb7RwBv7H9QtV17HbwuWrEApoxnA" \
 -e OIDC_DISABLE_SSL_CERTIFICATE_VALIDATION="true" \
 -e OIDC_HOSTNAME_HTTP="localhost" \
 -e OIDC_USER_NAME="jboss" \
 -e AAAA_SSO_URL="http://keycloak:8081/" \
 -e AAAA_SSO_REALM="jboss" \
 -e AAAA_SSO_SECRET="5UizIb7RwBv7H9QtV17HbwuWrEApoxnA" \
 -e AAAA_SSO_CLIENT="jboss" \
 -e AAAA_HOSTNAME_HTTP="localhost" \
 -e AAAA_SSO_DISABLE_SSL_CERTIFICATE_VALIDATION="true" \
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