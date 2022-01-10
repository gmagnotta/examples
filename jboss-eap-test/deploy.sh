#!/usr/bin/env bash
set -e

PASSWORD="password"
APPLICATION_NAME="jboss-eap"
DATABASE_USERNAME="jboss"
DATABASE_PASSWORD="jboss"
DATABASE_NAME="jboss"
SSO_PUBLIC_KEY=""
SSO_SECRET=""
SSO_URL="https://secure-sso-project.domain/auth/"
SSO_REALM="demo"
SSO_CLIENT="jboss-eap"
HOSTNAME_HTTP="jboss-eap-project.domain"
HOSTNAME_HTTPS="secure-jboss-eap-project.domain"
OCP_POSTGRESQL_SERVICE_HOST="postgresql"
OCP_POSTGRESQL_SERVICE_PORT="5432"

# Add view role to serviceaccount
oc policy add-role-to-user view system:serviceaccount:$(oc project -q):default

# Generate https certificate
keytool -genkeypair \
-storepass $PASSWORD \
-keypass $PASSWORD \
-dname "CN=$HOSTNAME_HTTPS" \
-alias https \
-storetype JKS \
-keystore eapkeystore.jks

# Generate jgroups key
keytool -genseckey \
-alias jgroups \
-storepass $PASSWORD \
-keypass $PASSWORD \
-storetype JCEKS \
-keystore eapjgroups.jceks

# Import https certificate in OpenShift
oc create secret generic eap-ssl-secret --from-file=eapkeystore.jks

# Import jgroups key in OpenShift
oc create secret generic eap-jgroup-secret --from-file=eapjgroups.jceks

# Link secrets to default service account
oc secrets link default eap-ssl-secret eap-jgroup-secret

# Deploy configmap and secrets used by application
oc process \
 -p APPLICATION_NAME=$APPLICATION_NAME \
 -p DATABASE_USERNAME=$(echo -n $DATABASE_USERNAME | base64) \
 -p DATABASE_PASSWORD=$(echo -n $DATABASE_PASSWORD | base64) \
 -p DATABASE_NAME=$(echo -n $DATABASE_NAME | base64) \
 -p SSO_SECRET=$(echo -n $SSO_SECRET | base64) \
 -p SSO_URL=$SSO_URL \
 -p SSO_REALM=$SSO_REALM \
 -p SSO_CLIENT=$SSO_CLIENT \
 -p SSO_PUBLIC_KEY=$(echo -n $SSO_PUBLIC_KEY | base64 -w0) \
 -p HOSTNAME_HTTP=$HOSTNAME_HTTP \
 -p HOSTNAME_HTTPS=$HOSTNAME_HTTPS \
 -p SSO_DISABLE_SSL_CERTIFICATE_VALIDATION="true" \
 -p OCP_POSTGRESQL_SERVICE_HOST=$OCP_POSTGRESQL_SERVICE_HOST \
 -p OCP_POSTGRESQL_SERVICE_PORT=$OCP_POSTGRESQL_SERVICE_PORT \
 -f jboss-eap-test-config-template.yaml | oc apply -f -
