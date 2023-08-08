# DOWNLOAD DB
FROM docker.io/aquasec/trivy as db

RUN trivy image --download-db-only

# DOWNLOAD DB
FROM docker.io/aquasec/trivy as javadb

RUN trivy image --download-java-db-only


# RUNTIME IMAGE
FROM docker.io/aquasec/trivy

USER root

COPY --from=db /root/.cache/trivy/db /cache/trivy/db/
COPY --from=javadb /root/.cache/trivy/java-db /cache/trivy/java-db/

RUN chown -R 0:0 /cache/ && \
    chmod 0777 -R /cache