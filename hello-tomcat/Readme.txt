Example project to show how to build and deploy a simple java web application


How to install:

oc create configmap template --from-file=hello-tomcat-template.yaml

oc apply -f ../infra-components/pipelines-common.yaml

oc process -f hello-tomcat-pipeline.yaml | oc apply -f -
