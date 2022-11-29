package com.gmagnotta.hello_tomcat;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.Part;


@Named
@ViewScoped
public class UploadFileBean implements Serializable {

    private Logger LOGGER = Logger.getLogger(UploadFileBean.class.getName());
    
    private Part uploadedFile;

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

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error uploading file", ex);

            throw ex;
        }

        return "s3explorer?faces-redirect=true";
    }

}
