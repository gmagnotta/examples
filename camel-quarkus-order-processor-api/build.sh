#!/bin/sh
set -e

export BUILDER_IMAGE="registry.access.redhat.com/ubi8/openjdk-17"
export RUNTIME_IMAGE=$BUILDER_IMAGE
export OUTPUT_IMAGE="camel-quarkus-order-processor-api"
export INCREMENTAL=true
export RUNTIME_ARTIFACT="/deployments/"

buildah_s2i.sh
#buildah_s2i_runtime.sh
