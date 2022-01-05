#!/usr/bin/env bash
set -e

PASSWORD="password"
APPLICATION_NAME="jboss-eap"

# Add view role to serviceaccount
oc policy add-role-to-user view system:serviceaccount:$(oc project -q):default

# Generate https certificate
keytool -genkeypair \
-storepass $PASSWORD \
-keypass $PASSWORD \
-dname "CN=secure-jboss-eap-..." \
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
 -p HOSTNAME_HTTP=jboss-eap-peppe2.apps.shared-na46.openshift.opentlc.com \
 -p HOSTNAME_HTTPS=secure-jboss-eap-peppe2.apps.shared-na46.openshift.opentlc.com \
 -p SSO_URL=https://secure-sso-peppe2.apps.shared-na46.openshift.opentlc.com/auth/ \
 -p SSO_REALM=demorealm \
 -p SSO_CLIENT=clientid \
 -p SSO_DISABLE_SSL_CERTIFICATE_VALIDATION=true \
 -p SSO_SECRET=M2M3Yjg5MzgtZGI5MS00NWY1LTkwMDgtYTNkOWQ0ZWI3OTE3 \
 -p SSO_PUBLIC_KEY=TUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFpVGo4d0l1ckVxeUFXNkNLQkhCR0Jxd3dPT1o0N1ZOMkhqV2cvWlE5TU9CQjNIbHUxbXJUUUF3b1hBNUgySjZRRWZFY1JHRXdBTk5Iak0rZ3NhSmdmcVliNnZ2TUFONzltZDRDUEFWeExpcmhjSGlOZk1Xc2w1QXRGMWN0VXd2dFdtcC9kTnZJUE9DTEh2RGk3eEJ4RDV5V1NCMnN1Sk1IckFORllUMlVtbUVUSTVWSmpxbGNqdEtJVXc5OTdveDg3WWduL0paWEFBYmZOMXRGNU0yZ0pvU2pPS2tIUk83UnJtQ0IxRDBoZEdTVXpNQ01aMnFTU3N5dHRGbW84ekc2YnBUUVltYldFVVhlT0laeTl2TmtidmgvSW94UW9PMzFSTS9sTWRIeGI5Zk1JRFRoNlRCNEh3SkZxUXNWZE5TakF3SzNaSTFVOS9RclAybjYrcnp1eHdJREFRQUI= \
 -f jboss-eap-test-config-template.yaml | oc apply -f -

