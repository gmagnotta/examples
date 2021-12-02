# RUN IMAGE
FROM registry.redhat.io/jboss-webserver-5/jws56-openjdk11-openshift-rhel8

COPY --chown=jboss:0 target/quarkus-order-processor-ui.war $JWS_HOME/webapps/ROOT.war
