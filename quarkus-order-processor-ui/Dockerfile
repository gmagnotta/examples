# BUILD IMAGE
FROM registry.redhat.io/jboss-webserver-5/jws56-openjdk11-openshift-rhel8 as builder

ARG S2I_SCRIPTS=/usr/local/s2i
#ENV MAVEN_MIRRORS="nexus"
#ENV NEXUS_MAVEN_MIRROR_ID="nexus"
#ENV NEXUS_MAVEN_MIRROR_OF="\*"
#ENV NEXUS_MAVEN_MIRROR_URL="http://nexus3-giuseppe.apps.shared-na46.openshift.opentlc.com/repository/maven-public/"

ENV MAVEN_REPOS="nexus"
ENV NEXUS_MAVEN_REPO_ID="nexus"
ENV NEXUS_MAVEN_REPO_USERNAME="tekton"
ENV NEXUS_MAVEN_REPO_PASSWORD="tekton"
ENV NEXUS_MAVEN_REPO_URL="http://nexus3-giuseppe.apps.shared-na46.openshift.opentlc.com/repository/maven-public/"

ENV LOGGING_SCRIPT_DEBUG="false"

COPY --chown=jboss:0 ./ /tmp/src/

RUN $S2I_SCRIPTS/assemble

# RUN IMAGE
FROM registry.redhat.io/jboss-webserver-5/jws56-openjdk11-openshift-rhel8

COPY --chown=jboss:0 --from=builder /deployments/quarkus-order-processor-ui.war /deployments/ROOT.war
