package com.gmagnotta.hello_tomcat;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import org.apache.cxf.jaxrs.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmagnotta.hello_tomcat.model.S3UploadedObject;
import com.gmagnotta.hello_tomcat.service.BrokerService;


@Named
@RequestScoped
public class UploadFileBean {

    private Logger LOGGER = Logger.getLogger(UploadFileBean.class.getName());
    
    private Part uploadedFile;

    @Inject
    private BrokerService brokerService;

    public Part getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(Part uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public String action() throws Exception {

        String fileName = uploadedFile.getSubmittedFileName();

        //String tmpdir = System.getProperty("java.io.tmpdir");

        //String uploadedPath = tmpdir + File.separator + fileName;

        //uploadedFile.write(uploadedPath);
        
        //LOGGER.info("Received file " + uploadedPath);

        try {
            LOGGER.info("Writing file to S3");

            String endpoint = System.getenv("ENDPOINT");
            String accessKey = System.getenv("ACCESSKEY");
            String secretKey = System.getenv("SECRETKEY");
            String bucket = System.getenv("BUCKET");

            S3Util s3Util = new S3Util(endpoint, accessKey, secretKey);

            s3Util.uploadFile(bucket, fileName, uploadedFile.getInputStream(), uploadedFile.getContentType());

            LOGGER.info("File written to S3");

            ObjectMapper objectMapper = new ObjectMapper();

            S3UploadedObject s3UploadedObject = new S3UploadedObject();
            s3UploadedObject.bucket = bucket;
            s3UploadedObject.objectKey = fileName;

            // Send
            LOGGER.info("Sending CloudEvent " + objectMapper.writeValueAsString(s3UploadedObject));

            WebClient.client(brokerService).header("ce-id", UUID.randomUUID().toString());
            WebClient.client(brokerService).header("ce-source", "image-uploader");
            WebClient.client(brokerService).header("ce-specversion", "1.0");
            WebClient.client(brokerService).header("ce-type", "com.gmagnotta.events/s3upload");

            brokerService.send(s3UploadedObject);

        } catch (Exception ex) {

            LOGGER.log(Level.SEVERE, "Error uploading file", ex);

            throw ex;
        }

        return "s3explorer?faces-redirect=true";
    }

}
