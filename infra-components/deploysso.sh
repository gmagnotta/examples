#!/usr/bin/env bash
set -e

export PASSWORD="password"
export APPLICATION_NAME="sso"

# Create the HTTPS keystore:

openssl req -new -newkey rsa:4096 -x509 -keyout xpaas.key -out xpaas.crt -days 365 -subj "/CN=xpaas-sso-demo.ca" -passin env:PASSWORD -passout env:PASSWORD

keytool -genkeypair -keyalg RSA -keysize 2048 \
-storepass $PASSWORD \
-keypass $PASSWORD \
-dname "CN=secure-sso-..." \
-alias jboss \
-storetype JKS \
-keystore keystore.jks

keytool -certreq -keyalg rsa -alias jboss -keystore keystore.jks -file sso.csr -storepass $PASSWORD

openssl x509 -req -CA xpaas.crt -CAkey xpaas.key -in sso.csr -out sso.crt -days 365 -CAcreateserial -passin env:PASSWORD

keytool -import -file xpaas.crt -alias xpaas.ca -keystore keystore.jks -storepass $PASSWORD 

keytool -import -file sso.crt -alias jboss -keystore keystore.jks -storepass $PASSWORD 


# Generate a secure key for the JGroups keystore:

keytool -genseckey \
-alias secret-key \
-storepass $PASSWORD \
-keypass $PASSWORD \
-storetype JCEKS \
-keystore jgroups.jceks


# Import the CA certificate into a new Red Hat Single Sign-On server truststore:

keytool -import -file xpaas.crt -alias xpaas.ca -keystore truststore.jks -storepass $PASSWORD


# OCP Secrets

oc policy add-role-to-user view system:serviceaccount:$(oc project -q):default

oc create secret generic sso-app-secret --from-file=keystore.jks --from-file=jgroups.jceks --from-file=truststore.jks

oc secrets link default sso-app-secret


# Other OCP

#oc apply -f registry.redhat.io-sa-secret.yaml

#oc secrets link default registry.redhat.io-sa-pull-secret --for=pull

oc process \
 -p HTTPS_SECRET="sso-app-secret" \
 -p HTTPS_KEYSTORE="keystore.jks" \
 -p HTTPS_NAME="jboss" \
 -p HTTPS_PASSWORD="password" \
 -p JGROUPS_ENCRYPT_SECRET="sso-app-secret" \
 -p JGROUPS_ENCRYPT_KEYSTORE="jgroups.jceks" \
 -p JGROUPS_ENCRYPT_NAME="secret-key" \
 -p JGROUPS_ENCRYPT_PASSWORD="password" \
 -p SSO_ADMIN_USERNAME="base64string" \
 -p SSO_ADMIN_PASSWORD="base64string" \
 -p SSO_TRUSTSTORE="truststore.jks" \
 -p SSO_TRUSTSTORE_PASSWORD="password" \
 -p SSO_TRUSTSTORE_SECRET="sso-app-secret" \
 -p SSO_HOSTNAME="secure-sso-..." \
-f template-sso.yaml | oc apply -f -
