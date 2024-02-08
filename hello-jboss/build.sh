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
#!/bin/sh

psql -f /opt/app-root/src/postgresql-start/V1.0.0__Initial_Schema.sql $POSTGRESQL_DATABASE
EOF

cat <<EOF > Containerfile
FROM registry.redhat.io/rhel8/postgresql-10:1-232

COPY src/main/resources/db/ /opt/app-root/src/postgresql-start/
COPY initdb.sh /opt/app-root/src/postgresql-start/
EOF

podman build -f Containerfile . -t hello-jbossdb
