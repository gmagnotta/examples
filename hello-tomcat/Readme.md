# Example project to show how to build and deploy a simple java web application with OpenShift Container Platform

---
How to use:

```
oc new-project hello-test

oc apply -f hello-tomcat-edit-from-hello.yaml

oc new-project hello-dev

oc create configmap deploy-template --from-file=hello-tomcat-template.yaml

oc process -f hello-tomcat-template-configs.yaml | oc apply -f -

oc apply -f ../infra-components/pipelines-common.yaml

oc process -f hello-tomcat-pipeline.yaml | oc apply -f -
```

You can also create a github webhook
```
oc apply -f hello-tomcat-trigger.yaml

```

Github should be configured as

payload url: 'http://el-listener-hello-dev.<OCP_DOMAIN>'

content type: application/json

secret: <secret_defined_in_hello-tomcat-trigger.yaml>