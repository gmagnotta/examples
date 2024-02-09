#!/bin/sh

set -e

export BUILDER_IMAGE="registry.redhat.io/jboss-eap-8/eap8-openjdk17-builder-openshift-rhel8:1.0.0.GA"
export OUTPUT_IMAGE="hello-jboss"
export INCREMENTAL=true
export RUNTIME_IMAGE="registry.redhat.io/jboss-eap-8/eap8-openjdk17-runtime-openshift-rhel8:1.0.0.GA"
export SRC_ARTIFACT="/opt/server"
export DESTINATION_URL="/opt/server"

buildah_s2i.sh
buildah_s2i_runtime.sh

cat <<EOF > initdb.sh
#!/bin/bash
psql -U \${POSTGRESQL_USER} -d \${POSTGRESQL_DATABASE} -f /opt/app-root/src/postgresql-start/V1.0.0__Initial_Schema.sql
EOF

cat <<EOF > Containerfile
FROM registry.redhat.io/rhel8/postgresql-10:1-232

USER root

RUN mkdir -p /tmp/src/postgresql-init

COPY --chown=26:0 src/main/resources/db/V1.0.0__Initial_Schema.sql /tmp/src/postgresql-start/V1.0.0__Initial_Schema.sql
COPY --chown=26:0 initdb.sh /tmp/src/postgresql-start/initdb.sh

RUN /usr/libexec/s2i/assemble

USER 26
EOF

podman build -f Containerfile . -t hello-jbossdb
