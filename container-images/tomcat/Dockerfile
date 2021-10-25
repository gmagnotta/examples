FROM registry.access.redhat.com/ubi8/openjdk-11

EXPOSE 8080

USER root

COPY jws-5.5.0-application-server.zip /tmp/
COPY jws-5.5.1-application-server.zip /tmp/

RUN microdnf install -y unzip && \
    mkdir /opt/jws && \
    unzip /tmp/jws-5.5.0-application-server.zip -d /tmp/jws && \
    unzip -o /tmp/jws-5.5.1-application-server.zip -d /tmp/jws && \
    mv /tmp/jws/jws-5.5/* /opt/jws/ && \

#    groupadd tomcat && \
#    useradd -g tomcat tomcat && \
    chown -R jboss:0 /opt/jws && \
    chmod 770 /opt/jws && \
    chmod 770 /opt/jws/tomcat/logs && \

    microdnf clean all && [ ! -d /var/cache/yum ] || rm -rf /var/cache/yum && \
    rm -r /tmp/jws

USER jboss

WORKDIR /opt/jws

ENTRYPOINT ["/opt/jws/tomcat/bin/catalina.sh", "run"]
