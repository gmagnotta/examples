#!/usr/bin/env bash
set -e

PASSWORD="password"
APPLICATION_NAME="jboss-eap"

oc policy add-role-to-user view system:serviceaccount:$(oc project -q):default

keytool -genkeypair \
-storepass $PASSWORD \
-keypass $PASSWORD \
-dname "CN=secure-jboss-eap-..." \
-alias https \
-storetype JKS \
-keystore eapkeystore.jks

keytool -genseckey \
-alias jgroups \
-storepass $PASSWORD \
-keypass $PASSWORD \
-storetype JCEKS \
-keystore eapjgroups.jceks

oc create secret generic eap-ssl-secret --from-file=eapkeystore.jks

oc create secret generic eap-jgroup-secret --from-file=eapjgroups.jceks

oc secrets link default eap-ssl-secret eap-jgroup-secret

oc patch configmap/$APPLICATION_NAME --type merge -p '{"data":{"hostname-http":"jboss-eap-..."}}'
oc patch configmap/$APPLICATION_NAME --type merge -p '{"data":{"hostname-https":"secure-jboss-eap-..."}}'
oc patch configmap/$APPLICATION_NAME --type merge -p '{"data":{"sso-disable-ssl-certificate-validation":"true"}}'
oc patch configmap/$APPLICATION_NAME --type merge -p '{"data":{"sso-realm":"demo"}}'
oc patch configmap/$APPLICATION_NAME --type merge -p '{"data":{"sso-url":"https://secure-sso-.../auth/"}}'
oc patch secret/$APPLICATION_NAME --type merge -p '{"data":{"sso-public-key":"base64string"}}'
oc patch secret/$APPLICATION_NAME --type merge -p '{"data":{"sso-secret":"base64string"}}'
oc patch secret/$APPLICATION_NAME --type merge -p '{"data":{"https-password":"base64string"}}'
oc patch secret/$APPLICATION_NAME --type merge -p '{"data":{"jgroups-encrypt-password":"base64string"}}'

