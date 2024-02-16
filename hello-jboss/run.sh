#!/bin/bash

podman network create --ignore dev

podman run -d --rm --name jbossdb --net dev \
 -e POSTGRESQL_USER="jboss" \
 -e POSTGRESQL_PASSWORD="jboss" \
 -e POSTGRESQL_DATABASE="jboss" \
 localhost/hello-jbossdb

sleep 15

if [ ! -f eapkeystore.jks ]; then
  echo "Generate keystore"
  keytool -genkeypair -storepass password -keypass password -dname CN=localhost -alias https -storetype JKS -keyalg RSA -keystore eapkeystore.jks
fi

if [ ! -f sso.pem ]; then
  echo "Import SSO cert"
  /usr/bin/openssl s_client -showcerts -connect keycloak:8444 </dev/null 2>/dev/null|openssl x509 -outform PEM >sso.pem

  keytool -importcert -alias sso -storepass password -noprompt -storetype JKS -keystore sso.jks -file sso.pem
fi

podman run --rm --name jboss --net dev \
 -v ./sso.jks:/etc/sso-secret-volume/sso.jks:z \
 -v ./eapkeystore.jks:/etc/eap-secret-volume/eapkeystore.jks:z \
 -e KEYCLOAK_PROVIDER_URL="http://10.89.0.52:8081/realms/jboss" \
 -e KEYCLOAK_TRUSTSTORE="/etc/sso-secret-volume/sso.jks" \
 -e KEYCLOAK_TRUSTSTORE_PASSWORD="password" \
 -e KEYCLOAK_CLIENTID="jboss" \
 -e KEYCLOAK_SECRET="j5ILKb1uhkrIWJ5N0CQ2USqcUm5GYy4F" \
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
 -e HTTPS_KEYSTORE_DIR="/etc/eap-secret-volume" \
 -e HTTPS_KEYSTORE="eapkeystore.jks" \
 -e HTTPS_KEYSTORE_TYPE="JKS" \
 -e HTTPS_NAME="https" \
 -e HTTPS_PASSWORD="password" \
 -p 8080:8080 \
 -p 8443:8443 \
 localhost/hello-jboss
