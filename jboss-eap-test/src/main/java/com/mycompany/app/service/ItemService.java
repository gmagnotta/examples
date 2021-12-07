package com.mycompany.app.service;


import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mycompany.model.Item;

@Stateless
@Path("item")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ItemService 
{
    @PersistenceContext(unitName = "my-persistence-unit")
	private EntityManager entityManager;
    
    @POST
    public void createItem(Item item) {
    	
    	entityManager.persist(item);
    	
    }
    
    @GET
    public List<Item> getItems() {
    	Query query = entityManager.createNamedQuery("getAllItems", Item.class);
    	
    	return query.getResultList();
    }
    
    public List<Item> getItemsByDescription(String description) {
    	Query query = entityManager.createNamedQuery("getItemsByDescription", Item.class);
    	query.setParameter("desc", description);
    	
    	return query.getResultList();
    }
    
    @GET
    @Path("{id}")
    public Item getItem(@PathParam("id") int id) {
    
    	return entityManager.find(Item.class, id);
    	
    }
    
    @PUT
    public void updateItem(Item item) {
    	entityManager.merge(item);
    }
    
    @DELETE
    public void deleteItem(Item item) {
    	entityManager.remove(entityManager.contains(item) ? item : entityManager.merge(item));
    }
    
}
