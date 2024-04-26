#!/bin/sh

set -e

export BUILDER_IMAGE="registry.access.redhat.com/ubi8/openjdk-11"
export OUTPUT_IMAGE="quarkus-gps-artifacts"
export INCREMENTAL=true

buildah_s2i.sh

export RUNTIME_IMAGE="openjdk11_raspi1b"
export OUTPUT_IMAGE="quarkus-gps"
export SOURCE_IMAGE="quarkus-gps-artifacts"
export SRC_ARTIFACT="/deployments/"
export DESTINATION_URL="/deployments/"
export RUNTIME_CMD="'[\"-jar\", \"/deployments/quarkus-run.jar\"]'"

buildah_s2i_runtime.sh
