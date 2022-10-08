# Example project to show how to build and deploy a simple java web application with OpenShift Container Platform

---
How to use:

```
oc new-project hello-test

oc apply -f hello-tomcat-edit-from-hello.yaml

oc new-project hello-dev

oc create configmap deploy-template --from-file=hello-tomcat-template.yaml

oc apply -f ../infra-components/pipelines-common.yaml

oc process -f hello-tomcat-pipeline.yaml | oc apply -f -
```