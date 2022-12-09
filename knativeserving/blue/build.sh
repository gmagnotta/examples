#!/bin/sh
set -e

export BUILDER_IMAGE="registry.redhat.io/rhel8/httpd-24"
export OUTPUT_IMAGE="webapp"
export INCREMENTAL=false
export ASSEMBLE_USER=1001

../../infra-components/s2i_build.sh
