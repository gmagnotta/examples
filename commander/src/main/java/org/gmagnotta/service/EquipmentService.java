package org.gmagnotta.service;

import java.util.List;

import org.gmagnotta.model.Equipment;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class EquipmentService {
    
    @Inject
    EntityManager em;

    public List<Equipment> getAll() {
        return em.createNamedQuery("Equipment.findAll", Equipment.class)
                .getResultList();
    }

    public Equipment getById(long id){
        return em.find(Equipment.class, id);
    }

    public void createEquipment(Equipment equipment) {
        em.persist(equipment);
    }

}
