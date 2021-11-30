# RUN IMAGE
FROM registry.access.redhat.com/ubi8/openjdk-11

USER root

RUN mkdir /opt/quarkus-app && \
    mkdir /opt/quarkus-app/ObjectStore && \
    chown -R 0:0 /opt/quarkus-app && \
    chmod -R 770 /opt/quarkus-app

VOLUME /opt/quarkus-app/ObjectStore

COPY target /opt/quarkus-app/

WORKDIR /opt/quarkus-app/

ENTRYPOINT ["java", "-jar", "camel-quarkus-order-processor-api-1.0.0-SNAPSHOT-runner.jar"]

