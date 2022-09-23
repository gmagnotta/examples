package org.gmagnotta.model.connect;

import java.io.Serializable;

public class Order implements Serializable {

    public long id;
    public String amount;
    public String username;
    public long creation_date;

    //private io.debezium.time.MicroTimestamp creation_date;
    // {"id":6635,"amount":"DhA=","creation_date":1663436687117000,"username":"ANONYMOUS"}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(long creation_date) {
        this.creation_date = creation_date;
    }

    public String toString() {

        return "Order {id " + id + " username " + username + " amount " + amount.toString() + " creation_date " + creation_date + "}";
    }
    
}
