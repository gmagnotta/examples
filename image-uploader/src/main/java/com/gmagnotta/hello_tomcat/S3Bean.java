package com.gmagnotta.hello_tomcat;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import io.minio.Result;
import io.minio.messages.Item;

@Named
@RequestScoped
public class S3Bean {

    private Logger LOGGER = Logger.getLogger(UploadFileBean.class.getName());

    private final int BUFFER_SIZE = 1048;

    private S3Util s3Util;

    private Iterable<Result<Item>> results;

    public S3Bean() {

        results = new ArrayList<>();

        String endpoint = System.getenv("ENDPOINT");
        String accessKey = System.getenv("ACCESSKEY");
        String secretKey = System.getenv("SECRETKEY");

        s3Util = new S3Util(endpoint, accessKey, secretKey);

    }

    public Iterable<Result<Item>> getResults() {
        return results;
    }

    public void reloadFileList() {

        String bucket = System.getenv("BUCKET");
        LOGGER.info("Reading bucket content");
        results = s3Util.listBucket(bucket);
    }

    public void downloadFile() throws Exception {

        FacesContext context = FacesContext.getCurrentInstance();

        Map<String, String> requestParams = context.getExternalContext().getRequestParameterMap();

        String fileName = (String) requestParams.get("filename");
        LOGGER.info("Requested filename " + fileName);

        String bucket = System.getenv("BUCKET");

        try (InputStream in = s3Util.getFile(bucket, fileName);
                OutputStream out = context.getExternalContext().getResponseOutputStream()) {

            context.getExternalContext().setResponseContentType("application/octet-stream");
            context.getExternalContext().setResponseHeader("Content-disposition",
                    "attachment; filename=\"" + fileName + "\"");

            byte[] buffer = new byte[BUFFER_SIZE];

            int numBytesRead;
            while ((numBytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, numBytesRead);
            }

            context.responseComplete();
        } catch (Exception ex) {
            
            LOGGER.log(Level.SEVERE, "Error downloading file", ex);

            throw ex;

        }

    }

}
