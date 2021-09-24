FROM registry.access.redhat.com/ubi8/openjdk-8-runtime

EXPOSE 11222

USER root

COPY redhat-datagrid-8.2.1-server.zip /tmp/
COPY infinispan-lon.xml /tmp/
COPY infinispan-nyc.xml /tmp/

RUN microdnf install -y unzip && \
    mkdir /opt/datagrid && \
    unzip /tmp/redhat-datagrid-8.2.1-server.zip -d /tmp/datagrid && \
    mv /tmp/datagrid/redhat-datagrid-8.2.1-server/* /opt/datagrid && \

    groupadd datagrid && \
    useradd -g datagrid datagrid && \
    chown -R datagrid:0 /opt/datagrid && \

    microdnf clean all && [ ! -d /var/cache/yum ] || rm -rf /var/cache/yum && \
    rm -r /tmp/datagrid && \

    ln -s /usr/lib/jvm/jre-1.8.0 /usr/lib/jvm/java-1.8.0

USER datagrid

WORKDIR /opt/datagrid

RUN /opt/datagrid/bin/cli.sh user create admin -p "admin" -g supervisor,reader,writer && \
    mkdir /opt/datagrid/server/log && \
    mkdir /opt/datagrid/server/data

USER root

RUN chown -R datagrid:0 /opt/datagrid && \
    chmod 770 /opt/datagrid/ && \
    chmod 770 /opt/datagrid/server/log && \
    chmod 770 /opt/datagrid/server/data && \
    cp /tmp/infinispan-lon.xml /opt/datagrid/server/conf/ && \
    cp /tmp/infinispan-nyc.xml /opt/datagrid/server/conf/ && \
    sed -i 's/127.0.0.1/0.0.0.0/' /opt/datagrid/server/conf/infinispan.xml

USER datagrid

ENTRYPOINT ["/opt/datagrid/bin/server.sh"]

CMD ["--cluster-stack=udp", "-Dinfinispan.cluster.name=London", "--server-config=infinispan.xml"]
