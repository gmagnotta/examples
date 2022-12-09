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