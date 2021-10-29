FROM registry.access.redhat.com/ubi8

USER root

EXPOSE 9090

COPY prometheus-2.30.2.linux-amd64.tar.gz /tmp/

RUN mkdir /opt/prometheus && \
    mkdir /tmp/prometheus && \
    tar xzvf /tmp/prometheus-2.30.2.linux-amd64.tar.gz -C /tmp/prometheus && \
    mv /tmp/prometheus/prometheus-2.30.2.linux-amd64/* /opt/prometheus && \

    groupadd prometheus && \
    useradd -g prometheus prometheus && \
    mkdir /opt/prometheus/data && \
    chown -R prometheus:0 /opt/prometheus && \
    chmod 770 /opt/prometheus/data && \

    rm -r /tmp/prometheus

WORKDIR /opt/prometheus

USER prometheus

ENTRYPOINT ["/opt/prometheus/prometheus"]

CMD ["--config.file=prometheus.yml"] 
