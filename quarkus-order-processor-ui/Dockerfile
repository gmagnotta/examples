# BUILD IMAGE
FROM registry.access.redhat.com/ubi8/openjdk-11 as builder

USER root

COPY . /tmp/src

WORKDIR /tmp/src

RUN mvn clean package

# RUN IMAGE
FROM tomcat

USER root

RUN rm -rf /opt/jws/tomcat/webapps/*

COPY --from=builder /tmp/src/target/quarkus-order-processor-ui.war /opt/jws/tomcat/webapps/ROOT.war

