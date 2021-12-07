# BUILD IMAGE
FROM registry.redhat.io/jboss-eap-7/eap74-openjdk11-openshift-rhel8 as builder

ARG S2I_SCRIPTS=/usr/local/s2i

COPY --chown=jboss:0 ./ /tmp/src/

RUN $S2I_SCRIPTS/assemble

# RUN IMAGE
FROM registry.redhat.io/jboss-eap-7/eap74-openjdk11-openshift-rhel8

ENV ENABLE_GENERATE_DEFAULT_DATASOURCE=true

COPY --chown=jboss:0 --from=builder /deployments/my-app-1.0-SNAPSHOT.war /deployments/ROOT.war
