#!/bin/sh
set -e

export BUILDER_IMAGE="registry.redhat.io/jboss-webserver-5/jws56-openjdk11-openshift-rhel8"
export OUTPUT_IMAGE="webapp-image-detection-artifacts"
export INCREMENTAL=true

buildah_s2i.sh

#export RUNTIME_IMAGE=$BUILDER_IMAGE
#export RUNTIME_ARTIFACT="/deployments/image-uploader.war"
#export RUNTIME_DEST_ARTIFACT="/deployments/ROOT.war"

export RUNTIME_IMAGE=$BUILDER_IMAGE
export OUTPUT_IMAGE="webapp-image-detection"
export SOURCE_IMAGE="webapp-image-detection-artifacts"
export SRC_ARTIFACT="/deployments/"
export DESTINATION_URL=$SRC_ARTIFACT
