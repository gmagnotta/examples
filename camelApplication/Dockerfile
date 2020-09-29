FROM registry.redhat.io/ubi8/openjdk-8
LABEL "io.openshift.s2i.build.image"="registry.redhat.io/ubi8/openjdk-8" \
      "io.openshift.s2i.build.source-location"="."

# Build stage
FROM registry.redhat.io/ubi8/openjdk-8 as build
COPY --chown=1001:0 ./pom.xml ./
RUN mvn dependency:go-offline -B
COPY --chown=1001:0 ./ ./
RUN mvn clean package

# Create final image
FROM registry.redhat.io/ubi8/openjdk-8
COPY --from=build --chown=1002:0 /home/jboss/target/camelapplication-jar-with-dependencies.jar ./
EXPOSE 8080
USER 1001
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","./camelapplication-jar-with-dependencies.jar"]
