# Example project to show how to build and deploy a simple java web application with OpenShift Container Platform, via OpenShift Pipelines

---
How to use:

```
oc new-project hello-dev

oc apply -f ../infra-components/pipelines-common.yaml

oc apply -f k8s/pipelines/hello-tomcat-pipeline.yaml

cosign generate-key-pair k8s://<mynamespace>/signing-secrets
```

You can also create a github webhook
```
oc apply -f k8s/pipelines/hello-tomcat-trigger.yaml

```

Github should be configured as

payload url: 'http://el-listener-hello-dev.<OCP_DOMAIN>'

content type: application/json

secret: <secret_defined_in_hello-tomcat-trigger.yaml>

To verify the signed image

cosign verify --key pubkey.key myregistry/myuser/myimage:tag