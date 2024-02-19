#!/bin/sh
set -e

export BUILDER_IMAGE="registry.redhat.io/jboss-webserver-6/jws60-openjdk17-openshift-rhel8@sha256:8ff2a1c968d3cca627e31e2cb9c336dafa651fe2d29cb64827f6cf6e877b6ab3"
export OUTPUT_IMAGE="hello-tomcat-artifacts"
export INCREMENTAL=true

buildah_s2i.sh

export RUNTIME_IMAGE=$BUILDER_IMAGE
export OUTPUT_IMAGE="hello-tomcat"
export SOURCE_IMAGE="hello-tomcat-artifacts"
export SRC_ARTIFACT="/deployments/"
export DESTINATION_URL=$SRC_ARTIFACT

buildah_s2i_runtime.sh
