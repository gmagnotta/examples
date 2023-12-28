#!/usr/bin/env bash
set -e

podman run --rm -ti -p 8081:8080 \
    -v ./tomcat-users.xml:/opt/jws-5.7/tomcat/conf/tomcat-users.xml \
    localhost/hello-tomcat
