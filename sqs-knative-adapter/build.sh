#!/bin/sh

export BUILDER_IMAGE="registry.access.redhat.com/ubi8/openjdk-11"
export OUTPUT_IMAGE="sqs-knative-adapter"
export INCREMENTAL=true
export RUNTIME_IMAGE=$BUILDER_IMAGE
export RUNTIME_ARTIFACT="/deployments/"

../infra-components/s2i_build.sh
