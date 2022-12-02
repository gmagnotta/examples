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



CREATE TABLE labelledfile
(
  id bigint NOT NULL,
  name character varying(255),
  CONSTRAINT labelledfile_pkey PRIMARY KEY (id)
)
;

CREATE TABLE labelledfile_labels
(
  labelledfile_id bigint NOT NULL,
  labels character varying(255),
  CONSTRAINT fkbmjeq1m4g42trg25l8l88t5kf FOREIGN KEY (labelledfile_id)
      REFERENCES public.labelledfile (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);


CREATE SEQUENCE hibernate_sequence
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

kn trigger create file-labeller-api-serverless --filter type=com.gmagnotta.events/filelabel --sink file-labeller-api-serverless