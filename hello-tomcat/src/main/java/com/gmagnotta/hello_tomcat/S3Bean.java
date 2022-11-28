package com.gmagnotta.hello_tomcat;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import io.minio.Result;
import io.minio.messages.Item;


@Named
@RequestScoped
public class S3Bean implements Serializable {

    private Logger LOGGER = Logger.getLogger(UploadFileBean.class.getName());
    
    private Iterable<Result<Item>> results;

    public Iterable<Result<Item>> getResults() {
        return results;
    }

    public S3Bean() throws IOException {

        results = new ArrayList<>();

        try {
            LOGGER.info("Reading bucket content");

            String endpoint = System.getenv("ENDPOINT");
            String accessKey = System.getenv("ACCESSKEY");
            String secretKey = System.getenv("SECRETKEY");
            String bucket = System.getenv("BUCKET");

            S3Util s3Util = new S3Util(endpoint, accessKey, secretKey);

            results = s3Util.listBucket(bucket);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error uploading file", ex);
        }

    }

}
