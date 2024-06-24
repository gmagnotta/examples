package org.gmagnotta.service;

import java.util.List;

import org.gmagnotta.model.Member;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class MemberService {
    
    @Inject
    EntityManager em;

    public List<Member> getAll() {
        return em.createNamedQuery("Members.findAll", Member.class)
                .getResultList();
    }

    public Member getById(long id){
        return em.find(Member.class, id);
    }

    public void createMember(Member member) {
        em.persist(member);
    }

}
