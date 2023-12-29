#!/bin/sh

set -e

export BUILDER_IMAGE="registry.access.redhat.com/ubi8/openjdk-11"
export OUTPUT_IMAGE="s3-image-rekognition"
export INCREMENTAL=true
#export RUNTIME_IMAGE=$BUILDER_IMAGE
#export RUNTIME_ARTIFACT="/deployments/"

buildah_s2i.sh
