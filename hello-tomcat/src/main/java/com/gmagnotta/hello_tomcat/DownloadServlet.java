package com.gmagnotta.hello_tomcat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/secured/download")
public class DownloadServlet extends HttpServlet {

    private final int BUFFER_SIZE = 1048;

    private Logger LOGGER = Logger.getLogger(DownloadServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
      throws ServletException, IOException {

        String fileName = (String) req.getParameter("filename");
        LOGGER.info("Read filename " + fileName);

        String endpoint = System.getenv("ENDPOINT");
        String accessKey = System.getenv("ACCESSKEY");
        String secretKey = System.getenv("SECRETKEY");
        String bucket = System.getenv("BUCKET");

        S3Util s3Util = new S3Util(endpoint, accessKey, secretKey);
    
        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");

        try(InputStream in = s3Util.getFile(bucket, fileName);
          OutputStream out = resp.getOutputStream()) {

            byte[] buffer = new byte[BUFFER_SIZE];
        
            int numBytesRead;
            while ((numBytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, numBytesRead);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error downloading file", ex);
        }
    }
}
