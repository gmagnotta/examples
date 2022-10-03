package org.gmagnotta.model;

public class Item {

    // {"id":0,"description":"Bacon King","price":"ZA=="}
    
    public long id;
    public String description;
    public String price;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String toString() {
        return "Item: {id " + id + " description " + description + " price " + price + "}";
    }

}
