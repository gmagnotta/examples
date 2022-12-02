package com.gmagnotta.hello_tomcat.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.gmagnotta.hello_tomcat.model.S3UploadedObject;

@Path("/")
public interface BrokerService {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Response send(S3UploadedObject payload);
}
