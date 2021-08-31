# BUILD IMAGE
FROM registry.access.redhat.com/ubi8/openjdk-11 as builder

USER root

COPY . /tmp/src

WORKDIR /tmp/src

RUN mvn clean package

# RUN IMAGE
FROM registry.access.redhat.com/ubi8/openjdk-11

USER root

RUN mkdir /opt/quarkus-app && \
    chown -R 0:0 /opt/quarkus-app && \
    chmod -R 770 /opt/quarkus-app

COPY --from=builder /tmp/src/target /opt/quarkus-app/

WORKDIR /opt/quarkus-app/

ENTRYPOINT ["java", "-jar", "quarkus-order-processor-1.0.0-SNAPSHOT-runner.jar"]

