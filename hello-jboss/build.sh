#!/bin/sh

set -e

export BUILDER_IMAGE="registry.redhat.io/jboss-eap-7/eap74-openjdk11-openshift-rhel8"
export OUTPUT_IMAGE="hello-jboss"
export INCREMENTAL=true
export RUNTIME_IMAGE="registry.redhat.io/jboss-eap-7/eap74-openjdk11-runtime-openshift-rhel8"
export RUNTIME_ARTIFACT="/s2i-output/server/"
export DESTINATION_URL="/opt/eap"
export RUNTIME_CMD="/opt/eap/bin/openshift-launch.sh"

buildah_s2i.sh
#buildah_s2i_runtime.sh
