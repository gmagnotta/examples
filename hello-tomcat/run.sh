#!/usr/bin/env bash
set -e

podman run --name tomcat --rm -ti -p 8081:8080 \
    -v ./tomcat-users.xml:/opt/jws-6.0/tomcat/conf/tomcat-users.xml:z \
    localhost/hello-tomcat
