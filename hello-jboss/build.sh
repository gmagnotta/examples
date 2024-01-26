#!/bin/sh

set -e

export BUILDER_IMAGE="registry.redhat.io/jboss-eap-7/eap74-openjdk11-openshift-rhel8@sha256:df517fcc8c950f2abefa2106917f3a1a5eba335072cd3e88e99268f2500e5e8f"
export OUTPUT_IMAGE="hello-jboss"
export INCREMENTAL=true
export RUNTIME_IMAGE="registry.redhat.io/jboss-eap-7/eap74-openjdk11-runtime-openshift-rhel8"
export RUNTIME_ARTIFACT="/s2i-output/server/"
export DESTINATION_URL="/opt/eap"
export RUNTIME_CMD="/opt/eap/bin/openshift-launch.sh"

buildah_s2i.sh
#buildah_s2i_runtime.sh

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
