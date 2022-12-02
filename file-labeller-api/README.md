# file-labeller-api Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

The purpose of this project is to collect all the labels for files uploaded
in a aws S3 bucket.

The application receives CloudEvents from other services that perform scans on
those files and collect the information inside a RDBMS.

The application exposes also a REST API to search the labels for a particular
file.

You can use the following command to simulate a CloudEvent

curl -v http://localhost:8080 \
  -H "Content-Type: application/json" \
  -H "Ce-Id: foo-1" \
  -H "Ce-Specversion: 1.0" \
  -H "Ce-Type: com.gmagnotta.events/filelabel" \
  -H "Ce-Source: cloud-event-labeller" \
  -d '{"name":"file.jpg","labels":["firstlabel", "secondlabel"]}'

You can use the following command to perform a search of the labels

curl -X GET http://localhost:8080/file/file.jpg