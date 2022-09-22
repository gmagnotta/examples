package org.gmagnotta.model.connect;

public class LineItem {

    public long id;
    public String price;
    public long quantity;
    public long item;
    public long ord;

    //{"id":6644,"price":"Alg=","quantity":5,"item":5,"ord":6643}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getItem() {
        return item;
    }

    public void setItem(long item) {
        this.item = item;
    }

    public long getOrd() {
        return ord;
    }

    public void setOrd(long ord) {
        this.ord = ord;
    }

    public String toString() {
        return "LineItem {id " + id + " quantity " + quantity + " item " + item + " order " + ord  + " price " + price + "}";
    }
    
}
