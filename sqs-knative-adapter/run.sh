#!/bin/sh
set -e

podman run --rm -ti -p 8080:8080 \
 -e K_SINK=http://192.168.1.104:8082 \
 -e AWS_ACCESS_KEY_ID="AKIAUUQQLNILUI4RO7KB" \
 -e AWS_SECRET_ACCESS_KEY="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" \
 -e SQS_QUEUE=arn:aws:sqs:eu-central-1:318935624215:peppesan-events \
 localhost/sqs-knative-adapter
