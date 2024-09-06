FROM registry.redhat.io/jboss-webserver-6/jws60-openjdk17-openshift-rhel8@sha256:8ff2a1c968d3cca627e31e2cb9c336dafa651fe2d29cb64827f6cf6e877b6ab3

LABEL "io.k8s.description"="/tmp" \
      "io.openshift.s2i.build.image"="registry.redhat.io/jboss-webserver-6/jws60-openjdk17-openshift-rhel8@sha256:8ff2a1c968d3cca627e31e2cb9c336dafa651fe2d29cb64827f6cf6e877b6ab3" \
      "io.openshift.s2i.scripts-url"="image:///usr/local/s2i"

USER root
# Copying in source code
COPY src /tmp/src
# Change file ownership to the assemble user. Builder image must support chown command.
RUN chown -R 1001:0 /tmp/src
USER 1001
# Assemble script sourced from builder image based on user input or image metadata.
# If this file does not exist in the image, the build will fail.
RUN /usr/local/s2i/assemble
# Run script sourced from builder image based on user input or image metadata.
# If this file does not exist in the image, the build will fail.
CMD /usr/local/s2i/run
