#!/usr/bin/env bash
#
# This script emulates an s2i build process performed exclusively with buildah.
#
# Version 0.0.5
#
set -e

BUILDER_IMAGE=""
RUNTIME_IMAGE=""
RUNTIME_ARTIFACT="/deployments/"
ASSEMBLE_USER="jboss"
OUTPUT_IMAGE=""
INCREMENTAL=false
CONTEXT_DIR="."

if [ -f ".s2i/localconfig" ]; then
    . .s2i/localconfig
else
    echo "Not found .s2i/localconfig"
    exit -1
fi

SCRIPTS_URL=$(buildah inspect $BUILDER_IMAGE | jq '.Docker.config.Labels["io.openshift.s2i.scripts-url"]' | sed 's/image:\/\///g' | tr -d '"')
DESTINATION_URL=$(buildah inspect $BUILDER_IMAGE | jq '.Docker.config.Labels["io.openshift.s2i.destination"]' | tr -d '"')

echo "Start"
builder=$(buildah from --ulimit nofile=90000:90000 $BUILDER_IMAGE)

buildah add --chown $ASSEMBLE_USER:0 $builder ./$CONTEXT_DIR $DESTINATION_URL/src

if [ "$INCREMENTAL" = "true" ]; then

    if [ -f "./artifacts.tar" ]; then
        echo "Restoring artifacts"
        buildah add --chown $ASSEMBLE_USER:0 $builder ./artifacts.tar $DESTINATION_URL/artifacts
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
    eval buildah run $ENV $builder -- $DESTINATION_URL/src/.s2i/bin/assemble
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

# RUNTIME IMAGE BUILD
if [ ! -z "$RUNTIME_IMAGE" ]; then

    echo "Creating Runtime Image"

    runner=$(buildah from $RUNTIME_IMAGE)

    buildah copy --chown $ASSEMBLE_USER:0 --from $builder $runner $RUNTIME_ARTIFACT $RUNTIME_ARTIFACT

    buildah commit $runner $OUTPUT_IMAGE

    buildah rm $runner

else

    echo "Not creating runtime image"

    buildah commit $builder $OUTPUT_IMAGE

fi

buildah rm $builder
