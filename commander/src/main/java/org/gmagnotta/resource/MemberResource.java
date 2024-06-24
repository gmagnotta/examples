package org.gmagnotta.resource;

import java.util.List;

import org.gmagnotta.model.Battalion;
import org.gmagnotta.model.Member;
import org.gmagnotta.service.BattalionService;
import org.gmagnotta.service.MemberService;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/member")
public class MemberResource {

    @Inject
    MemberService memberService;

    @Inject
    BattalionService battalionService;

    @Inject
    EntityManager em;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Member> getAll() {
        return memberService.getAll();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Member getById(@PathParam(value = "id") long id) {
        return memberService.getById(id);
    }

    @POST
    @Path("/{id}")
    @Transactional
    @Produces(MediaType.TEXT_PLAIN)
    public Response createMember(Member member, @PathParam(value = "id") long id) {
        Battalion battalion = battalionService.getById(id);
        
        if (battalion == null) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), "Battalion not foud").build();
        }

        member.battalion = battalion;

        memberService.createMember(member);

        return Response.status(Response.Status.CREATED).build();

    }
}
