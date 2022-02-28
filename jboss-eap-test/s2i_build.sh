#!/usr/bin/env bash
#
# This script emulates an s2i build process performed exclusively with buildah.
# Currently only builder images are supported.
#
# Version 0.0.1
#
set -e

BUILDER_IMAGE="registry.redhat.io/jboss-eap-7/eap74-openjdk11-openshift-rhel8"
ASSEMBLE_USER="jboss"
SCRIPTS_URL="/usr/local/s2i/"
OUTPUT_IMAGE="jboss-test"
INCREMENTAL=true
CONTEXT_DIR="."

#Define ENV variables that you want to inject, and list in ENVIRONMENTS separated by comma
CUSTOM_INSTALL_DIRECTORIES=extensions
GALLEON_PROVISION_LAYERS=""
GALLEON_PROVISION_DEFAULT_FAT_SERVER=true
ENVIRONMENTS="CUSTOM_INSTALL_DIRECTORIES"

echo "Start"
builder=$(buildah from $BUILDER_IMAGE)

buildah add --chown $ASSEMBLE_USER:0 $builder ./$CONTEXT_DIR /tmp/src

if [ "$INCREMENTAL" = "true" ]; then

    if [ -f "./artifacts.tar" ]; then
        echo "Restoring artifacts"
        buildah add --chown $ASSEMBLE_USER:0 $builder ./artifacts.tar /tmp/artifacts
    fi

fi

COMMAND=""

if [ -n "$ENVIRONMENTS" ]; then

    COMMAND+="buildah config "

    IFS=','; for word in $ENVIRONMENTS; do COMMAND+="--env $word=${!word} "; done

    COMMAND+='$builder'

fi

if [ ! -z "$COMMAND" ]; then
    echo "Executing $COMMAND"

    eval "$COMMAND"
fi

buildah config --cmd $SCRIPTS_URL/run $builder

if [ -x "$CONTEXT_DIR/.s2i/bin/assemble" ]; then
    echo "Using assemble from .s2i"
    buildah run $builder -- /tmp/src/.s2i/bin/assemble
else
    echo "Using assemble from image"
    buildah run $builder -- $SCRIPTS_URL/assemble
fi

if [ "$INCREMENTAL" = "true" ]; then

    echo "Saving artifacts"
    if [ -f "./artifacts.tar" ]; then
        rm ./artifacts.tar
    fi

    buildah run $builder -- /bin/bash -c 'if [ -x "$SCRIPTS_URL/save-artifacts" ]; then $SCRIPTS_URL/save-artifacts ; fi' > ./artifacts.tar

fi

buildah commit $builder $OUTPUT_IMAGE

buildah rm $builder