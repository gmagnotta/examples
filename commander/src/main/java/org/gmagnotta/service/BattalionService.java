package org.gmagnotta.service;

import java.util.List;

import org.gmagnotta.model.Battalion;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class BattalionService {
    
    @Inject
    EntityManager em; 

    public List<Battalion> getAll() {
        return em.createNamedQuery("Battalion.findAll", Battalion.class)
                .getResultList();
    }

    public List<Battalion> getByStatus(String status){
        return em.createNamedQuery("Battalion.findByStatus", Battalion.class)
                .setParameter("status",status)
                .getResultList();
    }


    public Battalion getById(long id){
        return em.find(Battalion.class, id);
    }

    public void updateBattalion(Battalion battalion) {
        em.merge(battalion);
    }

}
