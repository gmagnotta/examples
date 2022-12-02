#!/bin/sh

export BUILDER_IMAGE="registry.redhat.io/jboss-webserver-5/jws56-openjdk11-openshift-rhel8"
export OUTPUT_IMAGE="hello-tomcat"
export INCREMENTAL=true
export RUNTIME_IMAGE=$BUILDER_IMAGE
export RUNTIME_ARTIFACT="/deployments/hello-tomcat.war"
export RUNTIME_DEST_ARTIFACT="/deployments/ROOT.war"

../infra-components/s2i_build.sh
