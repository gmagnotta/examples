#!/bin/sh
set -e

export BUILDER_IMAGE="registry.redhat.io/jboss-webserver-5/jws57-openjdk11-openshift-rhel8"
export OUTPUT_IMAGE="hello-tomcat"
export INCREMENTAL=true
#export RUNTIME_IMAGE=$BUILDER_IMAGE
#export RUNTIME_ARTIFACT="/deployments/hello-tomcat.war"
##export RUNTIME_DEST_ARTIFACT="/deployments/ROOT.war"

buildah_s2i.sh
