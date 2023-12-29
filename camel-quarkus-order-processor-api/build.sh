#!/bin/sh
set -e

export BUILDER_IMAGE="registry.access.redhat.com/ubi8/openjdk-11"
export OUTPUT_IMAGE="camel-quarkus-order-processor-api"
export INCREMENTAL=true
#RUNTIME_IMAGE=$BUILDER_IMAGE
#RUNTIME_ARTIFACT="/deployments/"

buildah_s2i.sh