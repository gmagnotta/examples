package org.gmagnotta.service;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.cloudevents.CloudEvent;
import io.quarkus.test.Mock;

@Mock
@RestClient
@ApplicationScoped
public class MockBrokerService implements BrokerService {

    @Override
    public Response send(CloudEvent payload) {
        return Response.accepted().build();
    }
    
}
