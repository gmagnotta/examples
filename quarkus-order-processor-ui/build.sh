#!/bin/sh

set -e

export BUILDER_IMAGE="registry.redhat.io/jboss-webserver-5/jws56-openjdk11-openshift-rhel8"
export OUTPUT_IMAGE="quarkus-order-processor-ui"
export INCREMENTAL=true
#export RUNTIME_IMAGE=$BUILDER_IMAGE
#export RUNTIME_ARTIFACT="/deployments/"

buildah_s2i.sh