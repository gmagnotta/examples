# Build stage
FROM registry.redhat.io/ubi8/openjdk-8 as build
COPY --chown=jboss:jboss ./pom.xml ./
RUN mvn dependency:go-offline -B
COPY --chown=jboss:jboss ./ ./
RUN mvn clean package

# Create final image
FROM registry.redhat.io/ubi8/openjdk-8
COPY --from=build /home/jboss/target/backend-spring-boot-1.0.0.jar ./
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","./backend-spring-boot-1.0.0.jar"]

