package com.gmagnotta.hello_tomcat.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gmagnotta.hello_tomcat.model.LabelledFile;

@Path("/file")
public interface FileLabellerService {

    @GET
    @Path("/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public LabelledFile getFileByName(@PathParam("name") String name);

}
