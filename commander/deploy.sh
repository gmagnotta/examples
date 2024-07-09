#!/bin/sh

export CONTAINER_UTILS="/home/gmagnott/src/container-utils"
export CWD=`pwd`

export PROJECT="commander"
#export REGISTRY_USERNAME="user"
#export REGISTRY_PASSWORD="password"
export POSTGRESQL_DATABASE="commander"
export POSTGRESQL_USER="commander"
export POSTGRESQL_PASSWORD="commander"

set -e

ansible-playbook -e project=$PROJECT -e registry_username=$REGISTRY_USERNAME \
 -e registry_password=$REGISTRY_PASSWORD \
 $CONTAINER_UTILS/playbooks/playbook_initialize_project.yaml

ansible-playbook -e project=$PROJECT \
 -e postgresql_database=$POSTGRESQL_DATABASE \
 -e postgresql_user=$POSTGRESQL_USER \
 -e postgresql_password=$POSTGRESQL_PASSWORD \
 $CONTAINER_UTILS/playbooks/postgresql/playbook_provision_postgresql.yaml

ansible-playbook -e project=$PROJECT \
 -e postgresql_database=$POSTGRESQL_DATABASE \
 -e postgresql_user=$POSTGRESQL_USER \
 -e postgresql_file="$CWD/src/main/resources/schema.sql" \
 $CONTAINER_UTILS/playbooks/postgresql/playbook_run_sql_postgresql.yaml

ansible-playbook -e project=$PROJECT \
 -e postgresql_database=$POSTGRESQL_DATABASE \
 -e postgresql_user=$POSTGRESQL_USER \
 -e postgresql_file="$CWD/src/main/resources/import.sql" \
 $CONTAINER_UTILS/playbooks/postgresql/playbook_run_sql_postgresql.yaml

oc apply -k src/main/k8s/overlays/prod/