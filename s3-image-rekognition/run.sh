#!/bin/sh
set -e

podman run --rm -ti -p 8082:8080 \
 -e DUMMY_MODE="false" \
 -e AWS_REGION="eu-central-1" \
 -e AWS_ACCESS_KEY_ID="AKIAUUQQLNILUI4RO7KB" \
 -e AWS_SECRET_ACCESS_KEY="577YXBuRKQmx67ox4jSGJzWZfgLiOIBUHdIzDIf+" \
 localhost/s3-image-rekognition
