FROM registry.access.redhat.com/ubi8

USER root

EXPOSE 61616

RUN yum update -y && \
    yum install -y unzip java-1.8.0-openjdk-devel

COPY amq-broker-7.8.1-bin.zip /tmp/

RUN mkdir /opt/amq-broker && \
    mkdir /tmp/amq-broker && \
    unzip /tmp/amq-broker-7.8.1-bin.zip -d /tmp/amq-broker && \
    mv /tmp/amq-broker/amq-broker-7.8.1/* /opt/amq-broker

RUN groupadd amq-broker && \
    useradd -g amq-broker amq-broker && \
    mkdir /var/opt/amq-broker && \
    chown -R amq-broker:0 /var/opt/amq-broker && \
    chmod 770 /var/opt/amq-broker

RUN yum clean all && [ ! -d /var/cache/yum ] || rm -rf /var/cache/yum && rm -r /tmp/amq-broker

WORKDIR /var/opt/amq-broker

USER amq-broker

RUN /opt/amq-broker/bin/artemis create --user admin --password admin --role amq --allow-anonymous mybroker

USER root 

RUN mkdir /var/opt/amq-broker/mybroker/lock && \
    chown -R amq-broker:0 /var/opt/amq-broker && \
    chmod 770 /var/opt/amq-broker/mybroker/lock && \
    chmod 770 /var/opt/amq-broker/mybroker/data && \
    chmod 770 /var/opt/amq-broker/mybroker/log && \
    chmod 770 /var/opt/amq-broker/mybroker/tmp

USER amq-broker

ENTRYPOINT /var/opt/amq-broker/mybroker/bin/artemis run
