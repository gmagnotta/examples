package org.gmagnotta;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

import io.quarkus.security.identity.SecurityIdentity;

@Path("/api")
public class EchoResource {

    @Inject
    Logger logger;

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Path("/echo")
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    public User echo() {

        return new User(securityIdentity);

    }

    @GET
    @Path("/admin")
    @RolesAllowed("admin")
    @Produces(MediaType.TEXT_PLAIN)
    public String admin() {
        return "granted";
    }

    public static class User {

        private final String userName;

        User(SecurityIdentity securityIdentity) {
            this.userName = securityIdentity.getPrincipal().getName();
        }

        public String getUserName() {
            return userName;
        }
    }

}