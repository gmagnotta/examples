# RUN IMAGE
FROM quay.io/redhat_emp1/jbossws

USER root

RUN rm -rf /opt/jws/tomcat/webapps/*

COPY target/quarkus-order-processor-ui.war /opt/jws/tomcat/webapps/ROOT.war