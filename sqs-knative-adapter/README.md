# sqs-knative-adapter Project

This project is an adapter for AWS SQS that encapsulates the message retrieved from
the queue inside a CloudEvent and push it via HTTP POST to a destination.

The original idea was to be used as a Knative ContainerSource, but it's generic
and can work also as standalone.

## How to use

The application expects the following ENVIRONMENT VARIABLES:

* K_SINK this will be injected by Knative and contains the URL that the application will use to perform the HTTP POST
* AWS_ACCESS_KEY_ID the access key id that the applicaiton will use to authenticate against AWS SQS
* AWS_SECRET_ACCESS_KEY the access key that the applicaiton will use to authenticate against AWS SQS
* SQS_QUEUE the queue or arn that the application will use to fetch messages from AWS SQS

## How to test

while true ; do  echo -e "HTTP/1.1 200 OK\n\n" | nc -l -p 8082  ; done

## What is sent

Records are json as this one:

{"Records":[{"eventVersion":"2.1","eventSource":"aws:s3","awsRegion":"eu-central-1","eventTime":"2022-12-09T10:10:36.193Z","eventName":"ObjectCreated:Put","userIdentity":{"principalId":"A2SMOCNU6QZ1YS"},"requestParameters":{"sourceIPAddress":"93.38.123.106"},"responseElements":{"x-amz-request-id":"24XDMK74D2Y6FJEP","x-amz-id-2":"Cej+DXOVLagOh/3B6RaxNLt3XZ/O/HbPPfZ+ELBx2/1jl1/gGS8zLxhvopfoLO80TdElu5HzilR6XGPgyVla2o0niUIIGjhM"},"s3":{"s3SchemaVersion":"1.0","configurationId":"Send create object","bucket":{"name":"peppesan","ownerIdentity":{"principalId":"A2SMOCNU6QZ1YS"},"arn":"arn:aws:s3:::peppesan"},"object":{"key":"backupToExternal.sh","size":594,"eTag":"a05ea8e27a0fd1ef867f87976e1d3eda","sequencer":"006393099C24447605"}}}]}