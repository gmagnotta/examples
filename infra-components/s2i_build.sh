#!/usr/bin/env bash
#
# This script emulates an s2i build process performed exclusively with buildah.
# Currently only builder images are supported.
#
# Version 0.0.4
#
set -e

BUILDER_IMAGE=""
ASSEMBLE_USER="jboss"
SCRIPTS_URL="/usr/local/s2i/"
OUTPUT_IMAGE=""
INCREMENTAL=true
CONTEXT_DIR="."

echo "Start"
builder=$(buildah from $BUILDER_IMAGE)

buildah add --chown $ASSEMBLE_USER:0 $builder ./$CONTEXT_DIR /tmp/src

if [ "$INCREMENTAL" = "true" ]; then

    if [ -f "./artifacts.tar" ]; then
        echo "Restoring artifacts"
        buildah add --chown $ASSEMBLE_USER:0 $builder ./artifacts.tar /tmp/artifacts
    fi

fi

ENV=""
if [ -f "$CONTEXT_DIR/.s2i/environment" ]; then

    while IFS="" read -r line
    do
      [[ "$line" =~ ^#.*$ ]] && continue
      ENV+="-e $line "
    done < $CONTEXT_DIR/.s2i/environment

    echo "ENV is $ENV"

fi

buildah config --cmd $SCRIPTS_URL/run $builder

if [ -x "$CONTEXT_DIR/.s2i/bin/assemble" ]; then
    echo "Using assemble from .s2i"
    eval buildah run $ENV $builder -- /tmp/src/.s2i/bin/assemble
else
    echo "Using assemble from image"
    eval buildah run $ENV $builder -- $SCRIPTS_URL/assemble
fi

if [ "$INCREMENTAL" = "true" ]; then

    echo "Saving artifacts"
    if [ -f "./artifacts.tar" ]; then
        rm ./artifacts.tar
    fi

    buildah run $builder -- /bin/bash -c "if [ -x \"$SCRIPTS_URL/save-artifacts\" ]; then $SCRIPTS_URL/save-artifacts ; fi" > ./artifacts.tar

fi

buildah commit $builder $OUTPUT_IMAGE

buildah rm $builder
