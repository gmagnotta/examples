# BUILD IMAGE
FROM registry.access.redhat.com/ubi8/ubi-minimal as build

USER root
ENV FLYWAY_VERSION "8.1.0"

RUN microdnf install -y unzip && \
    curl -L https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/${FLYWAY_VERSION}/flyway-commandline-${FLYWAY_VERSION}.zip -o /tmp/flyway-commandline-${FLYWAY_VERSION}.zip && \
    unzip /tmp/flyway-commandline-${FLYWAY_VERSION}.zip -d /tmp/ && \
    ln -s /tmp/flyway-${FLYWAY_VERSION} /tmp/flyway

# RUN IMAGE
FROM registry.access.redhat.com/ubi8/openjdk-11-runtime

LABEL io.openshift.s2i.scripts-url="image:///flyway" \
      io.s2i.scripts-url="image:///flyway"

USER root

COPY --chown=jboss:0 --from=build /tmp/flyway /flyway

ENV FLYWAY_EDITION community

ENV USERNAME USERNAME
ENV PASSWORD PASSWORD
ENV JDBC_URL URL
ENV FLYWAY_PARAMS="-?"

COPY ./s2i/ /flyway/

RUN chown -R jboss:0 /flyway && \
    chmod 555 /flyway/flyway && \
    chmod 555 /flyway/run && \
    chmod 555 /flyway/assemble

WORKDIR /flyway

USER jboss

ENTRYPOINT ["/flyway/run"]
