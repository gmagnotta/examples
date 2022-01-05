#!/usr/bin/env bash
set -e

export PASSWORD="password"
export APPLICATION_NAME="sso"
export DNAME="CN=secure-sso-project-domain"
export SSO_ADMIN_USERNAME="admin"
export SSO_ADMIN_PASSWORD="changeme!"
export SSO_HOSTNAME="secure-sso-project-domain"

# Create the HTTPS keystore:

openssl req -new -newkey rsa:4096 -x509 -keyout xpaas.key -out xpaas.crt -days 365 -subj "/CN=xpaas-sso-demo.ca" -passin env:PASSWORD -passout env:PASSWORD

keytool -genkeypair -keyalg RSA -keysize 2048 \
-storepass $PASSWORD \
-keypass $PASSWORD \
-dname $DNAME \
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


# Deploy template
oc process \
 -p HTTPS_SECRET="sso-app-secret" \
 -p HTTPS_KEYSTORE="keystore.jks" \
 -p HTTPS_NAME="jboss" \
 -p HTTPS_PASSWORD=$PASSWORD \
 -p JGROUPS_ENCRYPT_SECRET="sso-app-secret" \
 -p JGROUPS_ENCRYPT_KEYSTORE="jgroups.jceks" \
 -p JGROUPS_ENCRYPT_NAME="secret-key" \
 -p JGROUPS_ENCRYPT_PASSWORD=$PASSWORD \
 -p SSO_ADMIN_USERNAME=$(echo -n $SSO_ADMIN_USERNAME| base64) \
 -p SSO_ADMIN_PASSWORD=$(echo -n $SSO_ADMIN_PASSWORD| base64) \
 -p SSO_TRUSTSTORE="truststore.jks" \
 -p SSO_TRUSTSTORE_PASSWORD=$PASSWORD \
 -p SSO_TRUSTSTORE_SECRET="sso-app-secret" \
 -p SSO_HOSTNAME=$SSO_HOSTNAME \
-f template-sso.yaml | oc apply -f -