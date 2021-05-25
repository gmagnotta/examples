FROM registry.access.redhat.com/ubi8

EXPOSE 11222

USER root

RUN yum update && \
    yum install -y unzip java-1.8.0-openjdk-devel

COPY redhat-datagrid-8.1.0-server.zip /tmp/

RUN mkdir /opt/datagrid && \
    unzip /tmp/redhat-datagrid-8.1.0-server.zip -d /tmp/datagrid && \
    mv /tmp/datagrid/redhat-datagrid-8.1.0-server/* /opt/datagrid

RUN groupadd datagrid && \
    useradd -g datagrid datagrid && \
    chown -R datagrid:0 /opt/datagrid

USER datagrid

WORKDIR /opt/datagrid

RUN /opt/datagrid/bin/cli.sh user create admin -p "admin" -g supervisor,reader,writer

USER root

RUN chown -R datagrid:0 /opt/datagrid/server && \
    yum clean all && [ ! -d /var/cache/yum ] || rm -rf /var/cache/yum && rm -r /tmp/datagrid

ENTRYPOINT /opt/datagrid/bin/server.sh
