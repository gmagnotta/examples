#!/usr/bin/env bash
set -e

export PASSWORD="password"
export APPLICATION_NAME="sso"
export DNAME="CN=secure-sso-project.domain"
export SSO_ADMIN_USERNAME="admin"
export SSO_ADMIN_PASSWORD="changeme!"
export SSO_HOSTNAME="secure-sso-project.domain"
export DATABASE_USERNAME="sso"
export DATABASE_PASSWORD="changeme!"
export DATABASE_NAME="sso"
export SSO_POSTGRESQL_SERVICE_HOST="postgresql"
export SSO_POSTGRESQL_SERVICE_PORT="5432"

# Create the HTTPS keystore:

TEMPDIR=`mktemp -d`

echo "Using temp directory $TEMPDIR"

openssl req -new -newkey rsa:4096 -x509 -keyout $TEMPDIR/xpaas.key -out $TEMPDIR/xpaas.crt -days 365 -subj "/CN=xpaas-sso-demo.ca" -passin env:PASSWORD -passout env:PASSWORD

keytool -genkeypair -keyalg RSA -keysize 2048 \
-storepass $PASSWORD \
-keypass $PASSWORD \
-dname $DNAME \
-alias jboss \
-storetype JKS \
-keystore $TEMPDIR/keystore.jks

keytool -certreq -keyalg rsa -alias jboss -keystore $TEMPDIR/keystore.jks -file $TEMPDIR/sso.csr -storepass $PASSWORD

openssl x509 -req -CA $TEMPDIR/xpaas.crt -CAkey $TEMPDIR/xpaas.key -in $TEMPDIR/sso.csr -out $TEMPDIR/sso.crt -days 365 -CAcreateserial -passin env:PASSWORD

keytool -import -file $TEMPDIR/xpaas.crt -alias xpaas.ca -keystore $TEMPDIR/keystore.jks -storepass $PASSWORD

keytool -import -file $TEMPDIR/sso.crt -alias jboss -keystore $TEMPDIR/keystore.jks -storepass $PASSWORD


# Generate a secure key for the JGroups keystore:

keytool -genseckey \
-alias jgroups \
-storepass $PASSWORD \
-keypass $PASSWORD \
-storetype JCEKS \
-keystore $TEMPDIR/jgroups.jceks


# Import the CA certificate into a new Red Hat Single Sign-On server truststore:

keytool -import -file $TEMPDIR/xpaas.crt -alias xpaas.ca -keystore $TEMPDIR/truststore.jks -storepass $PASSWORD


# OCP Secrets

oc policy add-role-to-user view system:serviceaccount:$(oc project -q):default

oc create secret generic sso-app-secret --from-file=$TEMPDIR/keystore.jks --from-file=$TEMPDIR/jgroups.jceks --from-file=$TEMPDIR/truststore.jks

oc secrets link default sso-app-secret


# Deploy template
oc process \
 -p HTTPS_SECRET="sso-app-secret" \
 -p HTTPS_KEYSTORE="keystore.jks" \
 -p HTTPS_NAME="jboss" \
 -p HTTPS_PASSWORD=$(echo -n $PASSWORD| base64) \
 -p JGROUPS_ENCRYPT_SECRET="sso-app-secret" \
 -p JGROUPS_ENCRYPT_KEYSTORE="jgroups.jceks" \
 -p JGROUPS_ENCRYPT_NAME="jgroups" \
 -p JGROUPS_ENCRYPT_PASSWORD=$PASSWORD \
 -p SSO_ADMIN_USERNAME=$(echo -n $SSO_ADMIN_USERNAME| base64) \
 -p SSO_ADMIN_PASSWORD=$(echo -n $SSO_ADMIN_PASSWORD| base64) \
 -p SSO_TRUSTSTORE="truststore.jks" \
 -p SSO_TRUSTSTORE_PASSWORD=$(echo -n $PASSWORD| base64) \
 -p SSO_TRUSTSTORE_SECRET="sso-app-secret" \
 -p SSO_HOSTNAME=$SSO_HOSTNAME \
 -p DATABASE_USERNAME=$(echo -n $DATABASE_USERNAME| base64) \
 -p DATABASE_PASSWORD=$(echo -n $DATABASE_PASSWORD| base64) \
 -p DATABASE_NAME=$(echo -n $DATABASE_NAME| base64) \
 -p SSO_POSTGRESQL_SERVICE_HOST=$SSO_POSTGRESQL_SERVICE_HOST \
 -p SSO_POSTGRESQL_SERVICE_PORT=$SSO_POSTGRESQL_SERVICE_PORT \
-f template-sso.yaml | oc apply -f -