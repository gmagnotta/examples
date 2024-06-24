#!/bin/sh

export BUILDER_IMAGE="registry.access.redhat.com/ubi8/openjdk-17"
export OUTPUT_IMAGE="commander"
export INCREMENTAL=true
#export RUNTIME_IMAGE=$BUILDER_IMAGE
#export RUNTIME_ARTIFACT="/deployments/"

buildah_s2i.sh
