package org.gmagnotta.service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.cloudevents.CloudEvent;

@Path("/")
@RegisterRestClient(configKey="broker-api")
public interface BrokerService {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    Response send(CloudEvent payload);
}
