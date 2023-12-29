#!/usr/bin/env bash
set -e

podman run --rm -ti -p 8081:8080 \
    -e BACKEND_URL="http://192.168.1.227:8082" \
    -e ENDPOINT="https://s3.amazonaws.com" \
    -e BUCKET="peppesan" \
    -e ACCESSKEY="AKIAUUQQLNILUI4RO7KB" \
    -e SECRETKEY="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" \
    -v ./tomcat-users.xml:/opt/jws-5.6/tomcat/conf/tomcat-users.xml \
    localhost/image-uploader
