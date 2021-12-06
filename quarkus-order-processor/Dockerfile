# BUILD IMAGE
FROM registry.access.redhat.com/ubi8/openjdk-11 as builder

ARG S2I_SCRIPTS=/usr/local/s2i

ENV S2I_SOURCE_DEPLOYMENTS_FILTER="*-runner.jar lib"
ENV MAVEN_S2I_GOALS="package test"

COPY --chown=jboss:0 ./ /tmp/src/

RUN $S2I_SCRIPTS/assemble

# RUN IMAGE
FROM registry.access.redhat.com/ubi8/openjdk-11

COPY --chown=jboss:0 --from=builder /deployments/ /deployments/
