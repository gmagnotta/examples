package com.gmagnotta.hello_tomcat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;


@Named
@RequestScoped
public class UploadFileBean {

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

        LOGGER.info("Received file " + fileName);

        try {

            LOGGER.info("Running detection against " + fileName);
            String result = DetectObjects.detect("/opt/jws-5.6/tomcat/models/ssd_inception_v2_coco_2017_11_17/saved_model", "/labels/mscoco_label_map.pbtxt", uploadedFile.getInputStream());

            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse)context.getExternalContext().getResponse();
        
            int read = 0;
            byte[] bytes = new byte[1024];
        
            //String fileName = "test.txt";
            response.setContentType("text/plain");
        
            //response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
        
            OutputStream os = null;
        
            ByteArrayInputStream bis1;
            try {
                bis1 = new ByteArrayInputStream(result.getBytes("UTF-8"));
        
                os = response.getOutputStream();
        
                while ((read = bis1.read(bytes)) != -1) {
                    os.write(bytes, 0, read);
                }
        
                os.flush();
                os.close();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        
            FacesContext.getCurrentInstance().responseComplete();
        } catch (Exception ex) {

            LOGGER.log(Level.SEVERE, "Error uploading file", ex);

            throw ex;
        }

        return "";
    }

}
