#!/usr/bin/env bash
set -e

CUSTOM_INSTALL_DIRECTORIES=extensions
GALLEON_PROVISION_LAYERS=""
GALLEON_PROVISION_DEFAULT_FAT_SERVER=true


INCREMENTAL=true

#ENVIRONMENTS="CUSTOM_INSTALL_DIRECTORIES,GALLEON_PROVISION_DEFAULT_FAT_SERVER"
ENVIRONMENTS="CUSTOM_INSTALL_DIRECTORIES"


builder=$(buildah from registry.redhat.io/jboss-eap-7/eap74-openjdk11-openshift-rhel8)

buildah add --chown jboss:0 $builder ./ /tmp/src

if [ "$INCREMENTAL" = "true" ]; then

    if [ -f "./artifacts.tar" ]; then
    echo "restoring artifacts"
    buildah add --chown jboss:0 $builder ./artifacts.tar /tmp/artifacts
    fi

fi

COMMAND=""

if [ -n "$ENVIRONMENTS" ]; then

    COMMAND+="buildah config "

    IFS=','; for word in $ENVIRONMENTS; do COMMAND+="--env $word=${!word} "; done

    COMMAND+='$builder'

fi

if [ ! -z "$COMMAND" ]; then
    echo "executing $COMMAND"

    eval "$COMMAND"
fi

buildah config --cmd /usr/local/s2i/run $builder

if [ -x ".s2i/bin/assemble" ]; then
    echo "Using assemble from .s2i"
    buildah run $builder -- /tmp/src/.s2i/bin/assemble
else
    buildah run $builder -- /usr/local/s2i/assemble
fi

if [ "$INCREMENTAL" = "true" ]; then

    echo "saving artifacts"
    if [ -f "./artifacts.tar" ]; then
    rm ./artifacts.tar
    fi

    buildah run $builder -- /bin/bash -c 'if [ -x "/usr/local/s2i/save-artifacts" ]; then /usr/local/s2i/save-artifacts ; fi' > ./artifacts.tar

fi

buildah commit $builder myjbosseap

buildah rm $builder

# Build lightweight image
#runner=$(buildah from registry.redhat.io/jboss-eap-7/eap74-openjdk11-runtime-openshift-rhel8)

#buildah copy --from $builder $runner /s2i-output/server /opt/eap

#buildah run --user 0:0 $runner -- /bin/bash -c 'chown -R jboss:root /opt/eap && chmod -R ug+rwX /opt/eap'

#buildah config --user jboss --cmd /opt/eap/bin/openshift-launch.sh $runner

#buildah commit $runner myjbosseap