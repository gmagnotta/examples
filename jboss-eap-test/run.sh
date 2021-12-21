#!/usr/bin/env bash
set -e

podman run --rm -ti -p 8080:8080 \
-e DB_SERVICE_PREFIX_MAPPING=ocp-postgresql=DS1 \
-e OCP_POSTGRESQL_SERVICE_HOST=192.168.3.225 \
-e OCP_POSTGRESQL_SERVICE_PORT=5432 \
-e DS1_CONNECTION_CHECKER=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker \
-e DS1_DATABASE=db \
-e DS1_DRIVER=postgresql \
-e DS1_EXCEPTION_SORTER=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter \
-e DS1_USERNAME=user \
-e DS1_PASSWORD=pass \
-e DS1_MAX_POOL_SIZE=20 \
-e DS1_MIN_POOL_SIZE=20 \
-e DS1_NONXA=true \
-e DS1_DATABASE=db \
-e DS1_USERNAME=user \
-e DS1_PASSWORD=pass \
myjbosseap