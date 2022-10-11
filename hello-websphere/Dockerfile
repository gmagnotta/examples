FROM registry.access.redhat.com/ubi8/openjdk-8 AS builder

COPY --chown=185:0 . /tmp/src

ENV S2I_SOURCE_DEPLOYMENTS_FILTER=*.war

RUN /usr/local/s2i/assemble

FROM docker.io/ibmcom/websphere-traditional:8.5.5.21-ubi8

COPY --chown=was:root --from=builder /deployments/hello-tomcat.war /work/config/
COPY --chown=was:root was-config.props /work/config/
COPY --chown=was:root install_app.py /work/config/install_app.py

RUN /work/configure.sh
