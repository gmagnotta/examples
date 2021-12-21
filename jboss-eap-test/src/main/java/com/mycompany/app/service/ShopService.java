package com.mycompany.app.service;


import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mycompany.model.Shop;

/**
 * Hello world!
 *
 */
@Stateless
@Path("shop")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ShopService 
{
    @PersistenceContext(unitName = "store")
	private EntityManager entityManager;
    
    @GET
    public List<Shop> getShops() {
    	
    	Query query = entityManager.createNamedQuery("getAllShops", Shop.class);
    	
    	return query.getResultList();
    }
    
    public void createShop(Shop shop) {
    	
    	entityManager.persist(shop);
    	
    }
    
    public void deleteShop(int id) {
    	
    	Shop shop = entityManager.find(Shop.class, id);
    	
    	entityManager.remove(shop);
    }
    
}
