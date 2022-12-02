# cloud-event-logger Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

The purpose of this project is to receive events from AWS S3 when an image is uploaded
and to call AWS rekognition to extract label from the image.

A CloudEvent is then generated and delivered containing the labels extracted from the image

You could use the following command to perform a test

curl -v http://localhost:8080 \
  -H "Content-Type: application/json" \
  -H "Ce-Id: foo-1" \
  -H "Ce-Specversion: 1.0" \
  -H "Ce-Type: com.gmagnotta.events/s3upload" \
  -H "Ce-Source: image-uploader" \
  -d '{"bucket":"mybucket","objectKey":"Image.jpg"}'

kn trigger create s3-image-rekognition-serverless --filter type=com.gmagnotta.events/s3upload --sink s3-image-rekognition-serverless
