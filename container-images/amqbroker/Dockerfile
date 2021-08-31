FROM registry.access.redhat.com/ubi8/openjdk-8-runtime 

USER root

EXPOSE 61616
EXPOSE 8161

COPY amq-broker-7.8.2-bin.zip /tmp/

RUN microdnf install -y unzip && \
    mkdir /opt/amq-broker && \
    mkdir /tmp/amq-broker && \
    unzip /tmp/amq-broker-7.8.2-bin.zip -d /tmp/amq-broker && \
    mv /tmp/amq-broker/amq-broker-7.8.2/* /opt/amq-broker && \

    groupadd amq-broker && \
    useradd -g amq-broker amq-broker && \
    mkdir /var/opt/amq-broker && \
    chown -R amq-broker:0 /var/opt/amq-broker && \
    chmod 770 /var/opt/amq-broker && \

    microdnf clean all && [ ! -d /var/cache/yum ] || rm -rf /var/cache/yum && \
    rm -r /tmp/amq-broker

RUN ln -s /usr/lib/jvm/jre-1.8.0 /usr/lib/jvm/java-1.8.0

WORKDIR /var/opt/amq-broker

USER amq-broker

RUN /opt/amq-broker/bin/artemis create --user admin --password admin --role amq --allow-anonymous mybroker

USER root 

RUN mkdir /var/opt/amq-broker/mybroker/lock && \
    chown -R amq-broker:0 /var/opt/amq-broker && \
    chmod 770 /var/opt/amq-broker/mybroker/lock && \
    chmod 770 /var/opt/amq-broker/mybroker/data && \
    chmod 770 /var/opt/amq-broker/mybroker/log && \
    chmod 770 /var/opt/amq-broker/mybroker/tmp && \
    sed -i 's/localhost:8161/0.0.0.0:8161/' /var/opt/amq-broker/mybroker/etc/bootstrap.xml && \
    sed -i 's/localhost//' /var/opt/amq-broker/mybroker/etc/jolokia-access.xml && \
    sed -i 's/-Dhawtio.disableProxy=true/-Dhawtio.disableProxy=false/' /var/opt/amq-broker/mybroker/etc/artemis.profile

USER amq-broker

ENTRYPOINT /var/opt/amq-broker/mybroker/bin/artemis run
